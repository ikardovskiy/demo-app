![Build Status](https://api.travis-ci.com/ikardovskiy/demo-app.svg?branch=develop)
![Code Coverage](https://codecov.io/github/ikardovskiy/demo-app/branch/develop/graph/badge.svg)

#
### Запуск
Сборка проекта проверялась под [AdoptOpenJDK 11.0.6](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot)

Требуется Maven, Java 11, Docker.
 
Все команды в данном разделе выполняются в корне проекта.
 
#### Сборка

    mvn clean package
    
#### Запуск и останов
    
    docker-compose up 
    docker-compose down
      
    
#### Использование

[Web Swagger UI](http://localhost:8080/swagger-ui.html)
для поиска кодов стран по заданному паттерну

реализовано в модуле **web**

по умолчанию включено локальное кеширование 
и обновление лоакльно кеша каждые 10 секунд 

возможность кеширования и его периодичность настриваются  
через настройки Spring Boot в модуле **web**


[Integration Swagger UI](http://localhost:8081/swagger-ui.html)

ручной запуск загрузки c сайта country.io   

автоматическая попытка загрузки производится каждые 60 секунд

периодичность автоматической попытки загрузки настраивается 
через настройки Spring Boot в модуле **integration**



