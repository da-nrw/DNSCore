import grails.util.*
beans = {
  jmsConnectionFactory(org.apache.activemq.ActiveMQConnectionFactory) {
    brokerURL = 'tcp://localhost:'+ application.config.cb.port
  }
}