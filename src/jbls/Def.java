package jbls;

import java.util.stream.Stream;

public interface Def<RecT> {
	Stream<Col<RecT, ?>> cols();
}
