FROM postgres:latest
MAINTAINER horfeusz
ENV POSTGRES_PASSWORD postgres
COPY init.sql /docker-entrypoint-initdb.d/
EXPOSE 5432