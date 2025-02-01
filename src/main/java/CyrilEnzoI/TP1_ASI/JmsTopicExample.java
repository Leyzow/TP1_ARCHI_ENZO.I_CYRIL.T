package CyrilEnzoI.TP1_ASI;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Classe permettant de démontrer le modèle Publish-Subscribe avec un Topic JMS.
 *
 * - Deux consommateurs s'abonnent à un Topic.
 * - Un producteur envoie un message sur ce Topic.
 * - Tous les consommateurs abonnés reçoivent le message.
 */
@Component
public class JmsTopicExample {

    private final ConnectionFactory connectionFactory;

    /**
     * Constructeur qui initialise la connexion avec ActiveMQ via TCP.
     */
    public JmsTopicExample() {
        this.connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    /**
     * Méthode qui :
     * - Crée un Topic JMS.
     * - Ajoute deux consommateurs abonnés au Topic.
     * - Publie un message vers le Topic.
     * - Tous les consommateurs abonnés reçoivent le message.
     *
     * @throws Exception en cas de problème de connexion ou de gestion JMS.
     */
    public void processTopic() throws Exception {
        Connection connection = null;
        Session session = null;
        try {
            // 🔹 Création de la connexion JMS
            connection = connectionFactory.createConnection();

            // 🔹 Création d'une session JMS (sans transaction, avec confirmation automatique des messages)
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 🔹 Création du Topic nommé "customerTopic"
            Topic topic = session.createTopic("customerTopic");

            // 🔹 Création des consommateurs abonnés au Topic
            MessageConsumer consumer1 = session.createConsumer(topic);
            MessageConsumer consumer2 = session.createConsumer(topic);

            // 🔹 Définition des comportements des consommateurs lors de la réception d'un message
            consumer1.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println("🎧 Consumer1 a reçu : " + ((TextMessage) message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            consumer2.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println("🎧 Consumer2 a reçu : " + ((TextMessage) message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            // 🔹 Démarrer la connexion pour activer la réception des messages par les consommateurs
            connection.start();
            System.out.println("✅ Les consommateurs sont prêts à recevoir des messages du topic.");

            // 🔹 Création d'un producteur de messages
            MessageProducer producer = session.createProducer(topic);

            // 🔹 Création et envoi d'un message textuel vers le Topic
            String payload = "Message important à diffuser";
            Message message = session.createTextMessage(payload);
            System.out.println("📢 Envoi du message : '" + payload + "' vers le topic");
            producer.send(message);

            // 🔹 Pause de 3 secondes pour laisser le temps aux consommateurs de recevoir le message
            Thread.sleep(3000);

        } finally {
            // 🔹 Fermeture propre de la session et de la connexion
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
    }
}
