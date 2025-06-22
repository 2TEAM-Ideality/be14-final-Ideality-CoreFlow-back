#!/bin/bash

# Amazon Linux 2용 Redis 설치
sudo amazon-linux-extras enable redis6
sudo yum clean metadata
sudo yum install -y redis

# Redis 서비스 시작
sudo systemctl enable redis
sudo systemctl start redis
