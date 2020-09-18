#!/bin/sh

if [ -z ${KRB5_REALM} ]; then
    echo "No KRB5_REALM Provided. Exiting ..."
    exit 1
fi

if [ -z ${KRB5_KDC} ]; then
    echo "No KRB5_KDC Provided. Exting ..."
    exit 1
fi

if [ -z ${KRB5_ADMINSERVER} ]; then
    echo "No KRB5_ADMINSERVER provided. Defaulting to ${KRB5_KDC}"
    KRB5_ADMINSERVER=${KRB5_KDC}
fi

echo "Creating Krb5 Client Configuration"

cat <<EOT > /etc/krb5.conf
[libdefaults]
 dns_lookup_realm = false
 ticket_lifetime = 24h
 renew_lifetime = 7d
 forwardable = true
 rdns = false
 default_realm = ${KRB5_REALM}
 
[realms]
 ${KRB5_REALM} = {
    kdc = ${KRB5_KDC}
    admin_server = ${KRB5_ADMINSERVER}
 }
EOT

### old stuff
#cat <<EOT > /etc/network/interfaces
#auto eth0
#iface eth0 inet dhcp
#dns-nameservers 9.46.74.172
#dns-search testkdc1.fyre.ibm.com
#EOT

# realm join FYRETEST11.IBM.COM -U 'sqluser@FYRETEST11.IBM.COM' -v --install=/
# the password is 'Passw0rd' for sqluser

# adutil user create --name sqluser -distname CN=sqluser,CN=Users,DC=FYRETEST11,DC=IBM,DC=COM --password 'Passw0rd'

## current stuff
# 1) add to FRONT of /etc/resolv.conf
# nameserver 9.46.74.172

# 2) join the realm
# mkdir /etc/sssd
# realm join FYRETEST11.IBM.COM -v --install=/
# testkdc1 creds are Administrator / S3cur!ty

# echo 'Passw0rd' | kinit sqluser@FYRETEST11.IBM.COM

echo "Adding entries to keytab..."
printf 'add_entry -password -p sqluser/sqlserver@'"${KRB5_REALM}"' -k 1 -e aes256-cts\npassword\nwkt /etc/krb5.keytab' | ktutil
printf 'add_entry -password -p MSSQLSvc/sqlserver:1433@'"${KRB5_REALM}"' -k 1 -e aes256-cts\npassword\nwkt /etc/krb5.keytab' | ktutil

#printf 'add_entry -password -p sqluser@FYRETEST11.IBM.COM -k 1 -e aes256-cts\nPassw0rd\nwkt /etc/krb5.keytab' | ktutil
#printf 'add_entry -password -p MSSQLSvc/48a549eb2b93:1433@FYRETEST11.IBM.COM -k 1 -e aes256-cts\npassword\nwkt /etc/krb5.keytab' | ktutil

echo "Starting SQLServer..."
/opt/mssql/bin/sqlservr