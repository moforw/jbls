package jbls;

import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

public class StrCol<RecT> extends Col<RecT, String> {
	public StrCol(final String n) {
		super(n);
	}

	@Override
	public String fromJson(final JsonValue v) {
        return ((JsonString)v).getString();
	}	

	@Override
	public StrCol<RecT> read(final Reader<RecT, String> r) {
		super.read(r);
		return this;
	}

	@Override
	public StrCol<RecT> write(final Writer<RecT, String> w) {
		super.write(w);
		return this;
	}

	@Override
	public void writeJson(final String v, final JsonGenerator json) {
		json.write(name, v);
	}
}
