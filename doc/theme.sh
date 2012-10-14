#!/bin/sh

cp -f ../res/drawable-mdpi/working/Holo-$1/*.png ../res/drawable-mdpi/
cp -f ../res/drawable-hdpi/working/Holo-$1/*.png ../res/drawable-hdpi/

if [ "$1" = "Dark" ]; then
sed -i 's/android:theme=\"@style\/.*\"/android:theme=\"@style\/Theme.Sherlock\"/' ../AndroidManifest.xml
else
sed -i 's/android:theme=\"@style\/.*\"/android:theme=\"@style\/Theme.Sherlock.Light\"/' ../AndroidManifest.xml
fi



