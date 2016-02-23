package jbls;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

import javax.json.stream.JsonGenerator;

public abstract class Tbl<RecT extends Rec> implements Comparable<Tbl<RecT>>, Def<RecT> {
	public final IdCol<RecT>   Id =       idCol(   "id")
		.read((r)     -> r.id());
	public final TimeCol<RecT> InTime =   timeCol( "inTime")
		.read((r)     -> r.insTime());
	public final LongCol<RecT>  PrevOffs = longCol("prevOffs")
		.read((r)     -> r.prevOffs())
		.write((r, v) -> r.setPrevOffs(v));	
	public final IntCol<RecT>  Rev =       intCol( "rev")
		.read((r)     -> r.rev())
		.write((r, v) -> r.setRev(v));	
	public final TimeCol<RecT> UpTime =    timeCol("upTime")
		.read((r)     -> r.upTime())
		.write((r, v) -> r.setUpTime(v));
		
	public final String name;
	
	public Tbl(final String n) {
		name = n;
	}
	
	public <ValT extends Comparable<ValT>, ColT extends Col<RecT, ValT>>
	ColT addCol(final ColT c) {
		colSet().add(c);
		return c;
	}
	
	@Override
	public int compareTo(final Tbl<RecT> other) {
		return name.compareTo(other.name);
	}

	@Override
	public Stream<Col<RecT, ?>> cols() {
		return cols.stream();
	}

	public void del(final RecT r, final DB db) {
		db.del(this, r);
	}

	public RecT get(final UUID id, final DB db) {
		final TempTbl<RecT> tt = db.tempTbl(this);
		
		if (tt.isDel(id)) {
			return null;
		}
		
		RecT r = tt.get(id);
		
		return (r == null) ? get(id) : r;
	}

	public IdCol<RecT> idCol(final String n) {
		return addCol(new IdCol<RecT>(n));
	}

	public Stream<UUID> ids() {
		return offs.keySet().stream();
	}
	
	public IntCol<RecT> intCol(final String n) {
		return addCol(new IntCol<RecT>(n));
	}

	public RecT ins(final DB db) {
		final RecT r = newRec(UUID.randomUUID());
		db.tempTbl(this).up(r, -1);
		return r;
	}

	public LongCol<RecT> longCol(final String n) {
		return addCol(new LongCol<RecT>(n));
	}

	public Stream<Rec> recs() {
		return recs.values().stream();
	}

	public StringCol<RecT> stringCol(final String n) {
		return addCol(new StringCol<RecT>(n));
	}

	public TimeCol<RecT> timeCol(final String n) {
		return addCol(new TimeCol<RecT>(n));
	}

	public long offs(final RecT r) {
		return offs.containsKey(r.id()) ? offs.get(r.id()) : -1;
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
		db.tempTbl(this).up(r, offs(r));
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
	
	protected RecT get(final UUID id) {
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

	protected void setPrevOffs(final Rec r) {
		@SuppressWarnings("unchecked")
		final long po = offs((RecT)r);
		
		if (po > -1) {
			r.setPrevOffs(offs.get(r.id()));
		}
	}
	
	protected void up(final Rec r, final long o) {
		r.setRev(r.rev() + 1);
		recs.put(r.id(), r);
		offs.put(r.id(), o);
	}

	protected abstract RecT newRec(final UUID id);

	private Set<Col<RecT, ?>> colSet() {
		if (cols == null) {
			cols = new TreeSet<>();
		}
		
		return cols;
	}
	
	private Set<Col<RecT, ?>> cols;
	private final Map<UUID, Long> offs = new ConcurrentSkipListMap<>();
	private final Map<UUID, Rec> recs = new ConcurrentSkipListMap<>();
}
