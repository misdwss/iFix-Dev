# Changelog
All notable changes to this module will be documented in this file.

## 1.0.3 - 2021-01-19
- Upgraded the log4j version to 2.17.1 for vulnerability fix

## 1.0.2 - 2021-10-29
- Fiscal-Event-Service will receive bulk fiscal event request data in push api request. And it will validate all the bulk data in request and in case of any missing required or invalid attributes , will throw the corresponding error message and stop processing.

## 1.0.1 - 2021-10-08
- Fiscal-Event-Service receives the EventType enum of FiscalEvent model in ALL_CAPS but it will process it in 
  Pascal_Case. The record in the kafka topic will get the enum in Pascal_Case. 

## 1.0.0 - 2021-09-24
- Baseline version released
