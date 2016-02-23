package jbls;

import java.time.Instant;

import javax.json.stream.JsonGenerator;

public class TimeCol<RecT> extends Col<RecT, Instant> {
	public TimeCol(final String n) {
		super(n);
	}

	public TimeCol<RecT> read(final Reader<RecT, Instant> r) {
		super.read(r);
		return this;
	}

	public TimeCol<RecT> write(final Writer<RecT, Instant> w) {
		super.write(w);
		return this;
	}

	@SuppressWarnings("unchecked")
	public void writeJson(final Rec r, final JsonGenerator json) {
		final Instant v = getVal((RecT)r);
		if (v == null) {
			json.writeNull(name);
		} else {
			json.write(name, v.toString());
		}
	}
}
