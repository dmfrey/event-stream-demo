#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail
set -e -x

export TERM=xterm

function main() {

    pushd src-repo

    echo "Build and Publish artifacts"
    $BUILD_CMD
    popd

    exit 0
}

# INIT
main "$@"
