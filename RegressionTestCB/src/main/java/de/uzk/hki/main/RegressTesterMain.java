package de.uzk.hki.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import de.uzk.hki.da.at.AcceptanceTest;
import de.uzk.hki.da.at.AcceptanceTestHelper;

//http://stackoverflow.com/questions/4648341/how-to-export-junit-test-suite-as-executable-jar
//http://stackoverflow.com/questions/2884163/pass-command-line-arguments-to-junit-test-case-being-run-programmatically
//http://stackoverflow.com/questions/36047637/how-can-i-include-test-classes-into-maven-jar-and-execute-them
public class RegressTesterMain {
	public static final File LOCAL_RESOURCE=new File("./src/test/resources/");
	
	public static void main(String[] argv) {
		System.out.println("Start TestMain");
		setLogger();
		
		String testName="CompleteATSuite";
		String testResourcesPath="/ci/DNSCore/ContentBroker/src/test/resources/at/";
		File testResourceFile=new File(testResourcesPath);
		Class testClass=CompleteATSuite.class;		
		
		CommandLineParser parser = new DefaultParser();
		// create the Options
		Options options = new Options();
		options.addOption(new Option( "help", "Print this message" ));
		Option testResourceOption  = new Option ("r","test-resources",true, "Path to the AT test resources directory. Default setting is '"+testResourcesPath+"'");
		Option testNameOption  = new Option ("n","test-name",true, "AT test name. To execute all AT use 'CompleteATSuite'. Default setting is '"+testName+"'");

		options.addOption(testResourceOption);
		options.addOption(testNameOption);
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "ant", options );
		
	    try {
	        CommandLine line;
			line = parser.parse( options, argv );
	        
	        if(line.hasOption( testResourceOption.getOpt() )||line.hasOption( testResourceOption.getLongOpt() )){
	        	testResourcesPath=line.getOptionValue(testResourceOption.getOpt());
	        }

	        if(line.hasOption( testNameOption.getOpt() )||line.hasOption( testNameOption.getLongOpt() )){
	        	testName=line.getOptionValue(testNameOption.getOpt());
	        }
	        

			System.out.println("Used Parameter: test suite: "+testName+"\t test resources dir:"+testResourcesPath); 
	        
	        if(testName.equals("CompleteATSuite"))
	        	testName="de.uzk.hki.main.CompleteATSuite";
	        else
	        	testName="de.uzk.hki.da.at."+testName;
	            
	        testResourceFile=new File(testResourcesPath);
	        //testResourceFile=new File(testResourceFile.getAbsolutePath());
	        if(!testResourceFile.canRead()|| !testResourceFile.isDirectory())
	        	throw new ParseException(testResourceFile+ " unreadable");


			
			testClass=Class.forName(testName);
	    }
	    catch( ClassNotFoundException | ParseException exp ) {
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage()+"\n"+exp.toString() );
	        formatter.printHelp( "ant", options );
	        System.exit(1);
	    }
	    
	    
	    try {
	    	if(LOCAL_RESOURCE.exists()){
	    		//LOCAL_RESOURCE.delete();
	    		FileUtils.deleteDirectory(LOCAL_RESOURCE);
	    	}
		   	LOCAL_RESOURCE.mkdirs();
			Files.createSymbolicLink(Paths.get(LOCAL_RESOURCE.getAbsolutePath()+"/at"), Paths.get(testResourceFile.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    //set important properties, to control junit testcases execution 
		System.setProperty(AcceptanceTestHelper.TEST_RESOURCES_PATH_PROPERTY, testResourcesPath);
		//System.setProperty(AcceptanceTestHelper.NO_CLEANUP_AFTER_EACH_TEST_PROPERTY, "1");
	    
		JUnitCore junit = new JUnitCore();
		junit.addListener(new RunListener(){
			String currentTestClass="";
			int counter=0;

			@Override
			public void testStarted(Description description) throws Exception {
				if(!currentTestClass.equals(description.getTestClass().toString())){
					counter++;
					System.out.println(">>>>>TestStarted: New Test-Case("+counter+"): "+description.getTestClass().toString());
					currentTestClass=description.getTestClass().toString();
					
				}
				System.out.println(">>>>>TestStarted: "+description.toString());
				super.testStarted(description);
			}

			@Override
			public void testFinished(Description description) throws Exception {
				System.out.println(">>>>>TestFinished: "+description.toString());
				super.testFinished(description);
			}

			@Override
			public void testFailure(Failure failure) throws Exception {
				System.out.println(">>>>>TestFailure: "+failure.toString());
				super.testFailure(failure);
			}
			
		});
		Result result = junit.run(testClass); //SuiteMetadataUpdates.class) ATMigrationRight.class CompleteATSuite.class
		System.out.println("Junit Result: "+(result.wasSuccessful()?"SUCCESSFUL":"FAIL")+"\nFailureCount: "+result.getFailureCount()+" | IgnoreCount: "+result.getIgnoreCount());
		System.out.println("RunCount:"+result.getRunCount());
		
		for(Failure fail: result.getFailures()){
			System.out.println("Failure: "+fail.toString());
		}
		
		//final cleanup of Data 
		//AcceptanceTest.cleanStorage();
		//AcceptanceTest.clearDB();
		
		LOCAL_RESOURCE.delete();
		System.exit(0); //otherwise application control is gained by junit threads and application doesn't stop at the end of main
	}
	
	
	private static void setLogger(){
		//Disable logging
		//Set SLF4J
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(ch.qos.logback.classic.Level.INFO);
	    root.detachAndStopAllAppenders();
	    //ch.qos.logback.core.FileAppender fAppender=new ch.qos.logback.core.FileAppender(); 
	    //fAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
	    //fAppender.setFile("RegressionTestSLF4J"+System.currentTimeMillis()+".log");
	    //root.addAppender(fAppender);
	    
	    //Set log4j
		Logger.getRootLogger().setLevel(Level.INFO);
		for(Enumeration e=Logger.getRootLogger().getAllAppenders();e.hasMoreElements();)
			Logger.getRootLogger().removeAppender((Appender)e.nextElement());
		/*Layout layout = new PatternLayout("%d [%t] %-5p %c %x - %m%n");
		try {
			Logger.getRootLogger().addAppender(new FileAppender(layout,"RegressionTest"+System.currentTimeMillis()+".log",true));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	}
}
