#!/usr/bin/env bash
#
# Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# See https://dzone.com/articles/how-to-publish-artifacts-to-maven-central for help on releasing from GitHub
#

###
### A bash script to simplify Maven releases.
###

# Make sure no pending change is on the working directory
modified_count=`git status --porcelain | wc -l`
if [[ "${modified_count}" -ne "0" ]]; then
   echo "Your working directoy contains uncommitted changes."
   exit 1
fi

# Make sure no pull can be made before release
LOCAL_ORIGIN=$(git rev-parse @)
REMOTE_ORIGIN=$(git rev-parse @{u})
if [[ "${LOCAL_ORIGIN}" != "${REMOTE_ORIGIN}" ]]; then
   echo "Your local branch is not synchronized with its remote branch."
   exit 1
fi

# How to use the script
function usage {
   echo "Release using Apache Maven"
   echo ""
   echo "Usage:"
   echo "   release.sh REL_VERSION DEV_VERSION [GPG_KEYNAME] [GPG_PASS]"
   echo ""
   echo "      REL_VERSION    The version to use for the release"
   echo "      DEV_VERSION    The version to use for the development"
   echo "      GPG_KEYNAME    The GPG key name (optional)"
   echo "      GPG_PASS       The GPG passphrase (optional)"
   echo ""
}

if [[ $# -lt "2" ]]; then
   usage
   exit 1
fi

REL_VERSION="$1"
DEV_VERSION="$2"
REL_TAG="v${REL_VERSION}"

EXTRA_ARGS=""
if [[ $# -eq 4 ]]; then
    GPG_KEYNAME="$3"
    GPG_PASS="$4"
    EXTRA_ARGS="-Dgpg.passphrase=${GPG_PASS} -Dgpg.keyname=${GPG_KEYNAME}"
fi

mvn clean
mvn -Prelease release:prepare -DreleaseVersion="${REL_VERSION}" -DdevelopmentVersion="${DEV_VERSION}" -Dtag=${REL_TAG} -Darguments="${EXTRA_ARGS}"
mvn -Prelease release:perform -DreleaseVersion="${REL_VERSION}" -DdevelopmentVersion="${DEV_VERSION}" -Dtag=${REL_TAG} -Darguments="${EXTRA_ARGS}"
mvn -Prelease deploy ${EXTRA_ARGS}

echo "Released version:       ${REL_VERSION}"
echo "Release tag:            ${REL_TAG}"
echo "Development version:    ${DEV_VERSION}"
