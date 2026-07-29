package de.uzk.hki.da.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uzk.hki.da.pkg.SipArchiveBuilder;
import de.uzk.hki.da.sb.Feedback;
import de.uzk.hki.da.sb.ProgressManager;
import de.uzk.hki.da.sb.SIPFactory;

public abstract class ExistingSIPModifier {
	private static Logger logger = LogManager.getLogger( ExistingSIPModifier.class );
	SIPFactory sipFactory;
	SipArchiveBuilder sipArchiveBuilder;

	abstract protected Feedback doModificationOnSIPData(File unpackedSIPDir) throws IOException;
	
	public ExistingSIPModifier() {
		super();
		sipFactory=new SIPFactory();
		sipFactory.setProgressManager(new SimpleLoggerProgressManager());
		sipFactory.setSipBuildingProcess(sipFactory.new SipBuildingProcess(){@Override public void run(){}});
		sipArchiveBuilder=new SipArchiveBuilder();
		sipArchiveBuilder.setProgressManager(new SimpleLoggerProgressManager());
		sipArchiveBuilder.setJobId(0);
		sipArchiveBuilder.setSipBuildingProcess(sipFactory.new SipBuildingProcess(){@Override public void run(){}});
	}

	public static class SimpleLoggerProgressManager extends ProgressManager{
		@Override	public void addJob(int id, String packageName, long folderSize) {logger.info("addJob("+id+","+packageName+","+folderSize+")");}
		@Override	public void reset()  {logger.info("reset()");}
		@Override	public void calculateProgressParts(boolean createCollection) {logger.info("calculateProgressParts("+createCollection+")");}
		@Override	public void copyProgress(int id, long processedData) {logger.info("copyProgress("+id+","+processedData+")");}
		@Override	public void premisProgress(int id, double progress) {logger.info("premisProgress("+id+","+progress+")");}
		@Override	public void bagitProgress(int id, double progress)  {logger.info("bagitProgress("+id+","+progress+")");}
		@Override	public void archiveProgress(int id, long archivedData)  {logger.info("archiveProgress("+id+","+archivedData+")");}
		@Override	public void deleteTempProgress(int id, double progress) {logger.info("deleteTempProgress("+id+","+progress+")");}
		@Override	public void skipJob(int id) {logger.info("skipJob("+id+")");}
		@Override	public void createSuccessMessage(boolean skippedFiles) {logger.info("createSuccessMessage("+skippedFiles+")");}
		@Override	public void setJobFolderSize(int id, long folderSize) {logger.info("setJobFolderSize("+id+","+folderSize+")");}
		@Override	public void abort() {logger.info("abort("+")");}
		@Override	public void createStartMessage() {logger.info("createStartMessage("+")");}
		@Override	public void startJob(int id) {logger.info("startJob("+id+")");}
		
	} 
	
	public Feedback startModifyExistingSip(String sourceSip,String destinationPath) throws IOException {
		Feedback returnValue=Feedback.SUCCESS;

		
		File sourceSipFile=new File(sourceSip);
		File unpackedWorkingDir = new File(destinationPath, "workDirFor-"+this.getClass().getSimpleName());
		File unpackedSIPDir =unpackedWorkingDir;
		boolean compressed=false;
		boolean sourceSipIsDir=new File(sourceSip).isDirectory();
		
		if(unpackedWorkingDir.exists()){
			try {
				FileUtils.deleteDirectory(unpackedWorkingDir);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		unpackedWorkingDir.mkdirs();
		try{
		if(!sourceSipIsDir){
			String extention=sourceSipFile.getName().substring(sourceSipFile.getName().lastIndexOf('.') + 1);
			if(extention.equalsIgnoreCase("tar")){
				try {
					sipArchiveBuilder.unarchiveFolder(sourceSipFile, unpackedWorkingDir, false);
					//unpackedSIPDir=unTar(sourceSipFile,unpackedWorkingDir);
				} catch (Exception e) {
					e.printStackTrace();
					returnValue=Feedback.ARCHIVE_ERROR;
				}
			} else if(extention.equalsIgnoreCase("tgz")){
				compressed=true;
				sipFactory.setCompress(compressed);
				try {
					sipArchiveBuilder.unarchiveFolder(sourceSipFile, unpackedWorkingDir, true);
					if( ! (unpackedWorkingDir.listFiles().length==1 && unpackedWorkingDir.listFiles()[0].isDirectory())){
						logger.error(String.format("Untaring %s to dir %s. is not as expected", sourceSipFile.getAbsolutePath(), unpackedWorkingDir.getAbsolutePath()));
						returnValue=Feedback.ARCHIVE_ERROR;
					}
					unpackedSIPDir=unpackedWorkingDir.listFiles()[0];
					/*File tarFile=unGzip(sourceSipFile,unpackedWorkingDir);
					unpackedSIPDir=unTar(tarFile,unpackedWorkingDir);
					tarFile.delete();*/
				} catch (Exception e) {
					e.printStackTrace();
					returnValue=Feedback.ARCHIVE_ERROR;
				}
			}
		}else{
				unpackedSIPDir=new File(unpackedWorkingDir,sourceSipFile.getName());
				try {
					FileUtils.copyDirectory(sourceSipFile,unpackedSIPDir);
				} catch (IOException e) {
					e.printStackTrace();
					returnValue=Feedback.COPY_ERROR;
				}
			}
		try {
			restructureUnpackedSIP(unpackedSIPDir);
		} catch (IOException e) {
			e.printStackTrace();
			returnValue=Feedback.COPY_ERROR;
		}
		doModificationOnSIPData(unpackedSIPDir);

		if(returnValue==Feedback.SUCCESS){
			returnValue=sipFactory.createBag(0,unpackedSIPDir);
		}
		if(returnValue==Feedback.SUCCESS){
			if(sourceSipIsDir){
				FileUtils.moveDirectory(sourceSipFile, new File(destinationPath,sourceSipFile.getName()));
			}else{
				//packageSip(unpackedSIPDir,new File(destinationPath,sourceSipFile.getName()),compressed);
				sipFactory.buildArchive(0,unpackedSIPDir,new File(destinationPath,sourceSipFile.getName()));
			}
		}
		}finally {
			FileUtils.deleteDirectory(unpackedWorkingDir);
		}
		
		return returnValue;
	}
	

	private static void restructureUnpackedSIP(File unpackedSIPDir) throws IOException{
		final File dataDir=new File(unpackedSIPDir,"data");
		for(File tmp:unpackedSIPDir.listFiles()){
			if(!tmp.equals(dataDir))
				tmp.delete();
		}
		FileUtils.copyDirectory(dataDir, unpackedSIPDir);
		FileUtils.deleteDirectory(dataDir);
	}
	/*
	private static File unGzip(final File inputFile, final File outputDir) throws FileNotFoundException, IOException {
	    logger.info(String.format("Ungzipping %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));
	    final File outputFile = new File(outputDir, inputFile.getName().replace(".tgz", ".tar"));//inputFile.getName().substring(0, inputFile.getName().length() - 4)
	    if(outputFile.exists())
	    	outputFile.delete();
	   
	    FileOutputStream outTar = null;
	    GzipCompressorInputStream in = null ;
	    try{
	    	in = new GzipCompressorInputStream(new FileInputStream(inputFile));
	    	outTar  = new FileOutputStream(outputFile);
	    	IOUtils.copy(in, outTar);
	    }finally{
	    	if(in!=null)
	    		in.close();
	    	if(outTar!=null)
	    		outTar.close();
	    }
	    return outputFile;
	}
	
	private static File unTar(final File inputFile, final File outputDir) throws FileNotFoundException, IOException, ArchiveException {

		logger.info(String.format("Untaring %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));
		String outputDirExtracedSIP=null;
	    final List<File> untaredFiles = new LinkedList<File>();
	    final InputStream is = new FileInputStream(inputFile); 
	    final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
	    TarArchiveEntry entry = null; 
	    while ((entry = (TarArchiveEntry)debInputStream.getNextEntry()) != null) {
	        final File outputFile = new File(outputDir, entry.getName());
	        if (entry.isDirectory()) {
	            logger.info(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath()));
	            if(outputFile.exists() && !outputFile.isDirectory())
	            	outputFile.delete();
	            if (!outputFile.exists()) {
	            	logger.info(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath()));
	                if (!outputFile.mkdirs()) {
	                    throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
	                }
	            }
	            if(outputDirExtracedSIP==null)
	            	outputDirExtracedSIP=outputFile.getAbsolutePath();
	            if(outputDirExtracedSIP.length()>outputFile.getAbsolutePath().length())
	            	outputDirExtracedSIP=outputFile.getAbsolutePath();
	        } else {
	        	logger.info(String.format("Creating output file %s.", outputFile.getAbsolutePath()));
	            final OutputStream outputFileStream = new FileOutputStream(outputFile); 
	            IOUtils.copy(debInputStream, outputFileStream);
	            outputFileStream.close();
	        }
	        untaredFiles.add(outputFile);
	    }
	    debInputStream.close(); 

	    return new File(outputDirExtracedSIP);
	}
	*/
	/*
	public Feedback createBag( File folder) {
		BagFactory bagFactory = new BagFactory();
		PreBag preBag = bagFactory.createPreBag(folder);
		preBag.makeBagInPlace(BagFactory.LATEST, false);

		Bag bag = bagFactory.createBag(folder);

		SimpleResult result = bag.verifyValid();
		if (result.isSuccess()) {
			return Feedback.SUCCESS;
		} else {
			logger.error("Bag in folder " + folder.getAbsolutePath()
					+ " is not valid.\n" + result.getErrorMessages());
			return Feedback.BAGIT_ERROR;
		}
	}
	
	public Feedback packageSip(File srcFolder, File destFile, boolean compress){
		SipArchiveBuilder archiveBuilder=null;
		try {
			archiveBuilder = new SipArchiveBuilder();
			archiveBuilder.setProgressManager(new SimpleLoggerProgressManager());
			archiveBuilder.setJobId(0);
			archiveBuilder.setSipBuildingProcess(sipFactory.new SipBuildingProcess(){@Override public void run(){}});
		} catch (Exception e) {
			logger.error("Failed to instantiate the ArchiveBuilder ", e);
			return Feedback.ABORT;
		}

		try {
			if (!archiveBuilder.archiveFolder(srcFolder, destFile, true,
					compress))
				return Feedback.ABORT;
		} catch (Exception e) {
			logger.error("Failed to archive folder " + srcFolder.getAbsolutePath()
					+ " to archive " + destFile.getAbsolutePath(), e);
			return Feedback.ARCHIVE_ERROR;
		}
		return Feedback.SUCCESS;
	}
	*/
}
