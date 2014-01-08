package de.uzk.hki.da.cb;

import java.io.File;
import java.io.IOException;

import de.uzk.hki.da.core.LoadBalancer;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.grid.GridFacade;
import de.uzk.hki.da.service.RetrievePackagesHelper;

public class ObjectToWorkAreaAction extends AbstractAction {

	private LoadBalancer loadBalancer;
	private GridFacade gridFacade;
	private DistributedConversionAdapter distributedConversionAdapter;
	
	
	@Override
	boolean implementation() {
		object.reattach();
		
		new File(object.getDataPath()).mkdirs();
		
		RetrievePackagesHelper retrievePackagesHelper = new RetrievePackagesHelper();
		
		try {
			if (!loadBalancer.canHandle(retrievePackagesHelper.getObjectSize(object, job, getGridFacade()))) {
				logger.info("no disk space available at working resource. will not fetch new data.");
				return false;
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to determine object size for object " + object.getIdentifier(), e);
		}
		
		try {
			retrievePackagesHelper.copyPackagesFromLZAToWorkArea(object, getGridFacade(),true);
			retrievePackagesHelper.unpackExistingPackages(object);
		} catch (IOException e) {
			throw new RuntimeException("error while trying to get existing packages from lza area",e);
		}
		
		distributedConversionAdapter.register("fork/"+object.getContractor().getShort_name()+"/"+object.getIdentifier(),
				object.getPath());
		return true;
	}

	
	
	
	
	@Override
	void rollback() throws Exception {}





	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}





	public void setLoadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
	}





	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}





	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}





	public GridFacade getGridFacade() {
		return gridFacade;
	}





	public void setGridFacade(GridFacade gridFacade) {
		this.gridFacade = gridFacade;
	}

}
