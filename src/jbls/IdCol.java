package jbls;

import java.util.UUID;

import javax.json.JsonString;
import javax.json.JsonValue;

public class IdCol<RecT> extends Col<RecT, UUID> {
	public IdCol(final String n) {
		super(n);
	}

	@Override
	public UUID fromJson(final JsonValue v) {
        return UUID.fromString(((JsonString)v).getString());
	}	
	
	@Override
	public IdCol<RecT> read(final Reader<RecT, UUID> r) {
		super.read(r);
		return this;
	}

	@Override
	public IdCol<RecT> write(final Writer<RecT, UUID> w) {
		super.write(w);
		return this;
	}
}
