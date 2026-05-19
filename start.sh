#!/bin/bash

BACKEND_PID=""
FRONTEND_EN_PID=""
FRONTEND_AR_PID=""

cleanup() {
    echo ""
    echo "Stopping all services..."
    if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
        # Windows Git Bash – use taskkill
        [ -n "$BACKEND_PID" ] && taskkill //PID "$BACKEND_PID" //F 2>/dev/null
        [ -n "$FRONTEND_EN_PID" ] && taskkill //PID "$FRONTEND_EN_PID" //F 2>/dev/null
        [ -n "$FRONTEND_AR_PID" ] && taskkill //PID "$FRONTEND_AR_PID" //F 2>/dev/null
        taskkill //IM "java.exe" //F 2>/dev/null
        taskkill //IM "node.exe" //F 2>/dev/null
    else
        [ -n "$BACKEND_PID" ] && kill "$BACKEND_PID" 2>/dev/null
        [ -n "$FRONTEND_EN_PID" ] && kill "$FRONTEND_EN_PID" 2>/dev/null
        [ -n "$FRONTEND_AR_PID" ] && kill "$FRONTEND_AR_PID" 2>/dev/null
        pkill -P $$ 2>/dev/null
    fi
    wait 2>/dev/null
    echo "All services stopped."
    exit 0
}

trap cleanup SIGINT SIGTERM

echo "Starting MySQL..."
docker-compose up -d
sleep 3

echo "Starting backend..."
(
    cd src/backend || exit
    ./gradlew bootRun
) &
BACKEND_PID=$!

echo "Starting frontend (English) on http://localhost:4200 ..."
(
    cd src/frontend || exit
    npx ng serve --configuration=en --port=4200
) &
FRONTEND_EN_PID=$!

echo "Starting frontend (Arabic) on http://localhost:4201 ..."
(
    cd src/frontend || exit
    npx ng serve --configuration=ar --port=4201
) &
FRONTEND_AR_PID=$!

echo ""
echo "All services started."
echo "  English : http://localhost:4200"
echo "  Arabic  : http://localhost:4201"
echo "  Press Ctrl+C to stop all services."
echo ""

# Wait for any background job to exit (or for Ctrl+C)
wait