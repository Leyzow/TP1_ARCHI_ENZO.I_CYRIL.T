package CyrilEnzoI.TP1_ASI;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Service de notification JMS utilisant un Topic.
 */
@Component
public class JmsNotificationService {

    private final ConnectionFactory connectionFactory;

    public JmsNotificationService() {
        this.connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    /**
     * Démarre deux consommateurs qui écoutent les notifications envoyées dans le topic.
     * @throws Exception
     */
    public void startNotificationConsumers() throws Exception {
        Connection connection = connectionFactory.createConnection();
        connection.setClientID("NotificationServiceClient"); // ID unique pour les abonnements durables
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic notificationTopic = session.createTopic("notificationTopic");

        // Création des abonnements durables
        MessageConsumer consumer1 = session.createDurableSubscriber(notificationTopic, "Consumer1");
        MessageConsumer consumer2 = session.createDurableSubscriber(notificationTopic, "Consumer2");

        System.out.println("✅ [DEBUG] Attachement du listener pour Consumer 1");
        consumer1.setMessageListener(message -> {
            try {
                System.out.println("📩 [Client 1] Notification reçue : " + ((TextMessage) message).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        System.out.println("✅ [DEBUG] Attachement du listener pour Consumer 2");
        consumer2.setMessageListener(message -> {
            try {
                System.out.println("📩 [Client 2] Notification reçue : " + ((TextMessage) message).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        connection.start();
        System.out.println("✅ Les clients sont abonnés aux notifications.");

        // ✅ Ajout d’un Hook pour fermer proprement les connexions
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("🛑 Fermeture propre des abonnés aux notifications.");
                session.close();
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
    }
}
