package de.uzk.hki.da.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name="quality_level_recheck_planner")
public class QualityLevelRecheckPlanner {
	/** The id. */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	/** The Constant logger. */
	static final Logger logger = LoggerFactory.getLogger(QualityLevelRecheckPlanner.class);
	
	@Column(name="filter_from_date_time", columnDefinition="DATE")
	private Date filterFromDateTime;
	
	@Column(name="filter_to_date_time", columnDefinition="DATE")
	private Date filterToDateTime;
	
	/*@OneToMany(cascade=CascadeType.ALL)
	private List<ConversionRoutine> conversionRoutines = new ArrayList<>();
	
	public RecheckFormatPlanner() {}
	
	public List<ConversionRoutine> getConversionRoutines() {
		return conversionRoutines;
	}
	
	public void setConversionRoutines(List<ConversionRoutine> conversionRoutines) {
		this.conversionRoutines = conversionRoutines;
	}
	
	public void addConversionRoutine(ConversionRoutine routine) {
		conversionRoutines.add(routine);
	}
	
	public void removeConversionRoutines(ConversionRoutine routine) {
		conversionRoutines.remove(routine);
	}*/
	
	
}
