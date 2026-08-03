#!/usr/bin/env bash

set -Eeuo pipefail

if [ "$#" -ne 1 ]; then
  echo "사용법: $0 /영화/데이터/파일.json"
  exit 1
fi

movie_data_file="$(realpath "$1")"

if [ ! -f "$movie_data_file" ]; then
  echo "영화 데이터 파일을 찾을 수 없습니다: $movie_data_file"
  exit 1
fi

if [ ! -r "$movie_data_file" ]; then
  echo "영화 데이터 파일을 읽을 수 없습니다: $movie_data_file"
  exit 1
fi

cd /home/ubuntu/backend

docker compose run \
  --rm \
  --no-deps \
  --volume "$movie_data_file:/import/movies.json:ro" \
  --env SPRING_PROFILES_ACTIVE=production,movie-import \
  --env SPRING_MAIN_WEB_APPLICATION_TYPE=none \
  --env MOVIE_DATA_PATH=/import/movies.json \
  backend