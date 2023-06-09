
Сборка и использование клиента: 

1) Выполнить `./gradlew balance-service-client:jar` 
2) Запускать jar со следующими параметрами `java -jar ./balance-service-client/build/libs/balance-service-client.jar host:port threadCount readQuota writeQuota lowerBoundId..upperBoundId` lowerBoundId..upperBoundId - диапазон id c шагом 1


Сборка и использование сервера: 

1) Собрать jar `./gradlew balance-service-server:bootJar`
2) Поднять сервер с бд через docker-compose `./balance-service-server/docker-compose.yml`


Некоторые технические детали: 

Пользуясь рекомендацией "а также приветствуется самостоятельное принятие решений в тех вопросах, где возможна неоднозначная трактовка",
хочу обазначить некоторые допущения. 

1) Для предотвращения аномалий конкурентного доступа используются пессимистические блокировки. В целом для решения данной проблемы можно использовать также оптимистические блокировки, 
но при работе с ними потребуются повторные запросы при неудачных обновлениях. Общая рекомендация - использовать пессимистическую блокировку при высокой конкуренции, оптимистическую при низкой. Допущение: для данного сервиса конкуренция высока. 
2) В качестве кеша используется Hazelcast, это позволит в будущем горизонтально масштабировать сервис за счет возможностей Hazelcast объединяться в кластер.
3) Метод `changeBalance` можно было бы исполнять асинхронно, закидывая запросы на апдейт в очередь. Такой поход должен увеличить пропускную способность и надежность, но усложнить архитектуру. 
Плюс при таком подходе изменения могут быть не сразу видны при последующих чтениях. Допущение для данной реализации: при апдейте последующие чтения должны видеть апдейт.

Результаты тестирования производительности находятся в `./performance_test_results`. 
Параметры клиента при тестировании: threadCount = 1000; RXX - доля запросов на чтение; WXX - доля запросов на запись; high_concurrency - 1..10 диапазон айдишников аккаунтов; low_concurrency - 1..1_000_000 диапазон айдишников аккаунтов.
