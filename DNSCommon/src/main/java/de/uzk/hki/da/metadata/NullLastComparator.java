package de.uzk.hki.da.metadata;

import java.util.Comparator;

/**
 * Comparator sorts objects and put null-Object at the end.
 */
public class NullLastComparator<T extends Comparable<T>> implements Comparator<T>{

	public int compare(T o1, T o2) {
		if(o1 == null){
			if(o2 == null){
				return 0;
			}else
				return 1;
		}
		if(o2 == null)
			return -1;
		return o1.compareTo(o2);
	}

}
