#!/bin/bash

sed -i -e '0,/<version>.*<\/version>/s/<version>.*<\/version>/<version>$1<\/version>/' pom.xml
sed -i -e '0,/<version>.*<\/version>/s/<version>.*<\/version>/<version>$1<\/version>/' vbox-key/pom.xml
sed -i -e '0,/<version>.*<\/version>/s/<version>.*<\/version>/<version>$1<\/version>/' VBoxManager/pom.xml
sed -i -e '0,/<version>.*<\/version>/s/<version>.*<\/version>/<version>$1<\/version>/' VBox-Instrumentation/pom.xml

