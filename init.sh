#!/bin/sh
#git submodule init
#git submodule update
cat ./tree-view-list-android/.project | sed -e s/tree-view-list-android/ActionBarSherlock/ > ./ActionBarSherlock/actionbarsherlock/.project
cp ./tree-view-list-android/.classpath ./ActionBarSherlock/actionbarsherlock/.classpath
cat ./tree-view-list-android/.project | sed -e s/tree-view-list-android/NineOldAndroids/ > ./NineOldAndroids/library/.project
cp ./tree-view-list-android/.classpath ./NineOldAndroids/library/.classpath
