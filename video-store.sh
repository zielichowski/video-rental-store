#!/usr/bin/env bash

if [ "$1" == "-skipTests" ]; then
    echo "building without tests!"
    mvn clean install -DskipTests -pl rental-store-monolith -am

elif [ "$1" == "-run" ]; then
    echo "running app..."
    mvn -pl rental-store-monolith spring-boot:run

elif [ "$1" == "-e2e" ]; then
    echo "running e2e test..."
    mvn clean test -pl e2e-test -am
elif [ "$1" == "-build" ]; then
    mvn clean install -pl rental-store-monolith -am
else
    echo "Usage: $0 -build|-run|-skipTests|-e2e"
    exit 1
fi
