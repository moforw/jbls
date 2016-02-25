package jbls;

import java.util.Collection;

import javax.json.stream.JsonGenerator;

public class SeqCol<RecT extends Rec, ValT> 
extends Col<RecT, Collection<ValT>>{
	public SeqCol(final Fld<RecT, ValT> vf) {
		super(vf.name);
		valFld = vf;
	}
	
	@Override
	public SeqCol<RecT, ValT> read(final Reader<RecT, Collection<ValT>> r) {
		super.read(r);
		return this;
	}

	@Override
	public void writeJson(final Collection<ValT> vs, final JsonGenerator json) {
		json.writeStartArray(name);
		
		try {
			vs.stream().forEach((v) -> json.write(valFld.toJson(v)));
		} finally {
			json.writeEnd();
		}
	}
	
	private final Fld<RecT, ValT> valFld;
}
