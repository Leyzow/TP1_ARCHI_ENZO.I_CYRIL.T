package CyrilEnzoI.TP1_ASI;

import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class JmsQueueExample {
    public static void main(String[] args) throws URISyntaxException, Exception {
        // Étape 1 : Configurer et démarrer le broker ActiveMQ
        BrokerService broker = BrokerFactory.createBroker(new URI("broker:(tcp://localhost:61616)"));
        broker.start(); // Démarrer le broker
        Connection connection = null;

        try {
            // Étape 2 : Créer une connexion au broker ActiveMQ
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createConnection();

            // Étape 3 : Créer une session JMS
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Étape 4 : Créer une file (Queue)
            Queue queue = session.createQueue("customerQueue");

            // Étape 5 : Créer un message à envoyer
            String payload = "Message important à traiter";
            Message message = session.createTextMessage(payload);

            // Étape 6 : Envoyer un message à la file
            MessageProducer producer = session.createProducer(queue);
            System.out.println("Envoi du message : '" + payload + "'");
            producer.send(message); // Envoi du message

            // Étape 7 : Créer un consommateur pour lire les messages
            MessageConsumer consumer = session.createConsumer(queue);

            // Étape 8 : Démarrer la connexion pour activer la réception des messages
            connection.start();

            // Étape 9 : Recevoir le message de la file
            TextMessage receivedMessage = (TextMessage) consumer.receive(); // Lecture synchrone
            System.out.println("Message reçu : " + receivedMessage.getText());

            // Fermer la session
            session.close();
        } finally {
            if (connection != null) {
                connection.close(); // Fermer la connexion
            }
            broker.stop(); // Arrêter le broker
        }
    }
}
