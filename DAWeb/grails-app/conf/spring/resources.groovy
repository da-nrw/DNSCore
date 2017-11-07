// Place your Spring DSL code here
beans = {
 jmsConnectionFactory(org.apache.activemq.ActiveMQConnectionFactory){
	//brokerURL = 'tcp://localhost:' +  application.config.cb.port
	 brokerURL = 'tcp://localhost:4455'
	}
	
	
}