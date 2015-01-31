#!/bin/sh
set -e
cd BluePlaquesLondon
./gradlew :build \
          :App:testNormalDebug \
          :App:jacocoReport \
          :App:coveralls \
          :App:testIntegrationDebug \
          :App:connectedAndroidTestIntegrationDebug \
          -PtravisCi -PdisablePreDex
