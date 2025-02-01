package CyrilEnzoI.TP1_ASI;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class JmsTopicExample {

    private final ConnectionFactory connectionFactory;

    public JmsTopicExample() {
        this.connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
    }

    public void processTopic() throws Exception {
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("customerTopic");

            // Création des consommateurs
            MessageConsumer consumer1 = session.createConsumer(topic);
            MessageConsumer consumer2 = session.createConsumer(topic);

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

            connection.start(); // Activer la réception des messages
            System.out.println("✅ Les consommateurs sont prêts à recevoir des messages du topic.");

            // Publication du message
            MessageProducer producer = session.createProducer(topic);
            String payload = "Message important à diffuser";
            Message message = session.createTextMessage(payload);
            System.out.println("📢 Envoi du message : '" + payload + "' vers le topic");
            producer.send(message);

            // Pause pour donner le temps aux consommateurs de traiter les messages
            Thread.sleep(3000);

        } finally {
            if (session != null) session.close();
            if (connection != null) connection.close();
        }
    }
}
