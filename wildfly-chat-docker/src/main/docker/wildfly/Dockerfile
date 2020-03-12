FROM jboss/wildfly:18.0.1.Final
RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/org/postgresql && \
   mkdir -p /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main
ADD postgresql-42.2.10.jar /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main
ADD module.xml /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main
ADD standalone.xml /opt/jboss/wildfly/standalone/configuration
ADD standalone.bat /opt/jboss/wildfly/bin

EXPOSE 8090 9990 8787 3528