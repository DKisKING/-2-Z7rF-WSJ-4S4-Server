package osv.model.content.grand_exchange;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;

public enum ProductType {
	BUY(0), SELL(1);

	private final byte id;

	private ProductType(int id) {
		Preconditions.checkArgument(id > Byte.MIN_VALUE && id < Byte.MAX_VALUE);
		this.id = (byte) id;
	}

	public int getId() {
		return id;
	}

	private static final Set<ProductType> TYPES = Collections.unmodifiableSet(EnumSet.allOf(ProductType.class));

	public static final Optional<ProductType> getType(byte id) {
		return TYPES.stream().filter(type -> type.id == id).findFirst();
	}
}
