#!/usr/bin/env bash

set -Eeuo pipefail

exec 9>/tmp/martin-deploy.lock
flock -w 900 9

echo "[1/5] 백엔드 코드 갱신"
sudo -u ubuntu git -C /home/ubuntu/backend pull --ff-only origin main

echo "[2/5] 프론트엔드 코드 갱신"
sudo -u ubuntu git -C /home/ubuntu/frontend pull --ff-only origin main

echo "[3/5] Docker Compose 검증"
cd /home/ubuntu/backend
docker compose config --quiet

echo "[4/5] FE, BE, Nginx 통합 배포"
if ! docker compose up \
    -d \
    --build \
    --remove-orphans \
    --wait \
    --wait-timeout 300
then
    docker compose ps
    docker compose logs --tail=200
    exit 1
fi

echo "[5/5] 배포 결과 확인"
docker compose ps
curl -fsS http://127.0.0.1/ >/dev/null
curl -fsS "http://127.0.0.1/api/movies?page=1" >/dev/null

echo "배포가 완료되었습니다."
