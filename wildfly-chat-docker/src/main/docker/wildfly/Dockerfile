#FROM jboss/base-jdk:11
FROM jboss/base-jdk:8

ENV WILDFLY_VERSION 17.0.1.Final
ENV WILDFLY_SHA1 eaef7a87062837c215e54511c4ada8951f0bd8d5
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
# ADD standalone.xml /opt/jboss/wildfly/standalone/configuration
# ADD standalone.conf.ps1 /opt/jboss/wildfly/bin

ENV LAUNCH_JBOSS_IN_BACKGROUND true

USER jboss

RUN /opt/jboss/wildfly/bin/add-user.sh -u 'admin' -p 'admin'
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u 'ejbuser' -p 'pasword123'

EXPOSE 8090 9990 8787 3528
#EXPOSE 8080 9990 8787 3528

# CMD ["/opt/jboss/wildfly/bin/standalone.sh", "--debug", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]