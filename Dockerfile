FROM denvazh/gatling

ADD src/test/scala /opt/gatling/user-files/simulations

CMD ["bin/gatling.sh", "-s", "example.DroolsJarConsumerSimulation"]

