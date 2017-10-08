package org.example.connector;

import java.util.UUID;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public final class ConnectorUtils {

    /**
     * Генератор 8-ми значного ключа корреляции.
     *
     * @return ключ корреляции
     */
    public static String generateCorrelationKey() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        return uuidString.substring(0, 8);
    }

    /**
     * Конвертация {@link javax.jms.Message} в {@link String}.
     *
     * @param message сообщение в формате {@link javax.jms.Message}
     * @return сообщение в формате {@link String}
     * @throws JMSException
     */
    public static String convertMessageToString(Message message) throws JMSException {
        if (message == null) {
            throw new RuntimeException("No response");
        }

        String data = null;

        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            data = textMessage.getText();
        } else if (message instanceof BytesMessage) {
            BytesMessage bytesMessage = (BytesMessage) message;

            int textLength = new Long(bytesMessage.getBodyLength()).intValue();
            byte[] textBytes = new byte[textLength];
            bytesMessage.readBytes(textBytes, textLength);
            data = new String(textBytes);
        }

        return data;
    }

    public static String getHexString(byte[] byteArray) {
        StringBuilder result = new StringBuilder();
        for (byte byteElement : byteArray) {
            int x = (byteElement & 0xff);
            int y = x + 0x100;
            String hexString = Integer.toString(y, 16);
            String hexSubstring = hexString.substring(1);

            result.append(hexSubstring);
        }

        return result.toString();
    }
}
