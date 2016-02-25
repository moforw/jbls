package jbls;

import java.math.BigDecimal;

import javax.json.stream.JsonGenerator;

public class DeciCol<RecT> extends Col<RecT, BigDecimal> {
	public DeciCol(final String n) {
		super(n);
	}

	@Override
	public DeciCol<RecT> read(final Reader<RecT, BigDecimal> r) {
		super.read(r);
		return this;
	}

	@Override
	public DeciCol<RecT> write(final Writer<RecT, BigDecimal> w) {
		super.write(w);
		return this;
	}
	
	@Override
	public void writeJson(final BigDecimal v, final JsonGenerator json) {
		json.write(name, v);
	}
}