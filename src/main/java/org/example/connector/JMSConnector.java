package org.example.connector;

import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

/**
 * Класс для взаимодействия с JMS-сервером.
 */
public final class JMSConnector {
    private static final Logger LOGGER = Logger.getLogger(JMSConnector.class.getName());

    /**
     * Функция взаимодействия IBM BPM c JMS-сервером.
     *
     * @param queueConnectionJNDI JNDI фабрики соединений
     * @param sendQueueJNDI       JNDI исходящей очереди
     * @param receiveQueueJNDI    JNDI входящей очереди
     * @param messageContent      Исходящее сообщение
     * @param timeout             Время ожидания ответного сообщения
     * @return Входящее сообщение
     * @throws Exception
     */
    public static String callJMSSync(String queueConnectionJNDI, String sendQueueJNDI, String receiveQueueJNDI, String messageContent, Integer timeout) throws Exception {
        InitialContext initialContext = new InitialContext();

        QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(queueConnectionJNDI);
        Queue sendQueue = (Queue) initialContext.lookup(sendQueueJNDI);
        Queue receiveQueue = (Queue) initialContext.lookup(receiveQueueJNDI);
        QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();

        String correlationId = ConnectorUtils.generateCorrelationKey();

        sendMessage(queueConnection, sendQueue, receiveQueue, correlationId, messageContent);

        return receiveMessage(queueConnection, receiveQueue, correlationId, timeout);
    }

    /**
     * Функция отправки сообщения на JMS-сервер.
     *
     * @param queueConnectionJNDI JNDI фабрики соединений
     * @param sendQueueJNDI       JNDI исходящей очереди
     * @param receiveQueueJNDI    JNDI входящей очереди
     * @param messageContent      Исходящее сообщение
     * @return Корреляционный ключ
     * @throws Exception
     */
    public static String sendJMSMessage(String queueConnectionJNDI, String sendQueueJNDI, String receiveQueueJNDI, String messageContent) throws Exception {
        InitialContext initialContext = new InitialContext();

        QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(queueConnectionJNDI);
        Queue sendQueue = (Queue) initialContext.lookup(sendQueueJNDI);
        Queue receiveQueue = (Queue) initialContext.lookup(receiveQueueJNDI);
        QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();

        String correlationKey = ConnectorUtils.generateCorrelationKey();

        sendMessage(queueConnection, sendQueue, receiveQueue, correlationKey, messageContent);

        return correlationKey;
    }

    private static void sendMessage(QueueConnection queueConnection, Queue sendQueue, Queue receiveQueue, String correlationId, String messageContent) throws Exception {
        QueueSession queueSession = queueConnection.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        QueueSender queueSender = queueSession.createSender(sendQueue);

        TextMessage message = queueSession.createTextMessage(messageContent);
        message.setJMSReplyTo(receiveQueue);
        message.setJMSMessageID(String.valueOf(System.currentTimeMillis()));
        message.setJMSCorrelationIDAsBytes(correlationId.getBytes());

        queueSender.send(message);
        queueSender.close();
        queueSession.close();

        LOGGER.info("Message sent");
    }

    /**
     * Функция получения сообщения от JMS-сервера.
     *
     * @param queueConnectionJNDI JNDI фабрики соединений
     * @param receiveQueueJNDI    JNDI входящей очереди
     * @param correlationId       Корреляционный ключ
     * @param timeout             Время ожидания ответного сообщения
     * @return Входящее сообщение
     * @throws Exception
     */
    public static String waitForJMSMessage(String queueConnectionJNDI, String receiveQueueJNDI, String correlationId, Integer timeout) throws Exception {
        InitialContext initialContext = new InitialContext();

        QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup(queueConnectionJNDI);
        Queue receiveQueue = (Queue) initialContext.lookup(receiveQueueJNDI);
        QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();

        return receiveMessage(queueConnection, receiveQueue, correlationId, timeout);
    }

    private static String receiveMessage(QueueConnection queueConnection, Queue receiveQueue, String correlationId, Integer timeout) throws JMSException {
        QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        QueueReceiver queueReceiver = queueSession.createReceiver(receiveQueue, "JMSCorrelationID='ID:" + ConnectorUtils.getHexString(correlationId.getBytes()) + "'");
        queueConnection.start();

        Message ingoingMessage = queueReceiver.receive(timeout * 1000);

        String result = ConnectorUtils.convertMessageToString(ingoingMessage);

        queueReceiver.close();
        queueSession.close();
        queueConnection.close();

        LOGGER.info("Message received");

        return result;
    }
}