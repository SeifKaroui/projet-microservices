#!/bin/bash
BASE_DIR="/home/ahmad/Ahmad/Ahmad-Tayh/IGL/SEM5/micro/projet-microservices"

start_service() {
    local name=$1
    local port=$2con
    echo "Starting $name on port $port..."
    nohup $BASE_DIR/$name/mvnw -f $BASE_DIR/$name/pom.xml spring-boot:run > $BASE_DIR/$name.log 2>&1 &
}

start_service "fig-server" 8888
sleep 20
start_service "discovery" 8761
sleep 20
start_service "customer" 8090
sleep 20
start_service "product" 8050
sleep 20
start_service "order" 8070
sleep 20
start_service "payment" 8060
sleep 20
start_service "notification" 8040
sleep 20
start_service "gateway" 8222
echo "All services started in background."
