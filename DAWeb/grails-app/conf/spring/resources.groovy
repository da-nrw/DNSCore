// Place your Spring DSL code here
beans = {
 jmsConnectionFactory(org.apache.activemq.ActiveMQConnectionFactory){
	//brokerURL = 'tcp://localhost:' +  application.config.cb.port
	 brokerURL =  grailsApplication.config.getProperty('spring.activemq.brokerUrl') + 
	    ':' + grailsApplication.config.getProperty('spring.activemq.port')
  }
}