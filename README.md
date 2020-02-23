# MovieManager
This is a toy project to manage my movies

![Build Status](https://travis-ci.org/Angular2Guy/MovieManager.svg?branch=master)

Author: Sven Loesekann

Technologies: Angular, Angular-Cli, Ng-Bootstrap, Typescript, Spring Boot, Postgresql, Jpa, Maven

## Features
-It imports movie data from the movie database. The imported movies can be searched by movie title and Genere. 
-The actors can be searched by actor name.
-The project manages its users with a login/signin and can import different movies for different users. 

## Mission Statement
The project has served its purpose to test Angular, Spring Boot with Jpa and Postgresql features and it is now used to manage my movies. The Angular frontend supports 2 languages and uses Ng-Bootstrap. The access to Postgesql uses Spring Repositories and Jpa. The import of the movies uses the Spring RestTemplate.

## Postgresql Setup
The Postgresql database can be setup to run in a Docker image. The steps for the setup can be found in the postgresql.sh file.

## Movie import
To import movies a key needs to be provieded at signin. To get such a key according to this [Faq](https://www.themoviedb.org/faq/api)

## Setup
Postgresql 9.x or newer.

Eclipse Oxygen JEE or newer.

Install Eclipse Plugin 'Eclipse Wild Web Developer' of the Eclipse Marketplace.

Maven 3.3.3 or newer.

Nodejs 12.16.x or newer

Npm 6.13.x or newer

Angular Cli 9 or newer.