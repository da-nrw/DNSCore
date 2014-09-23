## Application module configuration

${CB_HOME}/conf/beans.xml

Depending on the mode of installation (f,p,n) the beans.xml can look a little different respectively. However here are described the building blocks which comprise the beans.xml:

### The General params section 

#### Ingest Gate

The IngestGate is a special feature which was designed with the purpose of protecting the [WorkArea](processing_stages.md#WorkArea)'s file system from overflow. The ContentBroker's workflow engine recieves orders from various other components that always have the consequence that data from different resources must be fetched to the WorkArea. There the data get unpacked, processed and repacked.

Example from [beans.xml.full](../xml/beans.xml.full)

	<bean class="de.uzk.hki.da.core.IngestGate" id="ingestGate">
		<property name="workAreaRootPath" value="${localNode.workAreaRootPath}" />
		<property name="fileSizeFactor" value="3" />
		<property name="freeDiskSpacePercent" value="5" />
	</bean>

The workAreaRootPath is set automatically to the correspoding variable from config.properties so you don't have to take care of that.

    <property name="freeDiskSpacePercent" value="5" />

This is the value an administrator has to consider setting right for his machine. It defines how many percent of the WorkArea's file system have to be vacant in order to allow the ContentBroker to touch new jobs and fetch the corresponding packages from the IngestArea or long term resources to unpack and process them. When setting up a new node, a good value to start could be 50. When gained a little bit of experience with the ContentBroker on the particular machine, the value later can be refined. 

    <property name="fileSizeFactor" value="3" />

This value is tweakable, but does not depend on the machine but on the workflow and the packages' data. A value of 3 means that it is expected that an incoming package gets (during the Ingest workflow) bloated temporarily to three times its original size. Usually let the value as it is and only modify it when you have strong evidence that overflows of the file system occur due to huge package sizes.

#### Worker scheduler

Example from [beans.xml.full](../xml/beans.xml.full)

	<task:executor id="taskExecutor" pool-size="10"
		queue-capacity="20" rejection-policy="CALLER_RUNS" />
	<task:scheduled-tasks scheduler="taskScheduler">
		<task:scheduled ref="contentBroker" method="scheduleTask" fixed-delay="200" />
		<task:scheduled ref="ingestAreaScannerWorker" method="scheduleTask" fixed-delay="1000" />
		<task:scheduled ref="userAreaScannerWorker" method="scheduleTask" fixed-delay="1000" />
		<task:scheduled ref="integrityScannerWorker" method="scheduleTask" fixed-delay="20000" />
	</task:scheduled-tasks>
	<task:scheduler id="taskScheduler" pool-size="20" />

The ContentBroker has a scheduling mechanism which starts worker threads periodically.


### The action engine related settings

Example from [beans.xml.full](../xml/beans.xml.full)

	<util:map id="actionThreads">
		<entry key="DeleteObjectAction" value="3" />
		<entry key="RestartIngestWorkflowAction" value="3" />
		<entry key="IngestRegisterURNAction" value="3" />
		...
		<entry key="PIPGenObjectToWorkAreaAction" value="3" />
		<entry key="PIPGenCleanWorkAreaAction" value="3" />
	</util:map>

TODO description

	<util:list id="actionOrdering">
   		<value>DeleteObjectAction</value>
		<value>RestartIngestWorkflowAction</value>
		<value>RetrievalAction</value>
		...
		<value>PIPGenScanForPresentationAction</value>
		<value>PIPGenObjectToWorkAreaAction</value>
        <value>PostRetrievalAction</value>
	</util:list>
	
TODO description	

### The import secion

    <!-- beans.xml.full -->

    <import resource="classpath*:META-INF/beans-infrastructure.common.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.identifier.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.fedora.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irods.xml"/>

    <import resource="classpath*:META-INF/beans-workflow.presentation.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.ingest.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.retrieval.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.pipgen.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.other.xml"/>

You'll typically find this section at the top of a beans.xml file. When installing the application, the installer delivers it with a beans.xml which has the right imports for your version (node,pres,full). The imports activate certain components of the application which would be inactive otherwise. When the imports are active the application requires the presence of certain properties in the config.properties file, dependent on the modules activated. Compare the parameters section above.
