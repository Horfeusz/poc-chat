create extension pgcrypto;

CREATE TABLE role (
id   SERIAL PRIMARY KEY,
name VARCHAR(50) NOT NULL
);

CREATE TABLE poc_user (
id       SERIAL PRIMARY KEY,
login    VARCHAR(10) NOT NULL,
password VARCHAR(100) NOT NULL,
hash_password VARCHAR (100) NOT NULL
);

CREATE TABLE poc_user_role (
id_poc_user INTEGER NOT NULL,
id_role INTEGER NOT NULL
);

INSERT INTO public.role(name) VALUES ('guest');
INSERT INTO public.role(name) VALUES ('manager');

INSERT INTO public.poc_user(login, password, hash_password) VALUES ('Rolf', 'Rolf123', '97d95356f734c5b8e4cc9b3e90e2c949b37bd7c3c7c274cb2809b3b732f3625e');
INSERT INTO public.poc_user(login, password, hash_password) VALUES ('Hendrik', 'Hendrik123', 'c34f242be5c2bd1a34ca175f362562730fe90c57e354f8251bf6d3a748821b4a');
INSERT INTO public.poc_user(login, password, hash_password) VALUES ('Michal', 'Michal123', 'ece9044722c614c8fc9b6553f3856f170ab4fb14db06a4f2afc83d1f2399e337');

INSERT INTO public.poc_user_role
SELECT pu.id, r.id
  FROM public.poc_user pu, public.role r
WHERE r.name = 'guest'
  AND pu.login = 'Rolf';

INSERT INTO public.poc_user_role
SELECT pu.id, r.id
  FROM public.poc_user pu, public.role r
WHERE r.name = 'guest'
  AND pu.login = 'Hendrik';

INSERT INTO public.poc_user_role
SELECT pu.id, r.id
  FROM public.poc_user pu, public.role r
WHERE r.name = 'guest'
  AND pu.login = 'Michal';

INSERT INTO public.poc_user_role
SELECT pu.id, r.id
  FROM public.poc_user pu, public.role r
WHERE r.name = 'manager'
  AND pu.login = 'Hendrik';

INSERT INTO public.poc_user_role
SELECT pu.id, r.id
  FROM public.poc_user pu, public.role r
WHERE r.name = 'manager'
  AND pu.login = 'Michal';

CREATE VIEW vrole_use as
SELECT r.name, u.login FROM role r
  JOIN poc_user_role ur ON ur.id_role = r.id
  JOIN poc_user u ON u.id = ur.id_poc_user;
