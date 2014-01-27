beans = {
  jmsConnectionFactory(org.apache.activemq.ActiveMQConnectionFactory) {
    brokerURL = 'tcp://' + application.config.irods.server +':'+ application.config.cb.port
  }
}