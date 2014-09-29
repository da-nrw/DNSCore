package de.uzk.hki.da.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.uzk.hki.da.core.ConfigurationException;

/**
 * @author Jens Peters
 * Builds List of all available Actions
 *
 */
/**
 * @author Daniel M. de Oliveira
 *
 */
public class ActionInformation implements ApplicationContextAware {

	private ActionRegistry actionRegistry;
	
	private List<String> availableJobTypes = null;
	
	private List<ActionDescription> actionInformation = null; 
	

	/** The context. */
	private ApplicationContext context;
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ActionInformation.class);
	
	
	ActionInformation() {
	}
	
	@SuppressWarnings("unused")
	private void init(){
		if (context == null) throw new ConfigurationException("Unable to build action. Application context has not been set.");
		if (actionRegistry == null) throw new ConfigurationException("actionRegistry not set!");
		availableJobTypes = actionRegistry.getAvailableJobTypes();
		logger.trace("available job types: " + availableJobTypes);
		actionInformation = new ArrayList<ActionDescription>();
		for (String jobType : availableJobTypes) {
			ActionDescription ad = new ActionDescription();
			AbstractAction action = (AbstractAction) context.getBean(jobType);
			ad.setDescription(action.getDescription());
			ad.setActionType(jobType);
			ad.setStartStatus(action.getStartStatus());
			ad.setEndStatus(action.getEndStatus());		
			actionInformation.add(ad);
		}
	}
	
	public ActionDescription findStateInActionList(String status) {
		int searchState = Integer.parseInt(status);
		logger.trace("Searching actions for state " + searchState + " in "+ actionInformation.size() + " known actions");
		for (ActionDescription ad : actionInformation) {
				if (Integer.parseInt(ad.getStartStatus())<=searchState &&
						Integer.parseInt(ad.getEndStatus())>=searchState)
						return ad;
		} 
		logger.error("Given state of " +status + " not found in list of actions");
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
		
	}

	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public void setActionRegistry(ActionRegistry actionRegistry) {
		this.actionRegistry = actionRegistry;
	}

	public List<String> getAvailableJobTypes() {
		return availableJobTypes;
	}

	public void setAvailableJobTypes(List<String> availableJobTypes) {
		this.availableJobTypes = availableJobTypes;
	}
	
	
}
