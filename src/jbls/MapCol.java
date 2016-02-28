package jbls;

import java.util.Map;

import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;

public class MapCol<RecT extends Rec, KeyT, ValT> 
extends Col<RecT, Map<KeyT, ValT>>{
	public MapCol(final Fld<RecT, KeyT> kf, final Fld<RecT, ValT> vf) {
		super(kf.name + vf.name);
		keyFld = kf;
		valFld = vf;
	}
	
	@Override
	public Map<KeyT, ValT> fromJson(final String v) {
		throw new RuntimeException("Not supported!");
	}
	
	@Override
	public void load(final Rec rec, final JsonObject json) {
		final JsonObject jso = json.getJsonObject(name);
		@SuppressWarnings("unchecked")
		final Map<KeyT, ValT> m = getVal((RecT)rec);

		jso.keySet().forEach((k) -> {
			m.put(keyFld.fromJson(k), valFld.readJson(jso, k));
		});		
	}

	@Override
	public MapCol<RecT, KeyT, ValT> read(final Reader<RecT, Map<KeyT, ValT>> r) {
		super.read(r);
		return this;
	}

	@Override
	public void writeJson(final Map<KeyT, ValT> vs, final JsonGenerator json) {
		json.writeStartObject(name);
		
		try {
			vs.entrySet().forEach((e) -> {
				json.write(keyFld.toJson(e.getKey()), valFld.toJson(e.getValue()));
			});
		} finally {
			json.writeEnd();
		}
	}
	
	private final Fld<RecT, KeyT> keyFld;
	private final Fld<RecT, ValT> valFld;
}

