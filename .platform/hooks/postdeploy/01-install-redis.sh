#!/bin/bash

# Redis 설치 - Amazon Linux 2 기준 최신 방식
sudo yum install -y gcc jemalloc-devel tcl

cd /tmp
curl -O http://download.redis.io/redis-stable.tar.gz
tar xvzf redis-stable.tar.gz
cd redis-stable
make
sudo make install

# systemd에 redis 등록
sudo mkdir /etc/redis
sudo cp redis.conf /etc/redis

# Background 실행 설정
sudo sed -i 's/^supervised no/supervised systemd/' /etc/redis/redis.conf
sudo sed -i 's/^dir .\//dir \/var\/lib\/redis/' /etc/redis/redis.conf

# Redis 서비스 등록
sudo bash -c 'cat <<EOF > /etc/systemd/system/redis.service
[Unit]
Description=Redis In-Memory Data Store
After=network.target

[Service]
User=root
Group=root
ExecStart=/usr/local/bin/redis-server /etc/redis/redis.conf
ExecStop=/usr/local/bin/redis-cli shutdown
Restart=always

[Install]
WantedBy=multi-user.target
EOF'

# 서비스 활성화 및 실행
sudo systemctl daemon-reexec
sudo systemctl enable redis
sudo systemctl start redis
