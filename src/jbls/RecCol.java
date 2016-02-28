package jbls;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

public class RecCol<RecT extends Rec, ValT extends Rec> 
extends Col<RecT, ValT>{
	public final Tbl<ValT> valTbl;

	public RecCol(final String n, final Tbl<ValT> vt) {
		super(n);
		valTbl = vt;
	}
	
	@Override
	public ValT fromJson(final JsonValue v) {
		throw new RuntimeException("Not supported!");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void load(final Rec rec, final JsonObject json, final DB db) {
		final JsonObject jso = json.getJsonObject(name);
		setVal((RecT)rec, valTbl.load(jso, db));
	}
	
	@Override
	public RecCol<RecT, ValT> read(final Reader<RecT, ValT> r) {
		super.read(r);
		return this;
	}

	@Override
	public RecCol<RecT, ValT> write(final Writer<RecT, ValT> w) {
		super.write(w);
		return this;
	}

	@Override
	public void writeJson(final ValT v, final JsonGenerator json) {
		json.writeStartObject(name);
		try {
			valTbl.writeJson(v, json);
		} finally {
			json.writeEnd();
		}
	}
}