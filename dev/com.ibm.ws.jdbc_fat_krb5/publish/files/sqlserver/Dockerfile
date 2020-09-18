FROM mcr.microsoft.com/mssql/server:2019-latest

USER root

RUN set -ex; \
    echo krb5-config krb5-config/default_realm string LOCALDOMAIN | debconf-set-selections; \
    echo krb5-config krb5-config/kerberos_servers string | debconf-set-selections; \
    echo krb5-config krb5-config/add_servers_realm string LOCALDOMAIN | debconf-set-selections; \
    apt-get update; \
    printf '\n' | apt-get install -y --no-install-recommends \
        krb5-user \
        libpam-krb5 \
        libpam-ccreds \
        vim \
        inetutils-ping \
        realmd \
        software-properties-common \
        packagekit \
        net-tools \
        ifupdown \
        isc-dhcp-client \
        adcli;
        
RUN wget -O /tmp/adutil.deb 'https://packages.microsoft.com/ubuntu/18.04/mssql-server-preview/pool/main/a/adutil/adutil_0.6.061_amd64.deb' && \
    dpkg -i /tmp/adutil.deb && \
    rm /tmp/adutil.deb;

RUN chmod 777 /etc
RUN mkdir /etc/krb5
RUN touch /etc/krb5.conf && chmod 777 /etc/krb5.conf

#ADD setup/mssql.conf /var/opt/mssql/mssql.conf
RUN /opt/mssql/bin/mssql-conf set network.disablesssd true
RUN /opt/mssql/bin/mssql-conf set network.enablekdcfromkrb5conf true
RUN /opt/mssql/bin/mssql-conf set network.privilegedadaccount sqluser
RUN /opt/mssql/bin/mssql-conf set network.kerberoskeytabfile /etc/krb5.keytab

ADD setup/sqlserver.keytab /etc/krb5.keytab
RUN chmod 777 /etc/krb5.keytab
RUN chown mssql /etc/krb5.keytab

ADD docker-entrypoint.sh /
RUN chmod a+x /docker-entrypoint.sh

RUN chmod 777 /home

# USER mssql

ENV ACCEPT_EULA=true
ENV SA_PASSWORD=Testpassw0rd

# @AGG these are temporarily hard-coded
ENV KRB5_REALM=FYRETEST11.IBM.COM
ENV KRB5_KDC=testkdc1.fyre.ibm.com

ENTRYPOINT ["/docker-entrypoint.sh"]