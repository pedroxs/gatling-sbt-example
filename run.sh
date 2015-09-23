#!/bin/bash

url="drools-service-287093496.us-west-2.elb.amazonaws.com"
port="80"
users="1080000"
admins="1000"
duration="6"
time="hours"

docker run -d \
    --name 1080k-6h \
    -v $(pwd)/results:/opt/gatling/results \
    -v $(pwd)/scala:/opt/gatling/user-files/simulations \
    -e JAVA_OPTS="-DhostUrl=$url -DhostPort=$port -Dusers=$users -Dadmins=$admins -Dduration=$duration -Dtime=$time" \
    denvazh/gatling bin/gatling.sh -s example.DroolsJarConsumerSimulation

#    denvazh/gatling bin/gatling.sh -ro droolsjarconsumersimulation-1442720129137
