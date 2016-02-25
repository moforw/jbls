package jbls;

import javax.json.stream.JsonGenerator;

public class RefCol<RecT extends Rec, RefT extends Rec> 
extends Col<RecT, Ref<RecT, RefT>>{
	public final Tbl<RefT> refTbl;

	public RefCol(final String n, final Tbl<RefT> rt) {
		super(n);
		refTbl = rt;
	}
	
	@Override
	public RefCol<RecT, RefT> read(final Reader<RecT, Ref<RecT, RefT>> r) {
		super.read(r);
		return this;
	}

	@Override
	public void writeJson(final Ref<RecT, RefT> ref, final JsonGenerator json) {
		if (ref.id() == null) {
			json.writeNull(name);
		} else {
			json.write(name, refTbl.Id.toJson(ref.id()));
		}
	}
}
