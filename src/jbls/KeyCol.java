package jbls;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.json.stream.JsonGenerator;

public class KeyCol<RecT> extends Col<RecT, Key> {
	public enum KeyType { PRIVATE, PUBLIC };
	
	public final KeyType keyType;
	
	public KeyCol(final KeyType kt, final String n) {
		super(n);
		keyType = kt;
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
		final KeyFactory kf;
		
		try {
			kf = KeyFactory.getInstance("RSA");
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		final BigInteger mod;
		final BigInteger exp;
		
		if (keyType == KeyType.PRIVATE) {
			try {
				final RSAPrivateKeySpec spec = kf.getKeySpec(v, RSAPrivateKeySpec.class);
				mod = spec.getModulus();
				exp = spec.getPrivateExponent();
			} catch (final InvalidKeySpecException e) {
				throw new RuntimeException(e);
			}
			
		} else {
			try {
				final RSAPublicKeySpec spec = kf.getKeySpec(v, RSAPublicKeySpec.class);
				mod = spec.getModulus();
				exp = spec.getPublicExponent();
			} catch (final InvalidKeySpecException e) {
				throw new RuntimeException(e);
			}
		}

		final StringBuilder buf = new StringBuilder();
	    buf.append(mod);
	    buf.append(exp);		
		
	    json.write(name, buf.toString());
	}
}