package jbls;

import javax.json.stream.JsonGenerator;

public class StringCol<RecT> extends Col<RecT, String> {
	public StringCol(final String n) {
		super(n);
	}

	@Override
	public StringCol<RecT> read(final Reader<RecT, String> r) {
		super.read(r);
		return this;
	}

	@Override
	public StringCol<RecT> write(final Writer<RecT, String> w) {
		super.write(w);
		return this;
	}

	@Override
	public void writeJson(final String v, final JsonGenerator json) {
		json.write(name, v);
	}
}
