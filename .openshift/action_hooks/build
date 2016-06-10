#!/bin/bash

export JAVA_HOME=/etc/alternatives/java_sdk_1.8.0
export M2_HOME=$OPENSHIFT_DATA_DIR/apache-maven-3.3.9
export PATH=$M2_HOME/bin:$PATH
export PATH=$JAVA_HOME/bin:$PATH

cd $OPENSHIFT_REPO_DIR
mvn -s $OPENSHIFT_DATA_DIR/m2/settings.xml package -DskipTests=true

