package jbls;

import java.time.Instant;

import javax.json.JsonString;
import javax.json.JsonValue;

public class TimeCol<RecT> extends Col<RecT, Instant> {
	public TimeCol(final String n) {
		super(n);
	}

	@Override
	public Instant fromJson(final JsonValue v) {
 		return Instant.parse(((JsonString)v).getString());
	}	

	@Override
	public TimeCol<RecT> read(final Reader<RecT, Instant> r) {
		super.read(r);
		return this;
	}

	@Override
	public TimeCol<RecT> write(final Writer<RecT, Instant> w) {
		super.write(w);
		return this;
	}
}
