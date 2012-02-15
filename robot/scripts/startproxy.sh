#!/bin/bash

ant -f ../src/proxy/build.xml build
nxjpc -jar ../build/nxt/jars/simpleproxy.jar

