package org.example.converter;


import org.json.JSONArray;
import org.json.JSONObject;
import teamworks.TWList;
import teamworks.TWObject;
import teamworks.TWObjectFactory;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для конвертации объекта IBM BPM {@link teamworks.TWObject} <---> JSON-строку.
 */
public final class TWJsonConverter {
    private static final Logger LOGGER = Logger.getLogger(TWJsonConverter.class.getName());

    private static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String ISO_DATE_FORMAT_PATTERN = "(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})Z";

    /**
     * Функция преобразования JSON-строки в объект IBM BPM
     *
     * @param json JSON-строка
     * @return Объект IBM BPM
     * @throws Exception
     */
    public static TWObject convertJSONToTWObject(String json) throws Exception {
        if (json == null) {
            throw new RuntimeException("JSON is null");
        }

        if ("[".equals(json.trim().substring(0, 1))) {
            JSONArray jsonObject = new JSONArray(json);
            LOGGER.info(jsonObject.toString());

            TWObject twObject = (TWObject) convert(jsonObject);

            return twObject;
        } else {
            JSONObject jsonObject = new JSONObject(json);
            LOGGER.info(jsonObject.toString());

            TWObject twObject = convert(jsonObject);

            return twObject;
        }
    }

    /**
     * Функция преобразования объекта IBM BPM в JSON-строку
     *
     * @param twObject Объект IBM BPM
     * @return JSON-строка
     */
    public static String convertTWObjectToJSON(com.lombardisoftware.core.TWObject twObject) {
        if (twObject == null) {
            throw new RuntimeException("TWObject is null");
        }

        StringWriter out = new StringWriter();
        String jsonText;

        if (!twObject.isArray()) {
            JSONObject obj = convert((TWObject) twObject);
            obj.write(out);
        } else {
            JSONArray obj = convert((TWList) twObject);
            obj.write(out);
        }

        jsonText = out.toString();
        LOGGER.info(jsonText);

        return jsonText;
    }

    private static TWList convert(JSONArray jsonArray) throws Exception {
        TWList items = TWObjectFactory.createList();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object propertyValue = jsonArray.get(i);
            if (propertyValue instanceof JSONObject) {
                items.addArrayData(convert((JSONObject) propertyValue));
            } else if (propertyValue instanceof JSONArray) {
                items.addArrayData(convert((JSONArray) propertyValue));
            } else if (propertyValue instanceof Integer || propertyValue instanceof Boolean || propertyValue instanceof Double) {
                items.addArrayData(propertyValue);
            } else if (propertyValue instanceof String) {
                items.addArrayData(handleString((String) propertyValue));
            }
        }

        return items;
    }

    private static TWObject convert(JSONObject jsonObject) throws Exception {
        TWObject twObject = TWObjectFactory.createObject();

        for (String property : jsonObject.keySet()) {
            Object propertyValue = jsonObject.get(property);

            if (propertyValue instanceof JSONObject) {
                twObject.setPropertyValue(property, convert((JSONObject) propertyValue));
            } else if (propertyValue instanceof JSONArray) {
                twObject.setPropertyValue(property, convert((JSONArray) propertyValue));
            } else if (propertyValue instanceof Integer || propertyValue instanceof Boolean || propertyValue instanceof Double) {
                twObject.setPropertyValue(property, propertyValue);
            } else if (propertyValue instanceof String) {
                twObject.setPropertyValue(property, handleString((String) propertyValue));
            }
        }

        return twObject;
    }

    private static JSONObject convert(TWObject twObject) {
        JSONObject jsonObject = new JSONObject();

        for (String property : twObject.getPropertyNames()) {
            Object propertyValue = twObject.getPropertyValue(property);

            if (propertyValue instanceof TWObject) {
                if (((com.lombardisoftware.core.TWObject) propertyValue).isArray()) {
                    jsonObject.put(property, convert((TWList) propertyValue));
                } else {
                    jsonObject.put(property, convert((TWObject) propertyValue));
                }
            } else if (propertyValue instanceof String || propertyValue instanceof Integer || propertyValue instanceof Boolean || propertyValue instanceof Double) {
                jsonObject.put(property, propertyValue);
            } else if (propertyValue instanceof GregorianCalendar) {
                GregorianCalendar gc = (GregorianCalendar) propertyValue;
                Date currentDate = gc.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat(ISO_DATE_FORMAT);
                jsonObject.put(property, formatter.format(currentDate));
            }
        }

        return jsonObject;
    }

    private static JSONArray convert(TWList twList) {
        JSONArray jsonArray = new JSONArray();

        for (Object propertyValue : twList.getUnmodifiableArray()) {
            if (propertyValue instanceof TWObject) {
                if (((com.lombardisoftware.core.TWObject) propertyValue).isArray()) {
                    jsonArray.put(convert((TWList) propertyValue));
                } else {
                    jsonArray.put(convert((TWObject) propertyValue));
                }
            } else if (propertyValue instanceof String || propertyValue instanceof Integer || propertyValue instanceof Boolean || propertyValue instanceof Double) {
                jsonArray.put(propertyValue);
            } else if (propertyValue instanceof GregorianCalendar) {
                GregorianCalendar gc = (GregorianCalendar) propertyValue;
                Date currentDate = gc.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat(ISO_DATE_FORMAT);
                jsonArray.put(formatter.format(currentDate));
            }
        }

        return jsonArray;
    }

    private static Object handleString(String string) throws ParseException {
        if (string.length() == 20) {
            Pattern pattern = Pattern.compile(ISO_DATE_FORMAT_PATTERN);
            Matcher matcher = pattern.matcher(string);

            if (matcher.find()) {
                DateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);

                return dateFormat.parse(matcher.group(0));
            }
        }

        return string;
    }
}
