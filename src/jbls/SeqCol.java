package jbls;

import java.util.stream.Stream;

import javax.json.stream.JsonGenerator;

public class SeqCol<RecT extends Rec, ValT> 
extends Col<RecT, Stream<ValT>>{
	public SeqCol(final Fld<RecT, ValT> vf) {
		super(vf.name);
		valFld = vf;
	}
	
	@Override
	public SeqCol<RecT, ValT> read(final Reader<RecT, Stream<ValT>> r) {
		super.read(r);
		return this;
	}

	@Override
	public void writeJson(final Stream<ValT> vs, final JsonGenerator json) {
		json.writeStartArray(name);
		
		try {
			vs.forEach((v) -> json.write(valFld.toJson(v)));
		} finally {
			json.writeEnd();
		}
	}
	
	private final Fld<RecT, ValT> valFld;
}
