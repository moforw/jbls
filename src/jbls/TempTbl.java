package jbls;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Stream;

public abstract class TempTbl<RecT extends Rec> extends Tbl<RecT> {
	public TempTbl(String n) {
		super(n);
	}

	@Override
	protected void clear() {
		super.clear();
		dels.clear();
	}

	@Override
	protected void del(final UUID id) {
		super.del(id);
		dels.add(id);
	}
	
	public Stream<UUID> dels() {
		return dels.stream();
	}

	private final Set<UUID> dels = new ConcurrentSkipListSet<>();
}
