#!/bin/bash

kill -9 `ps -aef | grep ContentBroker.jar | grep -v grep | awk '{print $2}'`

rm -f /tmp/cb.running
