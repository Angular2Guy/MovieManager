#!/bin/sh
docker pull postgres
docker run --name local-postgres -e POSTGRES_PASSWORD=sven1 -e POSTGRES_USER=sven1 -e POSTGRES_DB=movies -p 5432:5432 -d postgres
# depricated psql -h localhost -U sven1 -f src/main/resources/db/initDB.sql movies
# docker start local-postgres
# docker stop local-postgres