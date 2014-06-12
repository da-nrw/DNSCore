package de.uzk.hki.da.cb;

import java.io.FileNotFoundException;
import java.io.IOException;

import javassist.expr.NewArray;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;

import de.uzk.hki.da.core.ConfigurationException;
import de.uzk.hki.da.core.IngestGate;
import de.uzk.hki.da.grid.DistributedConversionAdapter;
import de.uzk.hki.da.utils.Path;

/**
 * Fetches the PIPs from the nodes on which they've originally been created.
 * Overwrites any previously generated PIPs.
 * 
 * @author Daniel M. de Oliveira
 */
public class FetchPIPsAction extends AbstractAction {

	private IngestGate ingestGate;
	
	private DistributedConversionAdapter distributedConversionAdapter;
	
	Path publicContractorFolder = null;
	Path institutionContractorFolder = null;
	
//	Prefix for FetchPIPsActionTest
	String contractorFolderPrefix = "";
//	
	
	@Override
	boolean implementation() throws FileNotFoundException, IOException {
		if (distributedConversionAdapter==null) throw new ConfigurationException("irodsSystemConnector not set");
		
		publicContractorFolder = new Path("pips", "public", object.getContractor().getShort_name());
		institutionContractorFolder = new Path("pips", "institution", object.getContractor().getShort_name());
		String sourceDIPName = object.getIdentifier()+"_"+object.getLatestPackage().getId();
		
		replicateFromSourceResourceToWorkingResource(sourceDIPName);
		
		
		// the rename is necessary because at the moment we donl't have another possibility to delete or trim the irods
		// collections on specific resources.
		deletePreviousPIPs(object.getIdentifier());
		renamePIPs(sourceDIPName, object.getIdentifier());
		
		// cleanup
		distributedConversionAdapter.remove(new Path(publicContractorFolder,sourceDIPName).toString());
		distributedConversionAdapter.remove(new Path(institutionContractorFolder,sourceDIPName).toString());

		return true;
	}



	/**
	 * @param dipTargetPartialPath
	 * @throws IOException
	 */
	private void deletePreviousPIPs(String targetDIPName) throws IOException{
		
		if (new Path(localNode.getWorkAreaRootPath(),publicContractorFolder,targetDIPName).toFile().exists());
			FileUtils.deleteDirectory(new Path(
					localNode.getWorkAreaRootPath(),publicContractorFolder,targetDIPName).toFile());
		if (new Path(localNode.getWorkAreaRootPath(),institutionContractorFolder,targetDIPName).toFile().exists())
			FileUtils.deleteDirectory(new Path(
					localNode.getWorkAreaRootPath(),institutionContractorFolder, targetDIPName).toFile());
	}



	/**
	 * @param sourceDIPName
	 * @param targetDIPName
	 * @throws IOException
	 */
	private void renamePIPs(String sourceDIPName,
			String targetDIPName) throws IOException {
		
		if (new Path(localNode.getWorkAreaRootPath(),publicContractorFolder.toString(),sourceDIPName).toFile().exists())
			FileUtils.moveDirectory(
					new Path(localNode.getWorkAreaRootPath(),publicContractorFolder,sourceDIPName).toFile(), 
					new Path(localNode.getWorkAreaRootPath(),publicContractorFolder,targetDIPName).toFile());
		if (new Path(localNode.getWorkAreaRootPath(),institutionContractorFolder,sourceDIPName).toFile().exists())
			FileUtils.moveDirectory(
					new Path(localNode.getWorkAreaRootPath(),institutionContractorFolder,sourceDIPName).toFile(), 
					new Path(localNode.getWorkAreaRootPath(),institutionContractorFolder,targetDIPName).toFile());
	}



	/**
	 * @author Daniel M. de Oliveira
	 * @author Jens Peters
	 * @param dipSourcePartialPath
	 */
	private void replicateFromSourceResourceToWorkingResource(
			String dipSourcePartial) {
//		System.out.println("replicateFromSourceResourceToWorkingResource...");
//		System.out.println("Replicate from "+(new Path(localNode.getWorkAreaRootPath(),publicContractorFolder,dipSourcePartial).toFileWithoutFirstFileSeparator())+ " to " +
//				new Path(contractorFolderPrefix, publicContractorFolder,dipSourcePartial).toStringWithoutFirstFileSeparator());
//		System.out.println("replDir exists: " +(new Path(localNode.getWorkAreaRootPath(),publicContractorFolder,dipSourcePartial).toFileWithoutFirstFileSeparator()).exists());
//		System.out.println("source exists: " +new Path(contractorFolderPrefix, publicContractorFolder,dipSourcePartial).toFileWithoutFirstFileSeparator().exists());
//		System.out.println("Replicate from "+ new Path(localNode.getWorkAreaRootPath(),institutionContractorFolder,dipSourcePartial).toFileWithoutFirstFileSeparator() + " to " + 
//				new Path(contractorFolderPrefix, institutionContractorFolder,dipSourcePartial).toStringWithoutFirstFileSeparator());
		
//		TODO check if source exists
			distributedConversionAdapter.replicateToLocalNode(
					new Path(contractorFolderPrefix, publicContractorFolder,dipSourcePartial).toStringWithoutFirstFileSeparator());
//		TODO check if source exists
//		if (new Path(localNode.getWorkAreaRootPath(),institutionContractorFolder,dipSourcePartial).toFileWithoutFirstFileSeparator().exists()) 
			 distributedConversionAdapter.replicateToLocalNode(
				new Path(contractorFolderPrefix, institutionContractorFolder,dipSourcePartial).toStringWithoutFirstFileSeparator());
	}
	
	
	@Override
	void rollback() throws Exception {
		throw new NotImplementedException("No rollback implemented for this action");
	}



	public DistributedConversionAdapter getDistributedConversionAdapter() {
		return distributedConversionAdapter;
	}



	public void setDistributedConversionAdapter(
			DistributedConversionAdapter distributedConversionAdapter) {
		this.distributedConversionAdapter = distributedConversionAdapter;
	}



	public IngestGate getIngestGate() {
		return ingestGate;
	}



	public void setIngestGate(IngestGate ingestGate) {
		this.ingestGate = ingestGate;
	}
}
