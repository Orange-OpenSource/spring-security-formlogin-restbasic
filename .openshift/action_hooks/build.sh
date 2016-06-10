#!/bin/bash

export JAVA_HOME=/etc/alternatives/java_sdk_1.8.0
export PATH=$JAVA_HOME/bin:$PATH

cd $OPENSHIFT_REPO_DIR
mvn package -DskipTests=true
