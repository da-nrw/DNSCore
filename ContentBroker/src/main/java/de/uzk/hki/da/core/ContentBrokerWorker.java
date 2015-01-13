package de.uzk.hki.da.core;

import org.slf4j.MDC;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;

import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.action.ActionFactory;

public class ContentBrokerWorker extends Worker{

	/** The task executor. */
	private TaskExecutor taskExecutor;
	
	/** The action factory. */
	private ActionFactory actionFactory;
	
	public void setMDC(){
		MDC.put(WORKER_ID, "contentbroker");
	}
	
	/**
	 * Schedule task.
	 */
	@Override
	public void scheduleTaskImplementation() {
		
		logger.trace("scheduling task");		
		try {
			AbstractAction action = getActionFactory().buildNextAction();
			if(action != null) {
				logger.debug("executing... "+action.getName());
				getTaskExecutor().execute(action);
				
			}
		} catch (TaskRejectedException e) {
			logger.warn("Task rejected!",e);
		} catch (Exception e) {
			logger.error("Exception while scheduling task", e);
		}
	}
	
	
	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
	public ActionFactory getActionFactory() {
		return actionFactory;
	}
	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}
}
