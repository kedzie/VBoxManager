#!/bin/bash
echo 'Changing project version to v$1'
sed -i -e '0,/<version>.*<\/version>/s/<version>.*<\/version>/<version>$1<\/version>/' pom.xml
sed -i -e '0,/<version>.*<\/version>/s/<version>.*<\/version>/<version>$1<\/version>/' vbox-key/pom.xml
sed -i -e '0,/<version>.*<\/version>/s/<version>.*<\/version>/<version>$1<\/version>/' vbox-app/pom.xml
sed -i -e '0,/<version>.*<\/version>/s/<version>.*<\/version>/<version>$1<\/version>/' vbox-instrumentation/pom.xml

