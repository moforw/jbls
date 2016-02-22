package jbls;

public interface Reader<RecT, ValT> {
	public ValT val(final RecT r);
}
