package jbls;

import javax.json.stream.JsonGenerator;

public class StringCol<RecT> extends Col<RecT, String> {
	public StringCol(final String n) {
		super(n);
	}

	public StringCol<RecT> read(final Reader<RecT, String> r) {
		super.read(r);
		return this;
	}

	public StringCol<RecT> write(final Writer<RecT, String> w) {
		super.write(w);
		return this;
	}

	@SuppressWarnings("unchecked")
	public void writeJson(final Rec r, final JsonGenerator json) {
		String v = getVal((RecT)r);
		if (v == null) {
			json.writeNull(name);
		} else {
			json.write(name, v);
		}
	}
}
