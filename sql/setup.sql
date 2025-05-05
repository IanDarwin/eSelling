--
-- Setup for eselling database
--
-- If any fields have been added since this was made,
-- the "update" mode should take care of them.
--

create table item (
	id int8 primary key,
	description varchar not null,
	sold_date date,
	sold_for float4,
	askingprice float8,
	name varchar not null,
	soldprice float8,
	urls _varchar,
	condition varchar,
);

create table users (
	id int8 primary key,
	name varchar,
	password varchar,
	passwordhashed varchar,
);
