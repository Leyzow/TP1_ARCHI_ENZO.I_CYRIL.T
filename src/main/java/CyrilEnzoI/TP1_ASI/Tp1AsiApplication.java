package CyrilEnzoI.TP1_ASI;

import org.apache.activemq.broker.BrokerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Tp1AsiApplication {

	public static void main(String[] args) {
		SpringApplication.run(Tp1AsiApplication.class, args);
	}

	@Bean
	public BrokerService brokerService() throws Exception {
		BrokerService broker = new BrokerService();
		broker.addConnector("tcp://localhost:61616");
		broker.start();
		return broker;
	}

	@Bean
	CommandLineRunner start(JmsOrderService jmsOrderService, JmsNotificationService jmsNotificationService) {
		return args -> {
			jmsNotificationService.startNotificationConsumers(); // Démarrer les abonnés aux notifications
			jmsOrderService.processOrders(); // Démarrer le traitement des commandes

			// Attendre pour s'assurer que le consommateur est actif
			Thread.sleep(3000);

			jmsOrderService.placeOrder("Commande #123 - Smartphone"); // Passer une commande
		};
	}
}
