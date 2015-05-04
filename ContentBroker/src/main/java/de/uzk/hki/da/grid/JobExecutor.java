package de.uzk.hki.da.grid;

import de.uzk.hki.da.model.CopyJob;

public interface JobExecutor {

	
	public abstract boolean execute(CopyJob job);
}
