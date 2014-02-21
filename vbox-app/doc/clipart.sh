#!/bin/bash

ICONS=/home/kedzie/Development/Android_Design/Icons/All_Icons
PROJECT=/home/kedzie/Development/git/VBoxManager/vbox-app

for i in {mdpi,hdpi,xhdpi}; do
	cp $ICONS/holo_light/$i/$1 $PROJECT/doc/svg-$i/Holo-Light/$2
	cp $ICONS/holo_dark/$i/$1 $PROJECT/doc/svg-$i/Holo-Dark/$2
done
