#!/bin/bash

set -e

# set -x # -> for debug

#rm -rf /tmp/heroku

parse_git_branch() {
  git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/* \(.*\)/ (\1) /'
}
parse_commit_hash() {
  git log -1 | head -1
}
sync_folders() {
  rsync -rP --delete --exclude=.git $1/ $2/ >/dev/null
}

# config
PROJECT="myrepo-all"
REPO="https://github.com/bbozo/$PROJECT.git"
TMPDIR="/var/tmp/heroku"
COMMIT_LOG="`parse_git_branch`: `parse_commit_hash`"

# globals
here=$(pwd)

if [ -z "$JENKINS_HOME" ]; then
  CODE_SOURCE_DIR="$TMPDIR/$PROJECT"
else
  CODE_SOURCE_DIR=$(pwd)
fi



prepare() {
  echo
  echo "=============== Fetch latest version of code from GH ==============="
  echo

  # make sure our changes our there, but no need on jenkins
  echo "Pushing local changes...."
  git push
  echo
  
  # make sure TMPDIR is in place and go there
  if [ ! -d $TMPDIR ];  then mkdir $TMPDIR;    fi
  cd $TMPDIR
  
  # make sure REPO is in place and go there
  if [ ! -d $PROJECT ]; then git clone $REPO;  fi
  cd $PROJECT
  
  # checkout last version of whatever needs checkouting
  if [ "$#" -gt "0" ];  then git checkout $@;  fi
  echo "Pulling from GitHub..."
  git pull
  echo
}

function deploy() {

  export TARGET=$1
  
  function sync_common() {
    # replace vendor/common symling with the actual folder
    echo "Syncing vendor/common..."
    if [ -f "vendor/common" ]; then
      rm "vendor/common"
    fi
    sync_folders "$CODE_SOURCE_DIR/common" "$TMPDIR/$TARGET/vendor/common"
    echo "OK"
    echo
  }

  echo
  echo "=================== STEP 2: assemble the deployment code ==================="
  echo

  cd $TMPDIR
  
  echo "Pulling from Heroku..."
  if [ ! -d $1 ]; then heroku git:clone -a $1; fi
  cd $1
  git pull || echo "WARNING GIT PULL FAILED"
  echo

  echo "Assembling project-specific code..."
  # sync the main part of the project
  if [ "$1" = "web-host" ] ; then
    sync_folders "$CODE_SOURCE_DIR/frontend-admin-site/public_html" "$TMPDIR/$1"
    sync_common
  elif [ "$1" = "test-web-host" ] ; then
    sync_folders "$CODE_SOURCE_DIR/Frontend/themes/site" "$TMPDIR/$1"
    sync_common
  elif [ "$1" = "api-host"    ] || [ "$1" = "test-api-host"    ] ||
       [ "$1" = "upload-host" ] || [ "$1" = "test-upload-host" ]; then
    sync_folders "$CODE_SOURCE_DIR/backend-rest-api" "$TMPDIR/$1"
    sync_common
  elif [ "$1" = "test-web-legacy" ]; then
    sync_folders "$CODE_SOURCE_DIR/frontend-admin-site/public_html" "$TMPDIR/$1"
    sync_common
  else
    echo "I don't understand profile $1"
    exit 1
  fi
  echo "OK"
  echo

  
  
  echo
  echo "========================== STEP 3: commit & push ==========================="
  echo
  
  cd $TMPDIR/$1
  
  echo "Commiting..."
  git add .
  if git status | grep "Changes" ; then
    git commit -a -m "$COMMIT_LOG"
  fi
  echo
  
  echo "Pushing...."
  git push
  echo
  
  echo "Sending an async ping so that the box wakes up... OK"
  curl "https://"$1".herokuapp.com" &>/dev/null &
  echo

  echo "Heroku logs:"
  echo "====================================" 
  echo
  heroku logs -a $1 -n10000 >$here/log/heroku_log-$profile.log &
  heroku logs -a $1 -n100
}

if [ -z "$JENKINS_HOME" ]; then
  for profile do
    if echo $profile|grep -v '^test.*$' >/dev/null; then
      echo 
      echo "         YOU ARE DEPLOYING INTO PRODUCTION TO $profile, YOU CAN PRESS CTRL+C NOW IF YOU WISH TO STOP!!!!"
      echo
      read
    fi
  done

  prepare
fi

echo
echo "==================== Deploying the code to Heroku =================="
echo

for profile do
  deploy $profile 2>&1 | sed -re 's/(.*)/\['$profile'\] \1/g'  &
done

wait
echo
