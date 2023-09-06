#!/bin/bash
RUNTIME_VERSION=4.4.0-20230522
MUNIT_JVM=$(/usr/libexec/java_home -v 11)/bin/java

# shellcheck disable=SC2155
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
mvn clean
mkdir target
mvn verify \
    -DruntimeProduct=MULE_EE \
    -Dmule.disableSdkComponentIgnore=true \
    -Dmunit.test.timeout=1200000 \
    -DruntimeVersion=$RUNTIME_VERSION \
    -Dmunit.jvm="$MUNIT_JVM" \
    -Dmtf.javaopts="--illegal-access=warn" -e > ./target/test.log
cat ./target/test.log | grep "WARNING: Illegal reflective access by" > ./target/illegal-access.log
