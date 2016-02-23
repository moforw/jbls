package jbls;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

import javax.json.stream.JsonGenerator;

public abstract class Tbl<RecT extends Rec> implements Comparable<Tbl<RecT>> {
	public final IdCol   Id =      idCol(  "id")
		.read((r)     -> r.id());
	public final TimeCol InTime =  timeCol("inTime")
		.read((r)     -> r.insTime());
	public final IntCol  Rev =     intCol( "rev")
		.read((r)     -> r.rev())
		.write((r, v) -> r.setRev(v));	
	public final TimeCol UpTime = timeCol("upTime")
		.read((r)     -> r.upTime())
		.write((r, v) -> r.setUpTime(v));
		
	public final String name;
	
	public Tbl(final String n) {
		name = n;
	}
	
	public <ValT, ColT extends Col<RecT, ValT>> ColT addCol(final ColT c) {
		cols().add(c);
		return c;
	}
	
	@Override
	public int compareTo(final Tbl<RecT> other) {
		return name.compareTo(other.name);
	}

	public void del(final RecT r, final DB db) {
		db.del(this, r);
	}

	@SuppressWarnings("unchecked")
	public RecT get(final UUID id, final DB db) {
		final Tbl<RecT> tt = db.tempTbl(this);
		RecT r = tt.getRec(id);

		if (r == null) {
			r = (RecT)recs.get(id);

			if (r == null) {
				loadRec(id);
			}
		}
		
		return r;
	}
	
	public Stream<UUID> ids() {
		return offs.keySet().stream();
	}
	
	public IntCol intCol(final String n) {
		return addCol(new IntCol(n));
	}

	public RecT ins(final DB db) {
		final RecT r = newRec(UUID.randomUUID());
		db.tempTbl(this).up(r);
		return r;
	}

	public Stream<Rec> recs() {
		return recs.values().stream();
	}

	public StringCol stringCol(final String n) {
		return addCol(new StringCol(n));
	}

	public TimeCol timeCol(final String n) {
		return addCol(new TimeCol(n));
	}

	public IdCol idCol(final String n) {
		return addCol(new IdCol(n));
	}

	public Path offsPath(final Path root) {
		return root.resolve(
			FileSystems.getDefault().getPath(
				String.format("%s.fbo", name)));
	}

	public Path recsPath(final Path root) {
		return root.resolve(
			FileSystems.getDefault().getPath(
				String.format("%s.fbr", name)));
	}

	public void up(final RecT r, final DB db) {
		db.tempTbl(this).up(r);
	}

	public void writeJson(final Rec r, final JsonGenerator json) {
		for (final Col<?, ?> c: cols) {
			c.writeJson(r, json);
		}
	}
	
	protected void clear() {
		recs.clear();
		offs.clear();
	}

	protected TempTbl<RecT> temp(final String n) {
		final Tbl<RecT> t = this;
		return new TempTbl<RecT>(n) {
			@Override
			protected RecT newRec(UUID id) {
				return t.newRec(id);
			}
		};
	}

	protected void del(final UUID id) {
		offs.remove(id);
		recs.remove(id);
	}
	
	protected RecT getRec(final UUID id) {
		@SuppressWarnings("unchecked")
		final RecT r = (RecT)recs.get(id);	
		return (r == null) ? loadRec(id) : r;
	}

	protected RecT loadRec(final UUID id) {
		if (!offs.containsKey(id)) {
			return null;
		}
		
		//TODO load rec from offs
		
		return null;
	}
	
	protected void up(final Rec r) {
		recs.put(r.id(), r);
		offs.put(r.id(), -1);
		r.setRev(r.rev() + 1);
	}

	protected abstract RecT newRec(final UUID id);

	private Set<Col<RecT, ?>> cols() {
		if (cols == null) {
			cols = new TreeSet<>();
		}
		
		return cols;
	}
	
	private Set<Col<RecT, ?>> cols;
	private final Map<UUID, Integer> offs = new ConcurrentSkipListMap<>();
	private final Map<UUID, Rec> recs = new ConcurrentSkipListMap<>();

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
		
		@SuppressWarnings("unchecked")
		public void writeJson(final Rec r, final JsonGenerator json) {
			int v = getVal((RecT)r);
			json.write(name, v);
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

		@SuppressWarnings("unchecked")
		public void writeJson(final Rec r, final JsonGenerator json) {
			String v = getVal((RecT)r);
			if (v == null) {
				json.writeNull(name);
			} else {
				json.write(name, v);
			}
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
	
		@SuppressWarnings("unchecked")
		public void writeJson(final Rec r, final JsonGenerator json) {
			final Instant v = getVal((RecT)r);
			if (v == null) {
				json.writeNull(name);
			} else {
				json.write(name, v.toString());
			}
		}
	}

	public class IdCol extends Col<RecT, UUID> {
		public IdCol(final String n) {
			super(n);
		}
	
		public IdCol read(final Reader<RecT, UUID> r) {
			super.read(r);
			return this;
		}

		public IdCol write(final Writer<RecT, UUID> w) {
			super.write(w);
			return this;
		}

		@SuppressWarnings("unchecked")
		public void writeJson(final Rec r, final JsonGenerator json) {
			final UUID v = getVal((RecT)r);
			if (v == null) {
				json.writeNull(name);
			} else {
				json.write(name, v.toString());
			}
		}
	}
}
