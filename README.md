# ibm-bpm-integration-utils-app

Модуль для взаимодействия приложения на IBM BPM с сервером сообщений.

## JMSConnector

Класс для взаимодействия с JMS-сервером.

* **callJMSSync** - Функция взаимодействия IBM BPM c JMS-сервером.
* **sendJMSMessage** - Функция отправки сообщения на JMS-сервер.
* **waitForJMSMessage** - Функция получения сообщения от JMS-сервера.

## MQConnector

Класс для взаимодействия с MQ-сервером.

* **callMQSync** - Функция взаимодействия IBM BPM c MQ-сервером.
* **sendMQMessage** - Функция отправки сообщения на MQ-сервер.
* **waitForMQMessage** - Функция получения сообщения от MQ-сервера.

## TWJsonConverter

Класс для конвертации объекта IBM BPM <---> JSON-строку.

* **convertTWObjectToJSON** - Функция преобразования JSON-строки в объект IBM BPM.
* **convertJSONToTWObject** - Функция преобразования объекта IBM BPM в JSON-строку.