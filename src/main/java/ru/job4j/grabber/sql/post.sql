CREATE TABLE post (
	id serial primary key,
	name varchar(255) NOT NULL,
	text text,
	link text NOT NULL UNIQUE,
	created date NOT NULL
);