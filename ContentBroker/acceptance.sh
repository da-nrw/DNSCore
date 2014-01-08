#!/bin/bash

# author: Daniel M. de Oliveira

echo prepare acceptance testing

mvn failsafe:integration-test
