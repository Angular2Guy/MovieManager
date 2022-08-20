# MovieManager
This is a project to manage my movies. It shows howto use Angular with Spring Boot and Jpa. For development it uses a H2 database. For persistent data it uses Postgresql. The databases are initialized and updated with Liquibase. For fulltext search in actor biographies and movie overviews Hibernate Search is used.

Author: Sven Loesekann

Technologies: Angular, Angular-Cli, Ng-Bootstrap, Typescript, Spring Boot, H2, Postgresql, Liquibase, Jpa, Maven, Java, Hibernate Search

## Articles
* [An Angular Autocomplete From UI to DB](https://angular2guy.wordpress.com/2021/07/31/an-angular-autocomplete-from-ui-to-db/)
* [Advanced Kubernetes setup for Spring Boot App with Postgresql DB](https://angular2guy.wordpress.com/2021/07/30/advanced-kubernetes-setup-for-spring-boot-app-with-postgresql-db/)
* [Scalable Jwt Token Revokation in Spring Boot](https://angular2guy.wordpress.com/2022/03/26/scalable-jwt-token-revokation-in-spring-boot/)

## Features
1. It imports movie data from the movie database. The imported movies can be searched by movie title and Genere. 
2. The actors can be searched by actor name.
3. The project manages its users with a login/signin and can import different movies for different users. 
4. The loggedout/revoked tokens are checked now.
5. The movie overviews and actor bios are indexed for fulltext search with searchterms.

## Mission Statement
The project serves as example for the integration of Angular, Spring Boot with Jpa and relational databases in clean architecture. The Angular frontend uses the Ng-Bootstrap components. The backend manages/initialzies the H2/Postgresql databases with Liquibase. The data access is done with Jpa and Spring Repositories. Actor bios and Movie overviews are indexed and searched with Hibernate Search. The movies are imported with Spring WebClient. The architecture is checked with ArchUnit in a test. The security setup is done with Spring Security and Jwt Tokens, that are locked after logout.

## Postgresql setup
In the postgresql.sh file are the commands to pull and run Postgresql in a Docker image locally. To build a Jar with Postgresql setup build it with 'mvnw clean install -Ddocker=true'. In Eclipse the maven profile 'standalone-postgresql' has to be activated and a run/debug configuration with the VM parameter '-Dspring.profiles.active=prod' has to started. The database will be initialized by Liquibase. The Liquibase scripts are setup with preconditions that the tables/sequences/indexes are only created if they do not exist. 

## Kubernetes setup
In the helm directory is a kubernetes setup to run the moviemanager project with minikube. The Helm chart deployes the postgres database and the moviemanager with the needed parameters to run. It uses the resource limit support of Jdk 16 to limit memory. Kubernetes limits the cpu use and uses the startupprobes and livenessprobes that Spring Actuator provides.

## Movie import
To import movies a key needs to be provided at signin. To get such a key according to this [Faq](https://www.themoviedb.org/faq/api)

## Fulltext Search
Hibernate Search recreates the indexes for the movie overviews and actor bios if the amount of movies or actors has changed. The indexes are used to support searchterms in the indexed texts.

## Testdata
It is test data provided for the User 'John' and the Password 'Doe'. Then a movie with an actor is available for testing. The login data is also needed for the /h2-console. 

## Monitoring
The Spring Actuator interface with Prometheus interface can be used as it is described in this article: 

[Monitoring Spring Boot with Prometheus and Grafana](https://ordina-jworks.github.io/monitoring/2020/11/16/monitoring-spring-prometheus-grafana.html)

To test the setup the application has to be started and the Docker Images for Prometheus and Grafana have to be started and configured. The scripts 'runGraphana.sh' and 'runPrometheus.sh' can be used as a starting point.

## Setup
Postgresql 10.x or newer.

Eclipse IDE for Enterprise Java and Web Developers newest version.

Java 17 or newer

Maven 3.5.2 or newer.

Nodejs 14.15.x or newer

Npm 6.14.x or newer

Angular Cli 14 or newer.
