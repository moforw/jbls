package jbls;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

//TODO convert Rec to interface with id(), insTime(), upTime(), setUpTime(), rev(), setRev(), methods
///TODO add BasicRec with all fields, implement Rec
///TODO change Tbl to use accessors

//TODO add Context.isDel(this, UUID)
///check in Tbl.get()

//TODO add context commit/rollback test

//TODO add Idx / UIdx / MIdx
///use concurrent maps / sets
////add set ops to Idx interface
///add(l, r, res)
///retain(l, r, res)
///remove(l, r, res)
///override in sub classes
///take Reader<RecT, ValT> as constructor param

//TODO add indexing test

//TODO add commit/rollback indexing tests

//TODO add log file reading/writing

//TODO convert Albaum to use jbls

public class DB {
	public DB(final Path p) {
	}
	
	public void commit() {
		tempTbls.entrySet()
			.parallelStream()
			.forEach((e) -> {
				e.getValue().recs()
					.parallel()
					.forEach((r) -> e.getKey().up(r));

				e.getValue().dels()
					.parallel()
					.forEach((id) -> e.getKey().del(id));
			});

		clearTemp();
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
	
	private Map<Tbl<?>, TempTbl<?>> tempTbls = new ConcurrentSkipListMap<>();
}
