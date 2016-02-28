package jbls;

import javax.json.stream.JsonGenerator;

public class LongCol<RecT> extends Col<RecT, Long> {
	public LongCol(final String n) {
		super(n);
	}

	@Override
	public Long fromJson(final String v) {
        return Long.valueOf(v);
	}	
	
	@Override
	public LongCol<RecT> read(final Reader<RecT, Long> r) {
		super.read(r);
		return this;
	}

	@Override
	public LongCol<RecT> write(final Writer<RecT, Long> w) {
		super.write(w);
		return this;
	}
	
	@Override
	public void writeJson(final Long v, final JsonGenerator json) {
		json.write(name, v);
	}
}