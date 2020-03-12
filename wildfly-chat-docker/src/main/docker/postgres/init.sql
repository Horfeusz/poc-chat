create extension pgcrypto;

CREATE TABLE role (
id   SERIAL PRIMARY KEY,
name VARCHAR(50) NOT NULL
);

CREATE TABLE poc_user (
id       SERIAL PRIMARY KEY,
login    VARCHAR(10) NOT NULL,
password VARCHAR(100) NOT NULL
);

CREATE TABLE poc_user_role (
id_poc_user INTEGER NOT NULL,
id_role INTEGER NOT NULL
);

INSERT INTO public.role(name) VALUES ('guest');
INSERT INTO public.role(name) VALUES ('manager');

INSERT INTO public.poc_user(login, password) VALUES ('Rolf', 'Rolf123');
INSERT INTO public.poc_user(login, password) VALUES ('Hendrik', 'Hendrik123');

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
WHERE r.name = 'manager'
  AND pu.login = 'Hendrik';

CREATE VIEW vrole_use as
SELECT r.name, u.login FROM role r
  JOIN poc_user_role ur ON ur.id_role = r.id
  JOIN poc_user u ON u.id = ur.id_poc_user;
