package jbls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.FileSystems;
import java.time.Instant;
import java.util.UUID;

import org.junit.Test;

public class BasicRec implements Rec {
	public BasicRec(UUID i) {
		id = i;
		insTime = Instant.now();
		upTime = insTime;
		rev = 0;
	}
	
	public final UUID id;
	public final Instant insTime;

	@Override
	public boolean equals(final Object other) {
		return id.equals(((BasicRec)other).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public UUID id() {
		return id;
	}
	
	@Override
	public Instant insTime() {
		return insTime;
	}

	@Override
	public int rev() {
		return rev;
	}

	@Override
	public void setRev(final int r) {
		rev = r;
	}
	
	@Override
	public void setUpTime(final Instant t) {
		upTime = t;
	}
	
	public Instant upTime() {
		return upTime;
	}
	
	protected int rev;
	protected Instant upTime;
	
	public static class Tests {		
		public static class Customer extends BasicRec {
			public static class T extends Tbl<Customer> {		
				public final StringCol<Customer> Name = stringCol("name")
					.read((a)     -> a.name)
					.write((a, v) -> a.name = v);
				
				public T(final String n) {
					super(n);
				}
				
				@Override
				protected Customer newRec(final UUID id) {
					return new Customer(id);
				}
			}
			
			public static final T table = new T("customers");		

			public Customer(UUID i) {
				super(i);
			}
			
			private String name;
		}
		
		public static final DB db = 
			new DB(FileSystems.getDefault().getPath("./testdb/"));
		
		@Test
		public void testInsUpDel() {
			Customer c = Customer.table.ins(db);
			assertNotNull(c.id);
			assertTrue(c.insTime.compareTo(Instant.now()) <= 0);
			assertTrue(c.upTime.compareTo(Instant.now()) <= 0);
			assertEquals(1, c.rev);
			assertEquals(c, db.tempTbl(Customer.table).get(c.id, db));
			assertEquals(c, Customer.table.get(c.id, db));	
			Customer.table.up(c, db);
			assertEquals(2, c.rev);
			assertTrue(c.upTime.compareTo(c.insTime) >= 0);
			assertEquals(c, db.tempTbl(Customer.table).get(c.id, db));	
			Customer.table.del(c, db);
			assertEquals(2, c.rev);
			assertNull(db.tempTbl(Customer.table).get(c.id, db));	
		}

		@Test
		public void testCommit() {
			Customer c = Customer.table.ins(db);
			db.commit();
			assertNull(db.tempTbl(Customer.table).get(c.id, db));	
			assertEquals(c, Customer.table.get(c.id, db));	
			
			Customer.table.del(c, db);
			db.commit();
			assertNull(db.tempTbl(Customer.table).get(c.id, db));	
			assertNull(Customer.table.get(c.id, db));	
		}

		@Test
		public void testTrans() {
			Customer c;
			try(final Trans tr = db.trans()) {
				c = Customer.table.ins(db);
			}
			
			assertNull(Customer.table.get(c.id, db));	
		}	
	}
}
