package jbls;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

public class KeyCol<RecT> extends Col<RecT, Key> {
	public enum KeyType { PRIVATE, PUBLIC };
	
	public final KeyType keyType;
	
	public KeyCol(final KeyType kt, final String n) {
		super(n);
		keyType = kt;
	}

	@Override
	public Key fromJson(final String v) {
		throw new RuntimeException("Not supported!");
	}
	
	@Override
	public void load(final RecT rec, final JsonObject json) {
		final JsonArray jsa = json.getJsonArray(name);
		
		final byte[] bs = new byte[jsa.size()];
		
		int i = 0;
		for (final JsonValue v: jsa) {
			bs[i] = (byte)((JsonNumber)v).intValue();
			i++;
		};
		
		try {
			final Key k = (keyType == KeyType.PRIVATE)
				? KeyFactory.getInstance("RSA").generatePrivate(
					new PKCS8EncodedKeySpec(bs))
				: KeyFactory.getInstance("RSA").generatePublic(
					new X509EncodedKeySpec(bs));
			
			setVal(rec, k);
		} catch (final InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
		
	@Override
	public KeyCol<RecT> read(final Reader<RecT, Key> r) {
		super.read(r);
		return this;
	}

	@Override
	public KeyCol<RecT> write(final Writer<RecT, Key> w) {
		super.write(w);
		return this;
	}
	
	@Override
	public void writeJson(final Key v, final JsonGenerator json) {		
	    json.writeStartArray(name);
	    try {
	    	for (final byte b: v.getEncoded()) {
	    		json.write(b);
	    	}
	    } finally {
	    	json.writeEnd();
	    }
	}
}