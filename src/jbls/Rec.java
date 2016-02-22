package jbls;

import java.time.Instant;
import java.util.UUID;

public interface Rec extends Comparable<Rec> {
	UUID id();
	Instant insTime();
	int rev();
	Instant upTime();

	void setRev(final int r);
	void setUpTime(final Instant t);
	
	@Override
	default int compareTo(final Rec other) {
		return id().compareTo(other.id());
	}	
}
