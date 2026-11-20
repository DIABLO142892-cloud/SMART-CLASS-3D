#!/bin/sh
PRGDIR="$(cd "$(dirname "$0")" && pwd)"
exec "$PRGDIR/.mvn/wrapper.jar" "$@"