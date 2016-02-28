package jbls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Stream;

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
		public static final DB db = 
				new DB(FileSystems.getDefault().getPath("./testdb/"));

		public static class Customer extends BasicRec {
			public static class T extends Tbl<Customer> {		
				public final StrCol<Customer> Name = strCol("name")
					.read((c)     -> c.name)
					.write((c, v) -> c.name = v);

				public final MapCol<Customer, Instant, UUID> Orders = 
					mapCol(new TimeCol<Customer>("orderTime"), 
						new IdCol<Customer>("Lookup"))
					.read((c)     -> c.orderLookup);
				
				public T(final String n) {
					super(n);
				}
				
				@Override
				protected Customer newRec(final UUID id) {
					return new Customer(id);
				}
			}
			
			public static final T tbl = new T("customers");		

			public String name;
			
			public Customer(UUID i) {
				super(i);
			}
			
			public Order newOrder() {
				Order o = Order.tbl.ins(db);
				o.cust.set(this);
				orderLookup.put(o.insTime, o.id);
				return o;
			}
			
			public Stream<Order> orders() {
				return orderLookup
					.values()
					.stream()
					.map((id) -> Order.tbl.get(id, db));
			}
		
			private Map<Instant, UUID> orderLookup = new TreeMap<>();
		}

		public static class Order extends BasicRec {
			public static class T extends Tbl<Order> {				
				public final DeciCol<Order> TotAmnt = deciCol("totAmnt")
					.read((a)     -> a.totAmnt)
					.write((a, v) -> a.totAmnt = v);

				public final RefCol<Order, Customer> Cust = 
					refCol("cust", Customer.tbl)
					.read((o)     -> o.cust);

				public final SeqCol<Order, UUID> Items = 
					seqCol(new IdCol<Order>("items"))
					.read((i)     -> i.items);
			
				public T(final String n) {
					super(n);
				}
				
				@Override
				protected Order newRec(final UUID id) {
					return new Order(id);
				}
			}
			
			public static final T tbl = new T("orders");		

			public Ref<Order, Customer> cust = new Ref<>(tbl.Cust); 
			public List<UUID> items = new ArrayList<>();
			
			public Order(UUID i) {
				super(i);
			}			

			public Item newItem(final Product p, final BigDecimal a) {
				Item i = Item.tbl.ins(db);
				i.owner.set(this);
				i.prod = p;
				i.amnt = a;
				items.add(i.id);
				totAmnt = totAmnt.add(a);
				return i;
			}
			
			public Stream<Item> items() {
				return items
					.stream()
					.map((id) -> Item.tbl.get(id, db));
			}	

			private BigDecimal totAmnt = BigDecimal.ZERO;
		}

		public static class Product extends BasicRec {
			public static class T extends Tbl<Product> {				
				public final StrCol<Product> Name = strCol("name")
					.read((p)     -> p.name)
					.write((p, v) -> p.name = v);

				public T(final String n) {
					super(n);
				}
				
				@Override
				protected Product newRec(final UUID id) {
					return new Product(id);
				}
			}
			
			public static final T tbl = new T("prods");		

			public String name;
						
			public Product(UUID i) {
				super(i);
			}			
		}

		public static class Item extends BasicRec {
			public static class T extends Tbl<Item> {				
				public final DeciCol<Item> Amnt = deciCol("amnt")
					.read((i)     -> i.amnt)
					.write((i, v) -> i.amnt = v);

				public final RefCol<Item, Order> Owner = 
					refCol("owner", Order.tbl)
					.read((i)     -> i.owner);

				public final RecCol<Item, Product> Prod = recCol("prod", Product.tbl)
					.read((i)     -> i.prod)
					.write((i, v) -> i.prod = v);
				
				
				public T(final String n) {
					super(n);
				}
				
				@Override
				protected Item newRec(final UUID id) {
					return new Item(id);
				}
			}
			
			public static final T tbl = new T("items");		
			
			public final Ref<Item, Order> owner = new Ref<>(tbl.Owner); 
					
			public Item(UUID i) {
				super(i);
			}			

			private BigDecimal amnt;
			private Product prod; 
		}
		
		@Test
		public void testInsUpDel() {
			Customer c = Customer.tbl.ins(db);
			assertNotNull(c.id);
			assertTrue(c.insTime.compareTo(Instant.now()) <= 0);
			assertTrue(c.upTime().compareTo(Instant.now()) <= 0);
			assertEquals(1, c.rev());
			assertEquals(c, db.tempTbl(Customer.tbl).get(c.id, db));
			assertEquals(c, Customer.tbl.get(c.id, db));	
			Customer.tbl.up(c, db);
			assertEquals(2, c.rev());
			assertTrue(c.upTime().compareTo(c.insTime) >= 0);
			assertEquals(c, db.tempTbl(Customer.tbl).get(c.id, db));	
			Customer.tbl.del(c, db);
			assertEquals(2, c.rev());
			assertNull(db.tempTbl(Customer.tbl).get(c.id, db));	
		}

		@Test
		public void testIsDirty() {
			Customer c = Customer.tbl.ins(db);
			assertTrue(db.isDirty(Customer.tbl, c));

			db.commit();
			assertFalse(db.isDirty(Customer.tbl, c));
			
			Customer.tbl.up(c, db);
			assertTrue(db.isDirty(Customer.tbl, c));

			db.commit();
			assertFalse(db.isDirty(Customer.tbl, c));			
		}

		@Test
		public void testCommit() {
			Customer c = Customer.tbl.ins(db);
			db.commit();
			assertNull(db.tempTbl(Customer.tbl).get(c.id, db));	
			assertEquals(c, Customer.tbl.get(c.id, db));	
			
			Customer.tbl.del(c, db);
			db.commit();
			assertNull(db.tempTbl(Customer.tbl).get(c.id, db));	
			assertNull(Customer.tbl.get(c.id, db));	
		}

		@Test
		public void testGetDel() {
			Customer c = Customer.tbl.ins(db);
			db.commit();
			Customer.tbl.del(c, db);
			assertTrue(db.isDel(Customer.tbl, c.id));				
			assertNull(Customer.tbl.get(c.id, db));				
		}

		@Test
		public void testPrevOffs() {
			Customer c = Customer.tbl.ins(db);
			db.commit();			
			long o = Customer.tbl.offs(c);
			assertNotEquals(-1, o);
			
			Customer.tbl.up(c, db);
			db.commit();
			assertEquals(o, c.prevOffs());
			assertNotEquals(-1, Customer.tbl.offs(c));
			assertNotEquals(o, Customer.tbl.offs(c));
		}

		@Test
		public void testMapCol() {
			Customer c = Customer.tbl.ins(db);
			Order o = c.newOrder();
			Product p = Product.tbl.ins(db);
			p.name = "Foo";
			o.newItem(p, BigDecimal.valueOf(1000));
			db.commit();
			
			assertEquals(o, Order.tbl.get(o.id, db));	
			assertEquals(o, c.orders().findFirst().get());
		}
		
		@Test
		public void testTrans() {
			Customer c;
			try(final Trans tr = db.trans()) {
				c = Customer.tbl.ins(db);
			}
			
			assertNull(Customer.tbl.get(c.id, db));	
		}	
	}
}
