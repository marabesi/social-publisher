#!/bin/bash

rm -rf data/
mkdir data
./gradlew :cli:cucumber
