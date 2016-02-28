package jbls;

import java.util.UUID;

import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;

public class RefCol<RecT extends Rec, RefT extends Rec> 
extends Col<RecT, Ref<RecT, RefT>>{
	public final Tbl<RefT> refTbl;

	public RefCol(final String n, final Tbl<RefT> rt) {
		super(n);
		refTbl = rt;
	}

	@Override
	public Ref<RecT, RefT> fromJson(final String v) {
		throw new RuntimeException("Not supported!");
	}

	@Override
	public void load(final Rec rec, final JsonObject json) {
		final UUID id = refTbl.Id.fromJson(json.getString(name));
		@SuppressWarnings("unchecked")
		final Ref<RecT, RefT> r = getVal((RecT)rec);
		r.set(id);
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
