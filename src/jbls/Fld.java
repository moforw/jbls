package jbls;

public class Fld<RecT, ValT extends Comparable<ValT>> implements Comparable<Fld<RecT, ValT>> {
	public final String name;
	
	public Fld(final String n) {
		name = n;
	}

	public int compareRecs(final RecT l, final RecT r) {
		final ValT lv = getVal(l);
		final ValT rv = getVal(r);
		
		if (lv == null && rv == null) {
			return 0;
		} 
		
		if (lv == null || rv == null) {
			return (lv == null) ? -1 : 1;
		}
		
		return getVal(l).compareTo(getVal(r));
	}
	
	@Override
	public int compareTo(Fld<RecT, ValT> other) {
		return name.compareTo(other.name);
	}	

	public Fld<RecT, ValT> read(Reader<RecT, ValT> r) {
		reader = r;
		return this;
	}

	public ValT getVal(final RecT r) {
		return reader.val(r);
	}
	
	public Fld<RecT, ValT> write(Writer<RecT, ValT> w) {
		writer = w;
		return this;
	}

	public void setVal(final RecT r, final ValT v) {
		writer.setVal(r, v);
	}
	
	private Reader<RecT, ValT> reader;
	private Writer<RecT, ValT> writer;
}
