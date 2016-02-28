package jbls;

import java.util.UUID;

public class IdCol<RecT> extends Col<RecT, UUID> {
	public IdCol(final String n) {
		super(n);
	}

	@Override
	public UUID fromJson(final String v) {
        return UUID.fromString(v);
	}	
	
	@Override
	public IdCol<RecT> read(final Reader<RecT, UUID> r) {
		super.read(r);
		return this;
	}

	@Override
	public IdCol<RecT> write(final Writer<RecT, UUID> w) {
		super.write(w);
		return this;
	}
}
