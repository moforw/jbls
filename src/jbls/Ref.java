package jbls;

import java.util.UUID;

public class Ref<RecT extends Rec, RefT extends Rec> {
	public Ref(final RefCol<RecT, RefT> c) {
		col = c;
	}

	public RefT get() {
		if (rec == null) {
			rec = col.refTbl.get(id); 
		}
		
		return rec;
	}
	
	public UUID id() {
		return id;
	}

	public RefT rec() {
		return rec;
	}

	public void set(final UUID nid){
		if (rec != null && rec.id() != nid) {
			rec = null;
		}
	}

	public void set(final RefT r) {
		rec = r;
		id = (r == null) ? null : r.id();
	}

	private UUID id;
	private RefT rec;
	private final RefCol<RecT, RefT> col;
}
