package jbls;

import java.time.Instant;
import java.util.UUID;

public interface Rec extends Comparable<Rec> {
	UUID id();
	Instant insTime();
	long prevOffs();
	int rev();
	Instant upTime();

	void setPrevOffs(final long po);
	void setRev(final int r);
	void setUpTime(final Instant t);
	
	@Override
	default int compareTo(final Rec other) {
		return id().compareTo(other.id());
	}	
}
