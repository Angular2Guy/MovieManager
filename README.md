# MovieManager
This is a project to manage my movies. It shows howto use Angular with Spring Boot and Jpa. For development it uses a H2 database. For persistent data it uses Postgresql. The databases are initialized and updated with Liquibase.

![Build Status](https://travis-ci.org/Angular2Guy/MovieManager.svg?branch=master)

Author: Sven Loesekann

Technologies: Angular, Angular-Cli, Ng-Bootstrap, Typescript, Spring Boot, H2, Postgresql, Liquibase, Jpa, Maven, Java

## Features
1. It imports movie data from the movie database. The imported movies can be searched by movie title and Genere. 
2. The actors can be searched by actor name.
3. The project manages its users with a login/signin and can import different movies for different users. 

## Mission Statement
The project serves as example for the integration of Angular, Spring Boot with Jpa and relational databases in clean architecture. The Angular frontend uses the Ng-Bootstrap components. The backend manages/initialzies the H2/Postgresql databases with Liquibase. The data access is done with Jpa and Spring Repositories. The movies are imported with Spring RestTemplates. 

## Postgresql setup
In the postgresql.sh file are the commands to pull and run Postgresql in a Docker image locally. To build a Jar with Postgresql setup build it with 'mvnw clean install -Dpostgresql=true'. In Eclipse the maven profile 'standalone-postgresql' has to be activated and a run/debug configuration with the VM parameter '-Dspring.profiles.active=prod' has to started. The database will be initialized by Liquibase. The Liquibase scripts are setup with preconditions that the tables/sequences/indexes are only created if they do not exist. 

## Movie import
To import movies a key needs to be provided at signin. To get such a key according to this [Faq](https://www.themoviedb.org/faq/api)

## Testdata
It is test data provided for the User 'John' and the Password 'Doe'. Then a movie with an actor is availiable for testing. The login data is also needed for the /h2-console. 

## Setup
Postgresql 9.x or newer.

Eclipse Oxygen JEE or newer.

Install Eclipse Plugin 'Eclipse Wild Web Developer' of the Eclipse Marketplace.

Maven 3.3.3 or newer.

Nodejs 12.16.x or newer

Npm 6.13.x or newer

Angular Cli 9 or newer.