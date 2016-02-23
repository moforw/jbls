package jbls;

import java.time.Instant;
import java.util.Optional;
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

	default <RecT> int compareTo(final RecT other, final Def<RecT> d) {
		@SuppressWarnings("unchecked")
		Optional<Integer> res = d.cols()
			.map((c) -> c.compareRecs((RecT)this, other))
			.filter((r) -> r != 0)
			.findFirst();
		
		return (res.isPresent()) ? res.get() : 0; 
	}	
}
