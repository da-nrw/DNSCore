package de.uzk.hki.da.metadata;

import java.util.Comparator;


public class LidoLicense implements Comparable<LidoLicense>{

	private String term;
	private String href;
	
	/*public static enum Type{
		RESTRICTION_ON_ACCESS_TYPE("restriction on access"),
		USE_AND_REPRODUCTION_TYPE("use and reproduction");
		
		Type(String t){this.type=t;}
		private String type;
		@Override
		public String toString() {
			return type;
		}
	}*/
	
	public LidoLicense() {
		super();
	}	
	
	
	public LidoLicense(String href,String term) {
		super();
		this.term = term;
		this.href = href;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;	
		return toString().equals(obj.toString());
	}

	
	@Override
	public String toString() {
		return "LidoLicense [href=" + href + ", term=" + term + "]";
	}
	

	public int compareTo(LidoLicense o) {
		return toString().compareTo(o.toString());
	}
	
}