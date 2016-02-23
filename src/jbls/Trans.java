package jbls;

import java.io.Closeable;

public class Trans implements Closeable {
	public Trans(final DB d) {
		db = d;
	}

	@Override
	public void close() {
		db.rollback();
	}
	
	private final DB db;
}
