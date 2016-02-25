package jbls;

import java.time.Instant;

public class TimeCol<RecT> extends Col<RecT, Instant> {
	public TimeCol(final String n) {
		super(n);
	}

	@Override
	public TimeCol<RecT> read(final Reader<RecT, Instant> r) {
		super.read(r);
		return this;
	}

	@Override
	public TimeCol<RecT> write(final Writer<RecT, Instant> w) {
		super.write(w);
		return this;
	}
}
