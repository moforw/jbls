package jbls;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

//TODO add NGram class
///TODO add static BigInteger score(final String s)
///TODO build from other side and reverse using .not()?
///TODO or, figure out largest value from string length?
///TODO multiplier can be increased/decreased by shifting
///TODO add timing test

//TODO add Trans class
///DB constructor param
///implement closeable
///rollback if not committed
///add DB.trans() to create new

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

//TODO compression?

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
