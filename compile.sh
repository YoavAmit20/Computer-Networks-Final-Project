#!/bin/bash

# Change directory to Sources
cd Sources

# Compile Server.java
javac Server.java

if [ $? -eq 0 ]; then
    echo "Compilation successful. You can now run your server."
else
    echo "Compilation failed. Please check the error messages above."
fi
