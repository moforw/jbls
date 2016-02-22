package jbls;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.junit.Test;

public class NGram {
	public static BigInteger score(final String str) {
		BigInteger s = BigInteger.ZERO;
		BigInteger f = BigInteger.ONE.shiftLeft(str.length() * Character.BYTES);
		
		for (int i = 0; i < str.length(); i++) {
			s = s.add(f.multiply(BigInteger.valueOf(str.charAt(i))));
			f = f.shiftRight(Character.BYTES);
		}
		
		return s;
	}
	
	public static class Tests {
		public static Random rnd = new Random();
		
		public static String rndStr(final String cs, final int l)
		{
		    char[] res = new char[l];
		    
		    for (int i = 0; i < l; i++)
		    {
		        res[i] = cs.charAt(rnd.nextInt(cs.length()));
		    }
		    
		    return new String(res);
		}

		public static String rndStr(final int l) {
			return rndStr("abcdefghijklmnopqrstuvwxyzåäö0123456789", l);
		}

		@Test
		public void testScore() throws InterruptedException {
			assertTrue(score("abc").compareTo(score("def")) < 0);
			assertTrue(score("abc").compareTo(score("abd")) < 0);
			assertTrue(score("abc").compareTo(score("abcd")) < 0);
		}
		
		@Test
		public void testScorePerf() {
			Set<String> _strings = new HashSet<>();
			Set<BigInteger> _scores = new HashSet<>();
			
			for (int i = 0; i < 1000; i++) {
				_strings.add(rndStr(20));
			}

			for (String s: _strings) {
				_scores.add(score(s));
			}
			
			long start = System.nanoTime();
			Set<String> strings = new ConcurrentSkipListSet<>();
			_strings.parallelStream().forEach((s) -> strings.add(s));			
			long addStrTime = System.nanoTime() - start;
			System.out.println("insert strings: " + addStrTime);
			
			start = System.nanoTime();
			Set<BigInteger> scores = new ConcurrentSkipListSet<>();
			_scores.parallelStream().forEach((s) -> scores.add(s));
			long addScoreTime = System.nanoTime() - start;
			System.out.println("insert scores: " + addScoreTime + " (" + (addScoreTime / (double)addStrTime) + ")");

			start = System.nanoTime();
			_strings.parallelStream().forEach((s) -> strings.contains(s));			
			long findStrTime = System.nanoTime() - start;
			System.out.println("find strings: " + findStrTime);

			start = System.nanoTime();
			_scores.parallelStream().forEach((s) -> scores.contains(s));			
			long findScoreTime = System.nanoTime() - start;
			System.out.println("find scores: " + findScoreTime + " (" + (findScoreTime / (double)findStrTime) + ")");

			start = System.nanoTime();
			_strings.parallelStream().forEach((s) -> strings.remove(s));			
			long removeStrTime = System.nanoTime() - start;
			System.out.println("remove strings: " + removeStrTime);

			start = System.nanoTime();
			_scores.parallelStream().forEach((s) -> scores.remove(s));			
			long removeScoreTime = System.nanoTime() - start;
			System.out.println("remove scores: " + removeScoreTime + " (" + (removeScoreTime / (double)removeStrTime) + ")");
		}
	}
}
