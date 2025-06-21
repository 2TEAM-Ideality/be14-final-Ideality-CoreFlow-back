#!/bin/bash

# 설치 (Amazon Linux 2 기준)
yum install -y redis

# Redis 시작 및 자동 실행 등록
systemctl enable redis
systemctl start redis
