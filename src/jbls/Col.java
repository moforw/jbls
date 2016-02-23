package jbls;

import javax.json.stream.JsonGenerator;

public abstract class Col<RecT, ValT> extends Fld<RecT, ValT> {
	public Col(final String n) {
		super(n);
	}

	public abstract void writeJson(final Rec r, final JsonGenerator json);
}
