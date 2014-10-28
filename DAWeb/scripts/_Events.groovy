

	eventCreateWarStart = { warName, stagingDir ->
	def unknownValue = 'UNKNOWN'
	def buildNumberEnvironment = 'BUILD_NUMBER'
	def scmRevisionEnvironment = 'GIT_COMMIT'
	def buildNumberProperty = 'build.number'
	def scmRevisionProperty = 'build.revision'
	def buildNumber = System.getenv(buildNumberEnvironment)
	if( !buildNumber ) {
	buildNumber = System.getProperty(buildNumberProperty, unknownValue)
	}
	def scmRevision = System.getenv(scmRevisionEnvironment)
	if( !scmRevision ) {
	scmRevision = System.getProperty(scmRevisionProperty, unknownValue)
	}
	ant.propertyfile(file:"${stagingDir}/WEB-INF/classes/application.properties") {
	entry(key:'app.version.buildNumber', value: buildNumber)
	}
	ant.manifest(file: "${stagingDir}/META-INF/MANIFEST.MF", mode: "update") {
	attribute(name: "Build-Time", value: new Date())
	section(name: "Grails Application") {
	attribute(name: "Implementation-Build-Number", value: buildNumber)
	attribute(name: "Implementation-SCM-Revision", value: scmRevision)
	}
	}
	}


