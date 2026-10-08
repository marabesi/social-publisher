#!/bin/bash

rm -rf cli/build/distributions
./gradlew :cli:distZip :cli:shadowJar
cp ./cli/build/libs/social-1.0.0-all.jar ./social.jar
cd cli/build/distributions && \
    unzip -q ./social-1.0.0.zip && \
    rm -rf ./social-1.0.0.zip ./social-1.0.0.tar && \
    rm -rf ../../../social && \
    mv ./social-1.0.0 ../../../social