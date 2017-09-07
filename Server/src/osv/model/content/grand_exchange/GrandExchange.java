package osv.model.content.grand_exchange;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

import osv.Server;
import osv.util.Stream;

public class GrandExchange {

	private static final Path IO_PATH = Paths.get("Data", "grand_exchange");

	private final ProductMap products = new ProductMap();

	private final Map<Integer, ProductHistory> history = new HashMap<>();

	private long nextUpdateTime = Server.getCalendar().getMillisecondsRemaining(0, 0, 0);
	/**
	 * Thread safe Queue of Products which should be added to the mapping.
	 */
	private final Queue<Product> productQueue = new ConcurrentLinkedQueue<>();

	/**
	 * Adds a new item to the queue.
	 * 
	 * @param product The item which is to be added.
	 */
	public void add(Product product) {
		productQueue.add(product);
	}

	/**
	 * Adds all queued Products to the mapping.
	 */
	public void process() {
		while (!productQueue.isEmpty()) {
			Product product = productQueue.poll();
			switch (product.getType()) {
			case BUY:
				addBuyProduct(product);
				break;
			case SELL:
				addSellProduct(product);
				break;
			default:
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * Adds a new buy order to the mapping.
	 * 
	 * @param product The Product which should be added.
	 */
	private void addBuyProduct(Product product) {
		Set<Product> productSet = products.get(ProductType.SELL, ProductState.PENDING, product.getItemId());
		for (Product element : productSet) {
			if (element.getPrice() > product.getPrice()) {
				break;
			}
			// TODO: Safety check so collectible coins doesn't overflow Integer
			// limit
			int amount = Integer.max(product.getRemainingQuantity(), element.getRemainingQuantity());
			product.subtractRemainingQuantity(amount);
			product.addCollectibleQuantity(amount);
			product.subtractRemainingCoins(amount * product.getPrice());
			product.addCollectibleCoins(amount * (product.getPrice() - element.getPrice()));
			element.subtractRemainingQuantity(amount);
			element.addCollectibleCoins(amount * product.getPrice());
			if (element.getRemainingQuantity() <= 0) {
				products.changeState(element, ProductState.COMPLETED);
			}
			if (product.getRemainingQuantity() <= 0) {
				product.setState(ProductState.COMPLETED);
				break;
			}
		}
		products.add(product);
	}

	/**
	 * Adds a new sell order to the mapping.
	 * 
	 * @param product The Product which should be added.
	 */
	private void addSellProduct(Product product) {
		Set<Product> productSet = products.get(ProductType.BUY, ProductState.PENDING, product.getItemId());
		for (Product element : productSet) {
			if (element.getPrice() < product.getPrice()) {
				break;
			}
			// TODO: Safety check so collectible coins doesn't overflow Integer
			// limit
			int amount = Integer.max(product.getRemainingQuantity(), element.getRemainingQuantity());
			product.subtractRemainingQuantity(amount);
			product.addCollectibleCoins(amount * element.getPrice());
			element.subtractRemainingQuantity(amount);
			element.addCollectibleQuantity(amount);
			element.subtractRemainingCoins(amount * element.getPrice());
			if (element.getRemainingQuantity() <= 0) {
				products.changeState(element, ProductState.COMPLETED);
			}
			if (product.getRemainingQuantity() <= 0) {
				product.setState(ProductState.COMPLETED);
				break;
			}
		}
		products.add(product);
	}

	public void readLibrary() {
		try {
			Collection<File> files = FileUtils.listFiles(IO_PATH.resolve("offers").toFile(), ArrayUtils.toArray("dat"), true);
			for (File file : files) {
				Stream stream = new Stream(Files.readAllBytes(file.toPath()));
				String identifier = FilenameUtils.removeExtension(file.getName());
				String owner = stream.readString();
				ProductType type = ProductType.getType(stream.readSignedByte()).get();
				ProductState state = ProductState.getState(stream.readSignedByte()).get();
				byte slot = stream.readSignedByte();
				int item = stream.readInteger();
				int maximumQuantity = stream.readInteger();
				int collectableQuantity = stream.readInteger();
				int remainingQuantity = stream.readInteger();
				int collectibleCoins = stream.readInteger();
				int remainingCoins = stream.readInteger();
				int price = stream.readInteger();
				long timestamp = stream.readLong();
				Product product = new Product(identifier, owner, type, state, slot, item, maximumQuantity, collectableQuantity, remainingQuantity, collectibleCoins, remainingCoins,
						price, timestamp);
				products.add(product);
			}

			Collection<File> historyFiles = FileUtils.listFiles(IO_PATH.resolve("history").toFile(), ArrayUtils.toArray("dat"), true);
			for (File historyFile : historyFiles) {
				Stream stream = new Stream(Files.readAllBytes(historyFile.toPath()));
				int item = Integer.parseInt(FilenameUtils.removeExtension(historyFile.getName()));
				ProductHistory history = new ProductHistory(item);
				while (stream.currentOffset < stream.getBuffer().length) {
					byte month = stream.readSignedByte();
					int payload = stream.readInteger();
					int offset = stream.currentOffset;
					while (stream.currentOffset < offset + payload) {
						byte day = stream.readSignedByte();
						int price = stream.readInteger();
						long sold = stream.readLong();
						history.addProductData(month, day, new ProductData(price, sold));
					}
				}
				this.history.put(item, history);
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to read a grand exchange file.", e);
		}
	}

	public void write(Product product) throws Exception {
		Path directory = IO_PATH.resolve(Paths.get("offers", Integer.toString(product.getItemId())));
		if (Files.notExists(directory)) {
			Files.createDirectories(directory);
		}
		Path file = Paths.get(directory.toString(), product.getIdentifier() + ".dat");
		Stream stream = new Stream();
		stream.writeString(product.getOwner());
		stream.writeByte(product.getType().getId());
		stream.writeByte(product.getState().getId());
		stream.writeByte(product.getSlot());
		stream.writeDWord(product.getItemId());
		stream.writeDWord(product.getMaximumQuantity());
		stream.writeDWord(product.getCollectibleQuantity());
		stream.writeDWord(product.getRemainingQuantity());
		stream.writeDWord(product.getCollectibleCoins());
		stream.writeDWord(product.getRemainingCoins());
		stream.writeDWord(product.getPrice());
		stream.writeQWord(product.getTimestamp());
		Files.deleteIfExists(file);
		Files.createFile(file);
		Files.write(file, Arrays.copyOf(stream.buffer, stream.currentOffset));
	}

	public void update() {
		if (System.currentTimeMillis() >= nextUpdateTime) {
			Calendar calendar = Server.getCalendar().getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 0);
			nextUpdateTime = calendar.getTimeInMillis();
		}
	}

	public long getNextUpdateTime() {
		return nextUpdateTime;
	}

}
