#!/bin/bash

curl -v -s -S -X POST localhost:8080/someController/search -H "Content-Type:application/json" --data-binary '{"userId":-8001234567}' --basic -u "user1:password"
