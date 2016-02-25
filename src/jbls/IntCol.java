package jbls;

import javax.json.stream.JsonGenerator;

public class IntCol<RecT> extends Col<RecT, Integer> {
	public IntCol(final String n) {
		super(n);
	}

	@Override
	public IntCol<RecT> read(final Reader<RecT, Integer> r) {
		super.read(r);
		return this;
	}

	@Override
	public IntCol<RecT> write(final Writer<RecT, Integer> w) {
		super.write(w);
		return this;
	}
	
	@Override
	public void writeJson(final Integer v, final JsonGenerator json) {
		json.write(name, v);
	}
}
