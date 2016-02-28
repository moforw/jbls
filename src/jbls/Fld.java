package jbls;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

public abstract class Fld<RecT, ValT> implements Comparable<Fld<RecT, ValT>> {
	public final String name;
	
	public Fld(final String n) {
		name = n;
	}
	
	@Override
	public int compareTo(Fld<RecT, ValT> other) {
		return name.compareTo(other.name);
	}	

	public abstract ValT fromJson(final JsonValue v);

	public Fld<RecT, ValT> read(Reader<RecT, ValT> r) {
		reader = r;
		return this;
	}

	public ValT getVal(final RecT r) {
		return reader.val(r);
	}
	
	public void load(final RecT r, final JsonObject json, final DB db) {
		setVal(r, readJson(json));
	}
	
	public ValT readJson(final JsonObject o, String k) {
		final JsonValue v = o.get(k);
		return (v == null || v == JsonValue.NULL) ? null : fromJson(v);
	}

	public ValT readJson(final JsonObject o) {
		return readJson(o, name);
	}

	public Reader<RecT, ValT> reader() {
		return reader;
	}

	public String toJson(final ValT v) {
		return v.toString();
	}
	
	public Fld<RecT, ValT> write(Writer<RecT, ValT> w) {
		writer = w;
		return this;
	}

	public void setVal(final RecT r, final ValT v) {
		writer.setVal(r, v);
	}

	public void writeJson(final ValT v, final JsonGenerator json) {
		json.write(name, toJson(v));
	}

	@SuppressWarnings("unchecked")
	public void writeRecJson(final Rec r, final JsonGenerator json) {
		final ValT v = getVal((RecT)r);
		if (v == null) {
			json.writeNull(name);
		} else {
			writeJson(v, json);
		}
	}

	public Writer<RecT, ValT> writer() {
		return writer;
	}

	private Reader<RecT, ValT> reader;
	private Writer<RecT, ValT> writer;
}
