package jbls;

import java.util.UUID;

import javax.json.stream.JsonGenerator;

public class IdCol<RecT> extends Col<RecT, UUID> {
	public IdCol(final String n) {
		super(n);
	}

	public IdCol<RecT> read(final Reader<RecT, UUID> r) {
		super.read(r);
		return this;
	}

	public IdCol<RecT> write(final Writer<RecT, UUID> w) {
		super.write(w);
		return this;
	}

	@SuppressWarnings("unchecked")
	public void writeJson(final Rec r, final JsonGenerator json) {
		final UUID v = getVal((RecT)r);
		if (v == null) {
			json.writeNull(name);
		} else {
			json.write(name, v.toString());
		}
	}
}
