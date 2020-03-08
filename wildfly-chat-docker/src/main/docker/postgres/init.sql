create extension pgcrypto;

CREATE TABLE role (
id   SERIAL PRIMARY KEY,
name CHAR(50) NOT NULL
);

CREATE TABLE poc_user (
id       SERIAL PRIMARY KEY,
login    CHAR (50) NOT NULL,
password CHAR (100) NOT NULL
);

CREATE TABLE poc_user_role (
id_poc_user INTEGER NOT NULL,
id_role INTEGER NOT NULL
);

INSERT INTO public.role(name) VALUES ('guest');
INSERT INTO public.role(name) VALUES ('manager');

INSERT INTO public.poc_user(login, password) VALUES ('Rolf', MD5('Rolf123'));
INSERT INTO public.poc_user(login, password) VALUES ('Hendrik', MD5('Hendrik123'));

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
