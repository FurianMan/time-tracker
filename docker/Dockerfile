FROM mysql:8.0
  
# RUN groupadd -r mygroup && useradd -r -g mygroup myuser
WORKDIR /scripts
COPY scripts /scripts

USER root
RUN ["/bin/bash", "-c", "chmod +x /scripts/preparation_mysql.sh"]
CMD ["/scripts/preparation_mysql.sh"]
