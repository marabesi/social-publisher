#!/bin/bash

rm -rf build/distributions
./gradlew distZip
cd build/distributions && \
    unzip ./social-1.0.0.zip && \
    rm -rf ./social-1.0.0.zip ./social-1.0.0.tar && \
    mv ./social-1.0.0 ../../social
