package jbls;

import java.util.Map;
import java.util.stream.Stream;

import javax.json.stream.JsonGenerator;

public class MapCol<RecT extends Rec, KeyT, ValT> 
extends Col<RecT, Stream<Map.Entry<KeyT, ValT>>>{
	public MapCol(final Fld<RecT, KeyT> kf, final Fld<RecT, ValT> vf) {
		super(kf.name + vf.name);
		keyFld = kf;
		valFld = vf;
	}
	
	@Override
	public MapCol<RecT, KeyT, ValT> read(final Reader<RecT, Stream<Map.Entry<KeyT, ValT>>> r) {
		super.read(r);
		return this;
	}

	@Override
	public void writeJson(final Stream<Map.Entry<KeyT, ValT>> vs, final JsonGenerator json) {
		json.writeStartObject(name);
		
		try {
			vs.forEach((e) -> json.write(keyFld.toJson(e.getKey()), valFld.toJson(e.getValue())));
		} finally {
			json.writeEnd();
		}
	}
	
	private final Fld<RecT, KeyT> keyFld;
	private final Fld<RecT, ValT> valFld;
}

