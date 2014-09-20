#!/bin/sh

cp -f ./doc-mdpi/Holo-$1/*.png ../res/drawable-mdpi/
cp -f ./doc-hdpi/Holo-$1/*.png ../res/drawable-hdpi/
cp -f ./doc-xhdpi/Holo-$1/*.png ../res/drawable-xhdpi/

if [ "$1" = "Dark" ]; then
sed -i 's/android:theme=\"@style\/.*\"/android:theme=\"@style\/Theme.Sherlock\"/' ../AndroidManifest.xml
else
sed -i 's/android:theme=\"@style\/.*\"/android:theme=\"@style\/Theme.Sherlock.Light\"/' ../AndroidManifest.xml
fi



