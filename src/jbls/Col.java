package jbls;

import java.util.stream.Stream;

public class Col<RecT, ValT> extends Fld<RecT, ValT> implements Def<RecT> {
	public Col(final String n) {
		super(n);
	}
	
	public Stream<Col<RecT, ?>> cols() {
		return Stream.of(this);
	}
	
}
