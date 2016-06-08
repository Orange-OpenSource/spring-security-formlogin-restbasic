#!/bin/sh

./mvnw license:add-third-party && \
cp THIRD-PARTY.txt 3RDPARTY.TXT && rm THIRD-PARTY.txt
