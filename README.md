# Camel Migration
Exploring how to migrate a small Camel 2.x application to Camel 3.x

Camel is a very flexible framework that is designed around [Enterprise Integration Patterns (EIP)](https://www.enterpriseintegrationpatterns.com/). 
The transition from the 2.x to 3.x represents a major and non-backward compatible change. 
This repo will explore how to use the various Camel migrations guides to migrate from Camel 2.x to Camel 3.x using a small application.
This application will consume messages from a Kafka topic and write them to a directory.

The starting point:
- a small Camel 2.x application
- using Spring 5.x 
- using JDK 8

The goal:
- a small Camel 3.x application
- using Spring 5.x
- using JDK 11



## Building and starting the application
This application is built using Camel `Main` which allows the [application to be run as a standalone Java application](http://people.apache.org/~dkulp/camel/running-camel-standalone.html) 
(instead of packaging it up as a WAR). The application will run until it is terminated (Ctrl-C).

Build the application

	mvn clean install

Run the application

	mvn camel:run