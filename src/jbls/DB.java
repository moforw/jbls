package jbls;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

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

//TODO add Tbl.del
///add Context.del

public class DB {
	public DB(final Path p) {
	}
	
	public void commit() {
		clearTemp();
	}
	
	public void rollback() {		
		clearTemp();
	}
	
	@SuppressWarnings("unchecked")
	public <RecT extends Rec> Tbl<RecT> getTemp(final Tbl<RecT> t) {
		if (tempTbls.containsKey(t)) {
			return (Tbl<RecT>)tempTbls.get(t);
		} else {
			final String ttn = String.format("temp%s", 
				Character.toUpperCase(t.name.charAt(0)), t.name.substring(1));
			final Tbl<RecT> tt = t.clone(ttn);
			tempTbls.put(t, tt);
			return tt;
		}
	}
	
	private void clearTemp() {
		tempTbls.values().parallelStream().forEach((tt) -> tt.clear());
	}
	
	private Map<Tbl<?>, Tbl<?>> tempTbls = new ConcurrentSkipListMap<>();
}
