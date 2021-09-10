#!/bin/bash
jarDir="$(pwd)/out/artifacts/Release"
repoDir="/Users/lavender/Android-Tool"
cd $jarDir
rm -rf ./*.zip
wget -O Windows.zip -q --show-progress https://dl.google.com/android/repository/platform-tools-latest-windows.zip
wget -O MacOS.zip -q --show-progress https://dl.google.com/android/repository/platform-tools-latest-darwin.zip
wget -O Linux.zip -q --show-progress https://dl.google.com/android/repository/platform-tools-latest-Linux.zip
echo "Download completed!"
echo "Unzipping..."
unzip -q Windows.zip -d Windows
unzip -q MacOS.zip -d MacOS
unzip -q Linux.zip -d Linux
rm -rf Windows.zip MacOS.zip Linux.zip
cd ./Windows/platform-tools && rm -rf systrace sqlite3.exe source.properties NOTICE.txt hprof-conv.exe etc1tool.exe dmtracedump.exe && cd -
cd ./Linux/platform-tools && rm -rf systrace lib64 sqlite3 source.properties NOTICE.txt hprof-conv etc1tool dmtracedump && cd -
cd ./MacOS/platform-tools && rm -rf systrace lib64 sqlite3 source.properties NOTICE.txt hprof-conv etc1tool dmtracedump && cd -
cp Android-Tool.jar ./Windows/platform-tools
cp Android-Tool.jar ./Linux/platform-tools
cp Android-Tool.jar ./MacOS/platform-tools
echo "Creating archives..."
cd ./Windows/platform-tools && zip -q -r Android-Tool.Portable-Windows.zip . -X && mv Android-Tool.Portable-Windows.zip $jarDir/Android-Tool.Portable-Windows.zip && cd -
cd ./Linux/platform-tools && zip -q -r Android-Tool.Portable-Linux.zip . -X && mv Android-Tool.Portable-Linux.zip $jarDir/Android-Tool.Portable-Linux.zip && cd -
cd ./MacOS/platform-tools && zip -q -r Android-Tool.Portable-MacOS.zip . -X && mv Android-Tool.Portable-MacOS.zip $jarDir/Android-Tool.Portable-MacOS.zip && cd -
rm -rf Windows MacOS Linux
zip -q Android-Tool.jar.zip Android-Tool.jar
# echo "Uploading to x0.at..."
# curl -F "file=@$jarDir/Android-Tool.jar" https://x0.at/
echo "Completed!"
echo "Creating a release"
echo "Enter version"
read version
echo "latestVersion=$version" > $jarDir/values.properties
cd $repoDir
gh release create $version -F $(pwd)/changelog.md -t "Android-Tool v$version" $jarDir/Android-Tool.jar $jarDir/Android-Tool.Portable-Linux.zip $jarDir/Android-Tool.Portable-MacOS.zip  $jarDir/Android-Tool.Portable-Windows.zip $jarDir/values.properties
