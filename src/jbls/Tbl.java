package jbls;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

public abstract class Tbl<RecT extends Rec> implements Comparable<Tbl<RecT>>, Def<RecT> {
	public final IdCol<RecT>   Id =       idCol(   "sys:id")
		.read((r)     -> r.id());
	public final TimeCol<RecT> InTime =   timeCol( "sys:inTime")
		.read((r)     -> r.insTime());
	public final LongCol<RecT>  PrevOffs = longCol("sys:prevOffs")
		.read((r)     -> r.prevOffs())
		.write((r, v) -> r.setPrevOffs(v));	
	public final IntCol<RecT>  Rev =       intCol( "sys:rev")
		.read((r)     -> r.rev())
		.write((r, v) -> r.setRev(v));	
	public final TimeCol<RecT> UpTime =    timeCol("sys:upTime")
		.read((r)     -> r.upTime())
		.write((r, v) -> r.setUpTime(v));
		
	public final String name;
	
	public Tbl(final String n) {
		name = n;
	}
	
	public <ValT, ColT extends Col<RecT, ValT>> ColT addCol(final ColT c) {
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

	public DeciCol<RecT> deciCol(final String n) {
		return addCol(new DeciCol<RecT>(n));
	}

	public void del(final RecT r, final DB db) {
		db.del(this, r);
	}

	public RecT get(final UUID id, final DB db) {
		final TempTbl<RecT> tt = db.tempTbl(this);
		
		if (tt.isDel(id)) {
			return null;
		}
		
		RecT r = tt.basicGet(id, db);
		
		return (r == null) ? basicGet(id, db) : r;
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

	public void loadOffs(final DB db) {
		try (InputStream in = Files.newInputStream(offsPath(db));
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
		    String line;
		    
		    while ((line = reader.readLine()) != null) {
		    	if (line.compareTo(" ") > 0) {
			    	try (JsonReader json = Json.createReader(new StringReader(line))) {
				    	final JsonObject jso = json.readObject();
				    	final UUID id = Id.fromJson(jso.get("id"));
				    	
				    	if (jso.get("del") == JsonValue.TRUE) {
							offs.remove(id);
				    	} else {
				    		offs.put(id, jso.getJsonNumber("offs").longValue());
				    	}
			    	}
		    	}
		    }		    
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}
	}

	public void loadRecs(final DB db) {
		try (InputStream in = Files.newInputStream(recsPath(db));
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
		    String line;
		    
		    while ((line = reader.readLine()) != null) {
		    	if (line.compareTo(" ") > 0) {
			    	try (JsonReader json = Json.createReader(new StringReader(line))) {
				    	final JsonObject jso = json.readObject();
				    	load(jso, db);
			    	}
		    	}
		    }		    
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}
	}
	
	public <ValT> MapCol<RecT, ValT> mapCol(final Fld<RecT, ValT> vf) {		
		return addCol(new MapCol<>(vf));
	}

	public Stream<Rec> recs() {
		return recs.values().stream();
	}

	public <ValT extends Rec> RecCol<RecT, ValT> 
	recCol(final String n, final Tbl<ValT> vt) {		
		return addCol(new RecCol<>(n, vt));
	}

	public <RefT extends Rec> RefCol<RecT, RefT> 
	refCol(final String n, final Tbl<RefT> rt) {		
		return addCol(new RefCol<>(n, rt));
	}

	public <ValT> SeqCol<RecT, ValT> seqCol(final Fld<RecT, ValT> fld) {		
		return addCol(new SeqCol<>(fld));
	}

	public StrCol<RecT> strCol(final String n) {
		return addCol(new StrCol<RecT>(n));
	}

	public TimeCol<RecT> timeCol(final String n) {
		return addCol(new TimeCol<RecT>(n));
	}

	public long offs(final Rec r) {
		return offs.containsKey(r.id()) ? offs.get(r.id()) : -1;
	}
	
	public Path offsPath(final DB db) {
		return db.path.resolve(
			FileSystems.getDefault().getPath(
				String.format("%s.jbi", name)));
	}

	public KeyCol<RecT> pubKeyCol(final String n) {
		return addCol(new KeyCol<RecT>(KeyCol.KeyType.PUBLIC, n));		
	}
	
	public KeyCol<RecT> privKeyCol(final String n) {
		return addCol(new KeyCol<RecT>(KeyCol.KeyType.PRIVATE, n));		
	}
	
	public Path recsPath(final DB db) {
		return db.path.resolve(
			FileSystems.getDefault().getPath(
				String.format("%s.jbt", name)));
	}

	public void up(final RecT r, final DB db) {
		db.tempTbl(this).up(r, offs(r));
	}

	public void writeJson(final Rec r, final JsonGenerator json) {
		for (final Col<?, ?> c: cols) {
			c.writeRecJson(r, json);
		}
	}
	
	public Tbl<RecT> clear() {
		recs.clear();
		offs.clear();
		return this;
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
	
	protected RecT basicGet(final UUID id, final DB db) {
		@SuppressWarnings("unchecked")
		final RecT r = (RecT)recs.get(id);	
		return (r == null) ? load(id, db) : r;
	}

	public RecT load(final JsonObject json, final DB db) {
		final UUID id = Id.readJson(json);
		
		if (json.get("sys:del") == JsonValue.TRUE) {
			offs.remove(id);
			recs.remove(id);
			return null;
    	} else {
	
			RecT r = newRec(id);
			
			cols().forEach((c) -> { 
				if (c != Id && c.writer() != null) { 
					c.load(r, json, db);
				}
			});
			
			@SuppressWarnings("unchecked")
			final RecT prev = (RecT)recs.get(r.id());
			
			if (prev == null || prev.rev() < r.rev()) {
				recs.put(r.id(), r);			
				return r;
			}
			
			return prev;
    	}
	}

	public RecT load(final Path p, final long offs, final DB db) {
		try (final FileInputStream fs = new FileInputStream(p.toFile())) {
			fs.getChannel().position(offs);
		    BufferedReader reader = 
		    	new BufferedReader(new InputStreamReader(fs));
		    			
	    	try (JsonReader json = Json.createReader(reader)) {
		    	return load(json.readObject(), db);
	    	}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public RecT load(final UUID id, final DB db) {
		final Long o = offs.get(id);
		
		if (o == null) {
			return null;
		}
		
		return load(recsPath(db), o, db);		
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
