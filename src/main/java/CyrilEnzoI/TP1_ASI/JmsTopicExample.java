package CyrilEnzoI.TP1_ASI;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Classe TopicExample démontrant la publication et la souscription à un topic JMS avec ActiveMQ.
 *
 * Étapes pour les golmons :
 * 1. Lancer ActiveMQ sur tcp://localhost:61616.
 * 2. Compiler et exécuter cette classe.
 * 3. Observer les consommateurs qui écoutent le topic.
 * 4. Vérifier que le message envoyé est bien reçu par chaque consommateur.
 */
@Component
public class JmsTopicExample {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "exampleTopic";

    public static void main(String[] args) {
        JmsTopicExample example = new JmsTopicExample();
        example.start();
    }

    /**
     * Initialise le broker, publie un message et configure deux consommateurs.
     */
    public void start() {
        Connection connection = null;
        try {
            // Étape 1 : Création d'une connexion au broker ActiveMQ
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
            connection = connectionFactory.createConnection();
            connection.start();

            // Étape 2 : Création d'une session JMS
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);

            // Étape 3 : Configuration des consommateurs
            MessageConsumer consumer1 = session.createConsumer(topic);
            consumer1.setMessageListener(new ExampleMessageListener("Consumer1"));

            MessageConsumer consumer2 = session.createConsumer(topic);
            consumer2.setMessageListener(new ExampleMessageListener("Consumer2"));

            // Étape 4 : Publication d'un message
            MessageProducer producer = session.createProducer(topic);
            String messageContent = "Hello from JMS Topic!";
            TextMessage message = session.createTextMessage(messageContent);
            System.out.println("Envoi du message: " + messageContent);
            producer.send(message);

            // Étape 5 : Pause pour observer la réception des messages
            Thread.sleep(3000);
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Implémentation d'un MessageListener pour traiter les messages reçus du topic.
     */
    private static class ExampleMessageListener implements MessageListener {
        private final String name;

        public ExampleMessageListener(String name) {
            this.name = name;
        }

        @Override
        public void onMessage(Message message) {
            try {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    System.out.println(name + " a reçu: " + textMessage.getText());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
