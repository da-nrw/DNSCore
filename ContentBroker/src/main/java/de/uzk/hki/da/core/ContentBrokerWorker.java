package de.uzk.hki.da.core;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import de.uzk.hki.da.action.AbstractAction;
import de.uzk.hki.da.action.ActionFactory;

public class ContentBrokerWorker extends Worker{

	/** The task executor. */
	private TaskExecutor taskExecutor;
	
	/** The action factory. */
	private ActionFactory actionFactory;
	
	/**
	 * Schedule task.
	 */
	public void scheduleTask() {
		
		MDC.put("worker_id", "contentbroker");
		
		ch.qos.logback.classic.Logger logger =
				(ch.qos.logback.classic.Logger) LoggerFactory.getLogger("de.uzk.hki.da.core");
		Appender<ILoggingEvent> appender = logger.getAppender("WORKER");
		
		if (appender != null)
			appender.start();
		
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

		
		if (appender != null)
			appender.stop();
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
