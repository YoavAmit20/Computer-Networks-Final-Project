#!/bin/bash

JAVA_EXECUTABLE="java"

MAIN_CLASS="Server"

JAVA_OPTIONS="-Xmx512M -Xms256M"

# Change directory to Sources
cd Sources

$JAVA_EXECUTABLE $JAVA_OPTIONS -cp . $MAIN_CLASS