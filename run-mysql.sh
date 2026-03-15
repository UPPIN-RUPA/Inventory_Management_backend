#!/bin/sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR"

if [ ! -f .env ]; then
  echo ".env not found. Copy .env.example to .env first."
  exit 1
fi

set -a
. ./.env
set +a

export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"

docker compose up -d

exec sh ./mvnw spring-boot:run
