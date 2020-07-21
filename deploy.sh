#!/bin/bash

# pull
./mvnw docker-compose:pull@pull
# up
./mvnw docker-compose:up@up
# down
# mvn docker-compose:down@down