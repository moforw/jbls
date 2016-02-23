package jbls;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

//TODO update Rec objs on rollback
///add test

//TODO add Trans class
///DB constructor param
///implement closeable
///rollback if not committed
///add DB.trans() to create new

//TODO add Context.isDel(this, UUID)
///check in Tbl.get()
///add test

//TODO add PrevOffs/prevOffs to Tbl/Rec/BasicRec

//TODO add Idx / UIdx / MIdx
///use concurrent maps / sets
////add set ops to Idx interface
///add(l, r, res)
///retain(l, r, res)
///remove(l, r, res)
///override in sub classes
///take Col as constructor param

//TODO add indexing test

//TODO convert Albaum to use jbls

//TODO compression?

public class DB {
	public DB(final Path p) {
		path = p;
	}
	
	public void commit() {
		tempTbls.entrySet()
			.parallelStream()
			.forEach((e) -> {
				e.getValue().recs()
					.parallel()
					.forEach((r) -> {
						e.getKey().up(r);
						long offs = commitRec(e.getKey(), r);
						commitOffs(e.getKey(), r.id(), offs);
						
					});

				e.getValue().dels()
					.parallel()
					.forEach((id) -> {
						e.getKey().del(id);	
						commitDel(e.getKey(), id);
					});
			});

		clearTemp();
	}

	public long commitRec(final Tbl<?> t, Rec r) {
		try (final FileOutputStream f = new FileOutputStream(t.recsPath(path).toString(), true)) {
			final long offs = f.getChannel().size();
			final OutputStreamWriter w = new OutputStreamWriter(f);
			try(JsonGenerator json = Json.createGenerator(w)) {
				json.writeStartObject();
				t.writeJson(r, json);
				json.writeEnd();
				
				try {
					w.write('\n');
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			return offs;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void commitOffs(final Tbl<?> t, final UUID id, long offs) {		
		try (final FileWriter fw = 
				new FileWriter(t.offsPath(path).toString(), true);
				JsonGenerator json = Json.createGenerator(fw)) {
			json.writeStartObject();
			json.write("id", id.toString());
			json.write("offs", offs);
			json.writeEnd();
			fw.write('\n');
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void commitDel(final Tbl<?> t, final UUID id) {		
		try (final FileWriter fw = 
				new FileWriter(t.offsPath(path).toString(), true);
				JsonGenerator json = Json.createGenerator(fw)) {
			json.writeStartObject();
			json.write("id", id.toString());
			json.writeNull("offs");
			json.writeEnd();
			fw.write('\n');
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
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
	
	protected <RecT extends Rec> void del(final Tbl<RecT> t, final RecT r) {
		tempTbl(t).del(r.id());
	}
	
	private void clearTemp() {
		tempTbls.values().parallelStream().forEach((tt) -> tt.clear());
	}
	
	private final Path path;
	private Map<Tbl<?>, TempTbl<?>> tempTbls = new ConcurrentSkipListMap<>();
}
