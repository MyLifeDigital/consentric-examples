# Java Consentric Client - Basic Example

This directory contains a basic example of a Java client for the Concentric API.

This project was built using Java 12 and application dependencies are managed using Gradle

This example contains a main method that will obtain a JWT for accessing the Consentric API.

You will need the following information to use this example:
 * Your client ID
 * Your client secret
 * Your consentric application ID
 * In ID of a privacy policy configured within your Consentric application
 * An ID of an Option configured within your Consentric application
 * An ID of a Permission Statement configured within your Consentric application

These values should be copied into the appropriate location in Main.class

To execute the example use the command `./gradlew clean run`