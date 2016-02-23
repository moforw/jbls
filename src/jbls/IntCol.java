package jbls;

import javax.json.stream.JsonGenerator;

public class IntCol<RecT> extends Col<RecT, Integer> {
	public IntCol(final String n) {
		super(n);
	}

	public IntCol<RecT> read(final Reader<RecT, Integer> r) {
		super.read(r);
		return this;
	}

	public IntCol<RecT> write(final Writer<RecT, Integer> w) {
		super.write(w);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public void writeJson(final Rec r, final JsonGenerator json) {
		int v = getVal((RecT)r);
		json.write(name, v);
	}
}
