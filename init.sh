#!/bin/sh
BRANCH=${1:-'master'}
echo Initializing eclipse projects with branch [$1]..
git submodule init
git submodule update
cat ./tree-view-list-android/.project | sed -e s/tree-view-list-android/ActionBarSherlock/ > ./ActionBarSherlock/actionbarsherlock/.project
cp ./tree-view-list-android/.classpath ./ActionBarSherlock/actionbarsherlock/.classpath
cat ./tree-view-list-android/.project | sed -e s/tree-view-list-android/NineOldDroids/ > ./NineOldDroids/library/.project
cp ./tree-view-list-android/.classpath ./NineOldDroids/library/.classpath
git checkout $BRANCH
cd VBoxManager
git checkout $BRANCH
cd ../VBox-Instrumentation
git checkout $BRANCH
cd .. 
