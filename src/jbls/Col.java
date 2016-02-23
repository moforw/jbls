package jbls;

import java.util.stream.Stream;

import javax.json.stream.JsonGenerator;

public abstract class Col<RecT, ValT extends Comparable<ValT>> 
extends Fld<RecT, ValT> implements Def<RecT> {
	public Col(final String n) {
		super(n);
	}
	
	public Stream<Col<RecT, ?>> cols() {
		return Stream.of(this);
	}
	
	public abstract void writeJson(final Rec r, final JsonGenerator json);
}
