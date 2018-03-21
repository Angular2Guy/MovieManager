drop table Movie cascade;
create table if not exists Movie (
	id BIGSERIAL PRIMARY KEY,
	overview TEXT,
	releaseDate DATE,
	title VARCHAR(255),
	movieid int
);
ALTER SEQUENCE Movie_id_seq RESTART WITH 100;
drop table Actor cascade;
create table if not exists Actor (
	id BIGSERIAL PRIMARY KEY,
	actorid int,
	name VARCHAR(255),
	gender int,
	birthday DATE,
	deathday DATE,
	biography TEXT,
	placeOfBirth VARCHAR(1024)
);
ALTER SEQUENCE Actor_id_seq RESTART WITH 100;
drop table Cast1 cascade;
create table if not exists Cast1 (
	id BIGSERIAL PRIMARY KEY,
	movieChar VARCHAR(255),
	characterName VARCHAR(255),
	movie_id BIGINT,
	actor_id BIGINT,
	FOREIGN KEY (movie_id) REFERENCES Movie(id),
	FOREIGN KEY (actor_id) REFERENCES Actor(id)	
);
ALTER SEQUENCE Cast1_id_seq RESTART WITH 100;
drop table Genere cascade;
create table if not exists Genere (
	id BIGSERIAL PRIMARY KEY,
	genereid int,
	name VARCHAR(255)
);
ALTER SEQUENCE Genere_id_seq RESTART WITH 100;
drop table Movie_Genere cascade;
create table if not exists Movie_Genere (
	movie_id BIGINT,
	genere_id BIGINT,
	FOREIGN KEY (movie_id) REFERENCES Movie(id),
	FOREIGN KEY (genere_id) REFERENCES Genere(id)
);
drop table user1;
create table if not exists user1 (
	id BIGSERIAL PRIMARY KEY,
	username VARCHAR(255),
	password VARCHAR(255),
	moviedbkey VARCHAR(255),
	roles VARCHAR(255)
);
drop table Movie_User cascade;
create table if not exists Movie_User (
	movie_id BIGINT,
	user_id BIGINT,
	FOREIGN KEY (movie_id) REFERENCES Movie(id),
	FOREIGN KEY (user_id) REFERENCES user1(id)
);
drop table Actor_User cascade;
create table if not exists Actor_User (
	actor_id BIGINT,
	user_id BIGINT,
	FOREIGN KEY (actor_id) REFERENCES Actor(id),
	FOREIGN KEY (user_id) REFERENCES user1(id)
);