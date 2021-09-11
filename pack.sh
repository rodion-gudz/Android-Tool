#!/bin/zsh
function get_keypress {
  local REPLY IFS=
  >/dev/tty printf '%s' "$*"
  [[ $ZSH_VERSION ]] && read -rk1
  [[ $BASH_VERSION ]] && </dev/tty read -rn1
  printf '%s' "$REPLY"
}

function get_yes_keypress {
  local prompt="${1:-Are you sure}"
  local enter_return=$2
  local REPLY
  # [[ ! $prompt ]] && prompt="[y/n]? "
  while REPLY=$(get_keypress "$prompt"); do
    [[ $REPLY ]] && printf '\n' # $REPLY blank if user presses enter
    case "$REPLY" in
      Y|y)  return 0;;
      N|n)  return 1;;
      '')   [[ $enter_return ]] && return "$enter_return"
    esac
  done
}

function confirm {
  local prompt="${*:-Are you sure} [y/N]? "
  get_yes_keypress "$prompt" 1
}

function confirm_yes {
  local prompt="${*:-Are you sure} [Y/n]? "
  get_yes_keypress "$prompt" 0
}

jarDir="$(pwd)/out/artifacts/Release"
repoDir="/Users/lavender/Android-Tool"
cd $jarDir

confirm "Update SDK" && {
wget -O Windows-SDK.zip -q --show-progress https://dl.google.com/android/repository/platform-tools-latest-windows.zip
wget -O MacOS-SDK.zip -q --show-progress https://dl.google.com/android/repository/platform-tools-latest-darwin.zip
wget -O Linux-SDK.zip -q --show-progress https://dl.google.com/android/repository/platform-tools-latest-linux.zip
echo "Download completed!"
echo "Unzipping..."
rm -rf Windows-SDK MacOS-SDK Linux-SDK
unzip -q Windows-SDK.zip -d Windows-SDK
unzip -q MacOS-SDK.zip -d MacOS-SDK
unzip -q Linux-SDK.zip -d Linux-SDK
rm -rf Windows-SDK.zip MacOS-SDK.zip Linux-SDK.zip
cd ./Windows-SDK/platform-tools && rm -rf systrace sqlite3.exe source.properties NOTICE.txt hprof-conv.exe etc1tool.exe dmtracedump.exe && cd -
cd ./Linux-SDK/platform-tools && rm -rf systrace lib64 sqlite3 source.properties NOTICE.txt hprof-conv etc1tool dmtracedump && cd -
cd ./MacOS-SDK/platform-tools && rm -rf systrace lib64 sqlite3 source.properties NOTICE.txt hprof-conv etc1tool dmtracedump && cd -
}

cp -f -p Android-Tool.jar ./Windows-SDK/platform-tools
cp -f -p Android-Tool.jar ./Linux-SDK/platform-tools
cp -f -p Android-Tool.jar ./MacOS-SDK/platform-tools
echo "Creating archives..."
cd ./Windows-SDK/platform-tools && zip -q -r Android-Tool.Portable-Windows.zip . -X && mv Android-Tool.Portable-Windows.zip $jarDir/Android-Tool.Portable-Windows.zip && cd -
cd ./Linux-SDK/platform-tools && zip -q -r Android-Tool.Portable-Linux.zip . -X && mv Android-Tool.Portable-Linux.zip $jarDir/Android-Tool.Portable-Linux.zip && cd -
cd ./MacOS-SDK/platform-tools && zip -q -r Android-Tool.Portable-MacOS.zip . -X && mv Android-Tool.Portable-MacOS.zip $jarDir/Android-Tool.Portable-MacOS.zip && cd -
zip -q Android-Tool.jar.zip Android-Tool.jar
confirm "Upload to x0.at" && {
echo "Uploading to x0.at..."
curl -F "file=@$jarDir/Android-Tool.jar" https://x0.at/
}
echo "Completed!"
confirm "Publish the release" && {
    echo "Creating a release"
    echo "Enter version"
    read version
    echo "latestVersion=$version" > $jarDir/values.properties
    cd $repoDir
    gh release create $version -F $(pwd)/changelog.md -t "Android-Tool v$version" $jarDir/Android-Tool.jar $jarDir/Android-Tool.Portable-Linux.zip $jarDir/Android-Tool.Portable-MacOS.zip  $jarDir/Android-Tool.Portable-Windows.zip $jarDir/values.properties
}