#!/bin/bash

cd $OPENSHIFT_REPO_DIR
mvn package -DskipTests=true
