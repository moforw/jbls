package jbls;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

import javax.json.JsonString;
import javax.json.JsonValue;

public class DeciCol<RecT> extends Col<RecT, BigDecimal> {
	public static final DecimalFormat fmt = (DecimalFormat)NumberFormat.getInstance();
    
	{
    	fmt.setParseBigDecimal(true);
    }

	
	public DeciCol(final String n) {
		super(n);
	}
	
	@Override
	public BigDecimal fromJson(final JsonValue v) {
        return (BigDecimal)fmt.parse(((JsonString)v).getString(), 
        	new ParsePosition(0));
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
	public String toJson(final BigDecimal v) {
		return v.toString();
	}
}