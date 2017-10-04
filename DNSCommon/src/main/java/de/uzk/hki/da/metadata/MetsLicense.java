package de.uzk.hki.da.metadata;

import java.util.Comparator;


public class MetsLicense implements Comparable<MetsLicense>{
	public static String USE_AND_REP_TYPE="use and reproduction";
	/**
	 * Comparator sorts objects and put null-Object at the end.
	 */
	public static Comparator<MetsLicense> LicenseNullLastComparator=new Comparator<MetsLicense>(){
		public int compare(MetsLicense o1, MetsLicense o2) {
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
	};
	private String displayLabel;
	private String href;
	private String type;
	private String text;
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
	
	public MetsLicense() {
		super();
	}	
	
	public MetsLicense(String href,String displayLabel, String text) {
		this(USE_AND_REP_TYPE,href,displayLabel,text);
	}
	
	public MetsLicense(String type, String href,String displayLabel, String text) {
		super();
		this.displayLabel = displayLabel;
		this.href = href;
		this.text = text;
		this.type = type;
	}
	public String getDisplayLabel() {
		return displayLabel;
	}
	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
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
		return "MetsLicense [href=" + href + ", displayLabel=" + displayLabel + ", type=" + type + ", text=" + text+ "]";
	}
	

	public int compareTo(MetsLicense o) {
		return toString().compareTo(o.toString());
	}
	
}