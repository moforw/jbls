package jbls;

import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

public class MapCol<RecT extends Rec, ValT> 
extends Col<RecT, Map<String, ValT>>{
	public MapCol(final Fld<RecT, ValT> vf) {
		super(vf.name);
		valFld = vf;
	}
	
	@Override
	public Map<String, ValT> fromJson(final JsonValue v) {
		throw new RuntimeException("Not supported!");
	}
	
	@Override
	public void load(final Rec rec, final JsonObject json, final DB db) {
		final JsonObject jso = json.getJsonObject(name);
		@SuppressWarnings("unchecked")
		final Map<String, ValT> m = getVal((RecT)rec);

		jso.keySet().forEach((k) -> {
			m.put(k, valFld.readJson(jso, k));
		});		
	}

	@Override
	public MapCol<RecT, ValT> read(final Reader<RecT, Map<String, ValT>> r) {
		super.read(r);
		return this;
	}

	@Override
	public void writeJson(final Map<String, ValT> vs, final JsonGenerator json) {
		json.writeStartObject(name);
		
		try {
			vs.entrySet().forEach((e) -> {
				json.write(e.getKey(), valFld.toJson(e.getValue()));
			});
		} finally {
			json.writeEnd();
		}
	}
	
	private final Fld<RecT, ValT> valFld;
}

