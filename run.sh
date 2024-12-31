#!/bin/bash

./distribute.sh

docker build --platform=linux/amd64 . -t social

docker run --platform=linux/amd64 --rm social
