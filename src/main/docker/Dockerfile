FROM java:8

RUN bash -c "mkdir -p /opt/ooziz/lib/"
COPY ./lib /opt/ooziz/lib
COPY ./trade.jar /opt/ooziz/trade.jar
WORKDIR /opt/ooziz/

EXPOSE 9100
CMD java -jar trade.jar
#ENTRYPOINT  ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]