package org.example.connector;

import com.ibm.jms.JMSTextMessage;
import com.ibm.mq.MQC;
import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueReceiver;
import com.ibm.mq.jms.MQQueueSender;
import com.ibm.mq.jms.MQQueueSession;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.logging.Logger;

/**
 * Класс для взаимодействия с MQ-сервером.
 */
public final class MQConnector {
    private static final Logger LOGGER = Logger.getLogger(MQConnector.class.getName());

    /**
     * Функция взаимодействия IBM BPM c MQ-сервером.
     *
     * @param hostName       Адрес сервера MQ
     * @param port           Порт сервера MQ
     * @param queueManager   Менеджер очередей
     * @param channel        Канал
     * @param sendQueue      Исходящая очередь
     * @param receiveQueue   Входящая очередь
     * @param messageContent Исходящее сообщение
     * @param timeout        Время ожидания ответного сообщения
     * @return Входящее сообщение
     * @throws JMSException
     */
    public static String callMQSync(String hostName, Integer port, String queueManager, String channel, String sendQueue, String receiveQueue, String messageContent, Integer timeout) throws JMSException {
        MQQueueConnection connection = createConnection(hostName, port, queueManager, channel);
        MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        MQQueue sendMQQueue = (MQQueue) session.createQueue(sendQueue);
        MQQueue receiveMQQueue = (MQQueue) session.createQueue(receiveQueue);
        receiveMQQueue.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

        MQQueueSender sender = (MQQueueSender) session.createSender(sendMQQueue);

        JMSTextMessage outgoingMessage = (JMSTextMessage) session.createTextMessage(messageContent);
        outgoingMessage.setJMSType(MQC.MQFMT_STRING);

        String correlationKey = ConnectorUtils.generateCorrelationKey();

        outgoingMessage.setJMSCorrelationIDAsBytes(correlationKey.getBytes());
        outgoingMessage.setJMSReplyTo(receiveMQQueue);
        outgoingMessage.setJMSMessageID(String.valueOf(System.currentTimeMillis()));

        LOGGER.info("Outgoing message");
        LOGGER.info(outgoingMessage.toString());

        connection.start();
        sender.send(outgoingMessage);

        MQQueueReceiver receiver = (MQQueueReceiver) session.createReceiver(receiveMQQueue, "JMSCorrelationID='ID:" + ConnectorUtils.getHexString(correlationKey.getBytes()) + "'");
        Message ingoingMessage = receiver.receive(timeout * 1000);

        if (ingoingMessage == null) {
            LOGGER.info("Ingoing message is null");
        } else {
            LOGGER.info("Ingoing message");
            LOGGER.info(ingoingMessage.toString());
        }

        String result = ConnectorUtils.convertMessageToString(ingoingMessage);

        sender.close();
        receiver.close();
        session.close();
        connection.close();

        return result;
    }

    /**
     * Функция отправки сообщения на MQ-сервер.
     *
     * @param hostName       Адрес сервера MQ
     * @param port           Порт сервера MQ
     * @param queueManager   Менеджер очередей
     * @param channel        Канал
     * @param sendQueue      Исходящая очередь
     * @param receiveQueue   Входящая очередь
     * @param messageContent Исходящее сообщение
     * @return Корреляционный ключ
     * @throws Exception
     */
    public static String sendMQMessage(String hostName, Integer port, String queueManager, String channel, String sendQueue, String receiveQueue, String messageContent) throws Exception {
        MQQueueConnection connection = createConnection(hostName, port, queueManager, channel);
        MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        MQQueue sendMQQueue = (MQQueue) session.createQueue(sendQueue);
        MQQueue receiveMQQueue = (MQQueue) session.createQueue(receiveQueue);
        receiveMQQueue.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

        MQQueueSender sender = (MQQueueSender) session.createSender(sendMQQueue);

        JMSTextMessage outgoingMessage = (JMSTextMessage) session.createTextMessage(messageContent);
        outgoingMessage.setJMSType(MQC.MQFMT_STRING);

        String correlationId = ConnectorUtils.generateCorrelationKey();

        outgoingMessage.setJMSCorrelationIDAsBytes(correlationId.getBytes());
        outgoingMessage.setJMSReplyTo(receiveMQQueue);
        outgoingMessage.setJMSMessageID(String.valueOf(System.currentTimeMillis()));

        LOGGER.info("Outgoing message");
        LOGGER.info(outgoingMessage.toString());

        connection.start();
        sender.send(outgoingMessage);

        return correlationId;
    }

    /**
     * Функция получения сообщения от MQ-сервера.
     *
     * @param hostName      Адрес сервера MQ
     * @param port          Порт сервера MQ
     * @param queueManager  Менеджер очередей
     * @param channel       Канал
     * @param receiveQueue  Входящая очередь
     * @param correlationId Корреляционный ключ
     * @param timeout       Время ожидания ответного сообщения
     * @return Входящее сообщение
     * @throws Exception
     */
    public static String waitForMQMessage(String hostName, Integer port, String queueManager, String channel, String receiveQueue, String correlationId, Integer timeout) throws Exception {
        MQQueueConnection connection = createConnection(hostName, port, queueManager, channel);
        MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        MQQueue receiveMQQueue = (MQQueue) session.createQueue(receiveQueue);
        receiveMQQueue.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

        connection.start();

        MQQueueReceiver receiver = (MQQueueReceiver) session.createReceiver(receiveMQQueue, "JMSCorrelationID='ID:" + ConnectorUtils.getHexString(correlationId.getBytes()) + "'");
        Message ingoingMessage = receiver.receive(timeout * 1000);

        if (ingoingMessage == null) {
            LOGGER.info("Ingoing message is null");
        } else {
            LOGGER.info("Ingoing message");
            LOGGER.info(ingoingMessage.toString());
        }

        String result = ConnectorUtils.convertMessageToString(ingoingMessage);

        receiver.close();
        session.close();
        connection.close();

        return result;
    }

    private static MQQueueConnection createConnection(String hostName, Integer port, String queueManager, String channel) throws JMSException {
        MQQueueConnectionFactory mqQueueConnectionFactory = new MQQueueConnectionFactory();

        mqQueueConnectionFactory.setHostName(hostName);
        mqQueueConnectionFactory.setPort(port);
        mqQueueConnectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
        mqQueueConnectionFactory.setQueueManager(queueManager);
        mqQueueConnectionFactory.setChannel(channel);

        MQQueueConnection mqQueueConnection = (MQQueueConnection) mqQueueConnectionFactory.createQueueConnection();

        return mqQueueConnection;
    }
}
