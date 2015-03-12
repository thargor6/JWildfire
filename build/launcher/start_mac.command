#!/bin/bash
cd "$(dirname "$0")"
java -Dapple.eawt.quitStrategy=CLOSE_ALL_WINDOWS -jar ./j-wildfire-launcher.jar

