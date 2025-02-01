package CyrilEnzoI.TP1_ASI;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;
import java.util.concurrent.CountDownLatch;

/**
 * Service de gestion des commandes utilisant une Queue JMS.
 */
@Component
public class JmsOrderService {

    private final ConnectionFactory connectionFactory;
    private final CountDownLatch latch = new CountDownLatch(1); // Bloque le thread principal
    private boolean consumerAlreadyStarted = false; // Pour éviter plusieurs consommateurs

    public JmsOrderService() {
        this.connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    /**
     * Envoie une commande dans la queue JMS.
     * @param orderDetails Détails de la commande.
     * @throws Exception
     */
    public void placeOrder(String orderDetails) throws Exception {
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue orderQueue = session.createQueue("orderQueue");

            MessageProducer producer = session.createProducer(orderQueue);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            TextMessage message = session.createTextMessage(orderDetails);
            System.out.println("🛒 [DEBUG] Envoi du message dans la queue : " + orderDetails);
            producer.send(message);
            System.out.println("🛒 [DEBUG] Message envoyé avec succès dans la queue !");
        } finally {
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
    }

    /**
     * Consommateur qui écoute les commandes depuis la queue et envoie une notification.
     * Il reste actif aussi longtemps que l'application fonctionne.
     * @throws Exception
     */
    public void processOrders() throws Exception {
        if (consumerAlreadyStarted) {
            System.out.println("⚠️ Consommateur déjà actif.");
            return;
        }
        consumerAlreadyStarted = true;

        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE); // ✅ Utilisation de CLIENT_ACKNOWLEDGE
        Queue orderQueue = session.createQueue("orderQueue");
        Topic notificationTopic = session.createTopic("notificationTopic");

        // Création d’un producteur unique pour éviter des répétitions
        MessageProducer producer = session.createProducer(notificationTopic);

        // Création du consommateur de commandes
        MessageConsumer consumer = session.createConsumer(orderQueue);

        consumer.setMessageListener(message -> {
            try {
                System.out.println("📦 Nouveau message détecté !");
                String orderDetails = ((TextMessage) message).getText();
                System.out.println("📦 Commande reçue et traitée : " + orderDetails);

                // ✅ Confirmer explicitement que le message a bien été consommé
                message.acknowledge();

                // ✅ Après traitement, envoyer une notification
                String notification = "Votre commande '" + orderDetails + "' a été expédiée.";
                Message notificationMessage = session.createTextMessage(notification);
                producer.send(notificationMessage);
                System.out.println("📢 Notification envoyée : " + notification);

            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        connection.start();
        System.out.println("✅ Service de traitement des commandes actif.");

        // ✅ Ajout d’un Hook pour fermer la connexion proprement lors de l'arrêt
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("🛑 Fermeture propre du consommateur JMS.");
                session.close();
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
    }
}
