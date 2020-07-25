#!/bin/bash

# pull
./mvnw docker-compose:pull@pull

echo "$REDIS_ENV"

if [[ "$REDIS_ENV" == prod ]]
then
    echo match
    ./mvnw docker-compose:up@up
elif [[ "$REDIS_ENV" == cluster ]]
then
    ./mvnw docker-compose:up@up
elif [[ "$REDIS_ENV" == cluster ]]
then
    ./mvnw docker-compose:up@up
fi

# mvn docker-compose:down@down