#!/bin/bash

# -s example.DroolsJarConsumerSimulation

docker run -it --rm \
    -v $(pwd)/target:/opt/gatling/results \
    -v $(pwd)/src/test/scala:/opt/gatling/user-files/simulations \
    --link drools-consumer:drools-consumer \
    -e JAVA_OPTS="-DhostUrl=drools-consumer -Dusers=20 -Dadmins=5 -Dduration=30 -Dtime=seconds" \
    denvazh/gatling bin/gatling.sh -s example.DroolsJarConsumerSimulation
