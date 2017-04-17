# Оперативная автоматическая кластеризация сообщений социальных сетей с использованием системы потоковой обработки данных Apache Storm
## Запуск приложения
1. Этот проект зависит от проекта [text-clustering](https://github.com/GorshkovNikita/text-clustering), поэтому этот проект также необходимо сначала скачать, а потом запустить в его корне команду `mvn install`, предварительно установив Maven.
2. Также он зависит от [clustering-statistics](https://github.com/GorshkovNikita/clustering-statistics), поэтому его тоже необходимо скачать и собрать с помощью команды `mvn install`.
2. Создать [приложение для твиттера](https://apps.twitter.com/) для получения необходимых ключей.
3. Создать файл twitter-config.properties с помощью команды `cp src/main/resources/twitter-config.properties.example src/main/resources/twitter-config.properties` и скопировать в соответсвующие поля ключи, сгенерированные при создании приложения в пункте 2.
4. Создать MySQL базу данных с помощью скрипта, лежащего в корне (database.sql).
5. Создать файл mysql-config.properties в проекте [clustering-statistics](https://github.com/GorshkovNikita/clustering-statistics) с помощью команды `cp src/main/resources/mysql-config.properties.example src/main/resources/mysql-config.properties` и, если необходимо, поменять в нем информацию о пользователе, пароле и названии БД.
6. Запустить проект с параметром local.

Программа запустится в локальном режиме, а источником данных будет выступать Twitter Streaming API. Для управления входящим потоком можно в классе [TwitterStreamConnection](https://github.com/GorshkovNikita/storm-clustering/blob/master/src/main/java/diploma/TwitterStreamConnection.java) изменять ключевые слова. Каждые 5 минут программа будет собирать основную информацию о кластерах в созданную БД (количество документов в кластере, топ-10 слов).