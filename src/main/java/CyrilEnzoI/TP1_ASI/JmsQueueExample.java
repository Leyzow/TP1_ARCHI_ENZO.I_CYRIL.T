package CyrilEnzoI.TP1_ASI;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class JmsQueueExample {

    private final ConnectionFactory connectionFactory;

    public JmsQueueExample() {
        this.connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    public void processQueue() throws Exception {
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("customerQueue");

            // Envoi du message
            MessageProducer producer = session.createProducer(queue);
            String payload = "Message important à traiter";
            Message message = session.createTextMessage(payload);
            producer.send(message);
            System.out.println("Envoi du message : '" + payload + "'");

            // Consommation du message
            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            TextMessage receivedMessage = (TextMessage) consumer.receive();
            System.out.println("Message reçu : " + receivedMessage.getText());

        } finally {
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
    }
}
