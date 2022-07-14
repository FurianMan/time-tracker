FROM mysql:8.0
COPY ./database.sql /docker-entrypoint-initdb.d/database.sql
COPY ./data /var/lib/mysql
COPY ./mycustom.cnf /etc/mysql/conf.d/custom.cnf
USER root