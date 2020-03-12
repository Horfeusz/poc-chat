FROM jboss/base-jdk:11

ENV WILDFLY_VERSION 18.0.1.Final
ENV WILDFLY_SHA1 ef0372589a0f08c36b15360fe7291721a7e3f7d9
ENV JBOSS_HOME /opt/jboss/wildfly

USER root

RUN cd $HOME \
    && curl -O https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz \
    && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME \
    && rm wildfly-$WILDFLY_VERSION.tar.gz \
    && chown -R jboss:0 ${JBOSS_HOME} \
    && chmod -R g+rw ${JBOSS_HOME}

RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/org/postgresql && \
    mkdir -p /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main
ADD postgresql-42.2.10.jar /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main
ADD module.xml /opt/jboss/wildfly/modules/system/layers/base/org/postgresql/main
ADD standalone.xml /opt/jboss/wildfly/standalone/configuration

ENV LAUNCH_JBOSS_IN_BACKGROUND true

USER jboss

RUN /opt/jboss/wildfly/bin/add-user.sh admin admin --silent

EXPOSE 8090 9990 8787 3528

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "--debug", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]