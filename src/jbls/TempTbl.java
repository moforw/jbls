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
	public TempTbl<RecT> clear() {
		super.clear();
		dels.clear();
		return this;
	}
	
	public Stream<UUID> dels() {
		return dels.stream();
	}

	public boolean isDel(final UUID id) {
		return dels.contains(id);
	}

	@Override
	protected void del(final UUID id) {
		super.del(id);
		dels.add(id);
	}

	private final Set<UUID> dels = new ConcurrentSkipListSet<>();
}
