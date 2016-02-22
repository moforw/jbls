package jbls;

import java.time.Instant;
import java.util.UUID;

public class Rec implements Comparable<Rec> {
	public Rec(UUID i) {
		id = i;
		insTime = Instant.now();
	}
	
	public final UUID id;
	public final Instant insTime;
	
	@Override
	public int compareTo(final Rec other) {
		return id.compareTo(other.id);
	}

	@Override
	public boolean equals(final Object other) {
		return id.equals(((Rec)other).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public Instant upTime() {
		return upTime;
	}
	
	protected Instant upTime;
}
