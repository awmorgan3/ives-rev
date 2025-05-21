ARG REGISTRY_URL="ecp-non-prod.nexus-ecp.web.irs.gov/"
ARG IMAGE="ubi9/openjdk-17-runtime"
ARG IMAGE_TAG="latest"

FROM "${REGISTRY_URL}${IMAGE}:${IMAGE_TAG}"

LABEL io.k8s.description "ives-bwas application image"

ENV SCRIPT_DEBUG=false
ENV JAVA_OPTIONS="-Xms512m -Xmx512m -Dspring.profiles.active=default -Dcom.redhat.fips=false"

USER root
WORKDIR /deployments

RUN mkdir -p /deployments

COPY . /deployments/

RUN echo 'java $JAVA_OPTIONS -jar /deployments/wa-ives-bwas-service-exec.jar' > start.sh

RUN chgrp -R 0 /deployments && chmod -R g+rwX /deployments

USER jboss
EXPOSE 8080
ENTRYPOINT ["bash", "start.sh"] 