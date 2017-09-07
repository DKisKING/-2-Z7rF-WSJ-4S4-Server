package osv.model.content.grand_exchange;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;

public enum ProductState {
	PENDING(0), COMPLETED(1), CANCELLED(2);

	private final byte id;

	private ProductState(int id) {
		Preconditions.checkArgument(id > Byte.MIN_VALUE && id < Byte.MAX_VALUE);
		this.id = (byte) id;
	}

	public byte getId() {
		return id;
	}

	private static final Set<ProductState> STATES = Collections.unmodifiableSet(EnumSet.allOf(ProductState.class));

	public static final Optional<ProductState> getState(byte id) {
		return STATES.stream().filter(state -> state.id == id).findFirst();
	}
}
