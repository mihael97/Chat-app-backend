#!/usr/bin/env bash
sudo systemctl start docker
cd support || exit 1
docker-compose up -d --remove-orphans
cd ..