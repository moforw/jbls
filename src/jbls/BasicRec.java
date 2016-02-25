package jbls;

import static org.junit.Assert.*;

import java.nio.file.FileSystems;
import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.junit.Test;

public class BasicRec implements Rec {
	public final UUID id;
	public final Instant insTime;

	public BasicRec(final UUID i) {
		id = i;
		insTime = Instant.now();
		upTime = insTime;
		prevOffs = -1;
		rev = 0;
	}
		
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
	public long prevOffs() {
		return prevOffs;
	}

	@Override
	public int rev() {
		return rev;
	}

	@Override
	public void setPrevOffs(final long po) {
		prevOffs = po;
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
	
	private int rev;
	private Instant upTime;
	private long prevOffs;
	
	public static class Tests {		
		public static class Customer extends BasicRec {
			public static class T extends Tbl<Customer> {		
				public final StringCol<Customer> Name = stringCol("name")
					.read((c)     -> c.name)
					.write((c, v) -> c.name = v);

				public final SeqCol<Customer, UUID> Orders = 
					seqCol(new IdCol<Customer>("orders"))
					.read((c)     -> c.orders);
				
				public T(final String n) {
					super(n);
				}
				
				@Override
				protected Customer newRec(final UUID id) {
					return new Customer(id);
				}
			}
			
			public static final T table = new T("customers");		

			public String name;
			public Set<UUID> orders = new TreeSet<>();

			public Customer(UUID i) {
				super(i);
			}
			
			public Order newOrder() {
				Order o = Order.table.ins(db);
				o.customer = id;
				orders.add(o.id);
				return o;
			}
		}

		public static class Order extends BasicRec {
			public static class T extends Tbl<Order> {				
				public T(final String n) {
					super(n);
				}
				
				@Override
				protected Order newRec(final UUID id) {
					return new Order(id);
				}
			}
			
			public static final T table = new T("orders");		

			public Order(UUID i) {
				super(i);
			}
			
			public UUID customer; 
		}

		public static final DB db = 
			new DB(FileSystems.getDefault().getPath("./testdb/"));
		
		@Test
		public void testInsUpDel() {
			Customer c = Customer.table.ins(db);
			assertNotNull(c.id);
			assertTrue(c.insTime.compareTo(Instant.now()) <= 0);
			assertTrue(c.upTime().compareTo(Instant.now()) <= 0);
			assertEquals(1, c.rev());
			assertEquals(c, db.tempTbl(Customer.table).get(c.id, db));
			assertEquals(c, Customer.table.get(c.id, db));	
			Customer.table.up(c, db);
			assertEquals(2, c.rev());
			assertTrue(c.upTime().compareTo(c.insTime) >= 0);
			assertEquals(c, db.tempTbl(Customer.table).get(c.id, db));	
			Customer.table.del(c, db);
			assertEquals(2, c.rev());
			assertNull(db.tempTbl(Customer.table).get(c.id, db));	
		}

		@Test
		public void testIsDirty() {
			Customer c = Customer.table.ins(db);
			assertTrue(db.isDirty(Customer.table, c));

			db.commit();
			assertFalse(db.isDirty(Customer.table, c));
			
			Customer.table.up(c, db);
			assertTrue(db.isDirty(Customer.table, c));

			db.commit();
			assertFalse(db.isDirty(Customer.table, c));			
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
		public void testGetDel() {
			Customer c = Customer.table.ins(db);
			db.commit();
			Customer.table.del(c, db);
			assertTrue(db.isDel(Customer.table, c.id));				
			assertNull(Customer.table.get(c.id, db));				
		}

		@Test
		public void testPrevOffs() {
			Customer c = Customer.table.ins(db);
			db.commit();			
			long o = Customer.table.offs(c);
			assertNotEquals(-1, o);
			
			Customer.table.up(c, db);
			db.commit();
			assertEquals(o, c.prevOffs());
			assertNotEquals(-1, Customer.table.offs(c));
			assertNotEquals(o, Customer.table.offs(c));
		}

		@Test
		public void testSeqCol() {
			Customer c = Customer.table.ins(db);
			Order o = c.newOrder();
			db.commit();
			
			assertEquals(o, Order.table.get(o.id, db));	
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
