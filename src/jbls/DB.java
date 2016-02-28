package jbls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

//TODO rename KeyCol to BasicKeyCol<RecT, KeyT>
///add PubKeyCol & PrivKeyCol

//TODO add RevIdx<RecT, ValT>
///convert Tbl.offs to RevIdx<RecT, Long>
///change RefCol to take a RevIdx instead of Tbl for lookup
///add stream(set<UUID>), return stream of ValT

//TODO add aes encryption
///check bookmark

//TODO convert Albaum to use jbls
///one table for facts

//TODO add Idx / UIdx / MIdx
///use concurrent maps / sets
////add set ops to Idx interface
///add(l, r, res)
///retain(l, r, res)
///remove(l, r, res)
///override in sub classes
///take Col as constructor param

public class DB {
	public static void writeDel(final Tbl<?> t, final UUID id, final JsonGenerator json) {
		json.writeStartObject();
		json.write("id", t.Id.toJson(id));
		json.write("del", true);
		json.writeEnd();
		json.flush();
	}

	public static void writeLn(final OutputStreamWriter w) {
		try {
			w.write('\n');
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}	
	
	public final Path path;	
	
	public DB(final Path p) {
		path = p;
	}
	
	public void commit() {
		tempTbls.entrySet()
			.parallelStream()
			.forEach((e) -> {
				final Tbl<?> t = e.getKey();
				final TempTbl<?> tt = e.getValue();
				
				final File f = t.recsPath(this).toFile();
				try {
					f.createNewFile();
					try (final FileOutputStream fs = new FileOutputStream(f, true);
							final OutputStreamWriter fw = new OutputStreamWriter(fs)) {
						final FileChannel chan = fs.getChannel();
					
						tt.recs()
							.forEach((r) -> {
								t.setPrevOffs(r);
								
								try {
									final long offs = chan.size();
									final JsonGenerator json = 
											Json.createGenerator(fw);
									
									json.writeStartObject();
									t.writeJson(r, json);
									json.writeEnd();
									json.flush();

									writeLn(fw);
									t.up(r, offs);
								}  catch (final IOException ex) {
									throw new RuntimeException(ex);
								}
						});
						
						tt.dels().forEach((id) -> {
							t.del(id);
							writeDel(t, id, Json.createGenerator(fw));
							writeLn(fw);
						});						
					}
				} catch (final IOException ex) {
					throw new RuntimeException(ex);
				}

				try (final FileWriter fw = 
						new FileWriter(t.offsPath(this).toFile(), true)) {
				
					tt.recs().forEach((r) -> {
							final JsonGenerator json = 
									Json.createGenerator(fw);
							
							json.writeStartObject();
							json.write("id", t.Id.toJson(r.id()));
							json.write("offs", t.offs(r));
							json.writeEnd();
							json.flush();
							
							writeLn(fw);
					});

					tt.dels().forEach((id) -> {
						writeDel(t, id, Json.createGenerator(fw));
						writeLn(fw);
					});
				} catch (final IOException ex) {
					throw new RuntimeException(ex);
				}
				
			
			});

		clearTemp();
	}
	
	public boolean isDel(final Tbl<?> t, final UUID id) {
		final TempTbl<?> tt = tempTbls.get(t);
		
		if (tt == null) {
			return false;
		}
		
		return tt.isDel(id);
	}
	
	public <RecT extends Rec> boolean isDirty(final Tbl<RecT> t, final RecT r) {
		@SuppressWarnings("unchecked")
		final TempTbl<RecT> tt = (TempTbl<RecT>)tempTbls.get(t);
		
		if (tt == null) {
			return false;
		}
		
		return tt.basicGet(r.id(), this) != null;
	}

	public void rollback() {
		clearTemp();
	}
	
	@SuppressWarnings("unchecked")
	public <RecT extends Rec> TempTbl<RecT> tempTbl(final Tbl<RecT> t) {
		if (tempTbls.containsKey(t)) {
			return (TempTbl<RecT>)tempTbls.get(t);
		} else {
			final String ttn = String.format("temp%s", 
				Character.toUpperCase(t.name.charAt(0)), t.name.substring(1));
			final TempTbl<RecT> tt = t.temp(ttn);
			tempTbls.put(t, tt);
			return tt;
		}
	}
	
	public Trans trans() {
		return new Trans(this);
	}
	
	protected <RecT extends Rec> void del(final Tbl<RecT> t, final RecT r) {
		tempTbl(t).del(r.id());
	}
	
	private void clearTemp() {
		tempTbls.values().parallelStream().forEach((tt) -> tt.clear());
	}
	
	private Map<Tbl<?>, TempTbl<?>> tempTbls = new ConcurrentSkipListMap<>();
}
