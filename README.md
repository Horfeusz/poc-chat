# Application POC-CHAT 
WildFly and GlassFish remote EJB integration.

## Introduction
Here is an example of how to integrate EJB servers: GlassFish 3.1 (Java 1.7) and WildFly 17 (Java 1.8)
Integration is at the EJB level. This is configuration and application for GlassFish.\
Source application for [GlassFish]

## Configuration for WildFly 17

### Security
The Application will be use JDBC-realm. In the sources is located docker image.
This image create database PostgreSQL. Script [init.sql] create examples structure and realms.\
The image can create the command:
```sh
docker build -t poc-image-postgres .
```

### Copy JDBC driver to Server:
Copy files [postgresql-42.2.10.jar] and [module.xml] to ${WILDFLY_HOME}/modules/system/layers/base/org/postgresql \
If the "postgresql" folder does not exist, create it.

### Use standalone-full configuration:
I changed file name standalone.xml on standalone-std.xml and I changed file name standalone-full.xml on standalone.xml.
Your way may be different. 

### Use CLI to configure the server:
Start server first.
```sh
jboss-cli.sh -c --controller=127.0.0.1:9990 --user=admin --password=[YOUR ADMIN PASS] --file=configure-server.cli
```

### System properties
poc-chat-glassfish-host: host definition where GlassFish is 
poc-chat-glassfish-port: port definition where GlassFish is

[postgresql-42.2.10.jar]: https://jdbc.postgresql.org/download/postgresql-42.2.10.jar
[module.xml]: https://github.com/Horfeusz/poc-chat/blob/master/wildfly-chat-docker/src/main/docker/wildfly/module.xml
[GlassFish]: https://github.com/Horfeusz/poc-chat-glass