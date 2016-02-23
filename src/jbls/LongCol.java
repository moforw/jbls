package jbls;

import javax.json.stream.JsonGenerator;

public class LongCol<RecT> extends Col<RecT, Long> {
	public LongCol(final String n) {
		super(n);
	}

	@Override
	public LongCol<RecT> read(final Reader<RecT, Long> r) {
		super.read(r);
		return this;
	}

	@Override
	public LongCol<RecT> write(final Writer<RecT, Long> w) {
		super.write(w);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public void writeJson(final Rec r, final JsonGenerator json) {
		long v = getVal((RecT)r);
		json.write(name, v);
	}
}