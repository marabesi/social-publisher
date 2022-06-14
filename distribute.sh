#!/bin/bash

rm -rf build/distributions
./gradlew distZip
cd build/distributions && \
    unzip ./social-1.0-SNAPSHOT.zip && \
    rm -rf ./social-1.0-SNAPSHOT.zip ./social-1.0-SNAPSHOT.tar