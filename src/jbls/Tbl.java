package jbls;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public abstract class Tbl<RecT extends Rec> {
	public final UIDCol Id = uidCol("id").read((r) -> r.id);
	public final TimeCol InTime = timeCol("inTime").read((r) -> r.insTime);
	public final TimeCol UpTime = timeCol("upTime").read((r) -> r.upTime);

	public final String name;
	
	public Tbl(final String n) {
		name = n;
	}
		
	public RecT getRec(final UUID id, final DB cx) {
		final Tbl<RecT> tt = cx.getTemp(this);
		RecT r = tt.getRec(id);

		if (r == null) {
			r = recs.get(id);

			if (r == null) {
				loadRec(id);
			}
		}
		
		return r;
	}
	
	public IntCol intCol(final String n) {
		return new IntCol(n);
	}

	public RecT ins(final DB cx) {
		final RecT r = newRec(UUID.randomUUID());
		cx.getTemp(this).up(r);
		return r;
	}
	
	public StringCol stringCol(final String n) {
		return new StringCol(n);
	}

	public TimeCol timeCol(final String n) {
		return new TimeCol(n);
	}

	public UIDCol uidCol(final String n) {
		return new UIDCol(n);
	}

	public void up(final RecT r, final DB cx) {
		cx.getTemp(this).up(r);
	}

	protected void clear() {
		recs.clear();
		offs.clear();
	}

	protected Tbl<RecT> clone(final String n) {
		final Tbl<RecT> t = this;
		return new Tbl<RecT>(n) {
			@Override
			protected RecT newRec(UUID id) {
				return t.newRec(id);
			}
		};
	}

	protected RecT getRec(final UUID id) {
		final RecT r = recs.get(id);	
		return (r == null) ? loadRec(id) : r;
	}

	protected RecT loadRec(final UUID id) {
		if (!offs.containsKey(id)) {
			return null;
		}
		
		//TODO load rec from offs
		
		return null;
	}
	
	protected void up(final RecT r) {
		recs.put(r.id, r);
		offs.put(r.id, -1);
	}

	protected abstract RecT newRec(final UUID id);

	private final Map<UUID, Integer> offs = new ConcurrentSkipListMap<>();
	private final Map<UUID, RecT> recs = new ConcurrentSkipListMap<>();

	public class IntCol extends Col<RecT, Integer> {
		public IntCol(final String n) {
			super(n);
		}
	
		public IntCol read(final Reader<RecT, Integer> r) {
			super.read(r);
			return this;
		}

		public IntCol write(final Writer<RecT, Integer> w) {
			super.write(w);
			return this;
		}
	}

	public class StringCol extends Col<RecT, String> {
		public StringCol(final String n) {
			super(n);
		}
	
		public StringCol read(final Reader<RecT, String> r) {
			super.read(r);
			return this;
		}

		public StringCol write(final Writer<RecT, String> w) {
			super.write(w);
			return this;
		}
	}

	public class TimeCol extends Col<RecT, Instant> {
		public TimeCol(final String n) {
			super(n);
		}
	
		public TimeCol read(final Reader<RecT, Instant> r) {
			super.read(r);
			return this;
		}

		public TimeCol write(final Writer<RecT, Instant> w) {
			super.write(w);
			return this;
		}
	}

	public class UIDCol extends Col<RecT, UUID> {
		public UIDCol(final String n) {
			super(n);
		}
	
		public UIDCol read(final Reader<RecT, UUID> r) {
			super.read(r);
			return this;
		}

		public UIDCol write(final Writer<RecT, UUID> w) {
			super.write(w);
			return this;
		}
	}
}
