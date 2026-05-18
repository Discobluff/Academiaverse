#!/bin/bash
set -e

echo "===== LOCAL CI PIPELINE ====="

# Stage 1: BUILD
echo "=== STAGE 1: BUILD ==="
cd src/backend
chmod +x gradlew
./gradlew build -x test -x check
cd ../..

cd src/frontend
npm ci
npx ng build
cd ../..
echo "✅ BUILD stage passed"

# Stage 2: STATIC ANALYSIS
echo "=== STAGE 2: STATIC ANALYSIS ==="
cd src/backend
./gradlew check -x test
cd ../..

cd src/frontend
npx ng lint
cd ../..
echo "✅ STATIC ANALYSIS stage passed"

# Stage 3: TEST
echo "=== STAGE 3: TEST ==="
if ! docker ps | grep -q mysql-db; then
    docker-compose up -d
    sleep 10
fi

cd src/backend
./gradlew test jacocoTestReport
cd ../..

cd src/frontend
npx ng test --watch=false
cd ../..
echo "✅ TEST stage passed"

# Stage 4: DEPLOY
echo "=== STAGE 4: DEPLOY ==="
cd src/backend
./gradlew bootJar
echo "JAR at build/libs/$(ls build/libs/*.jar)"
cd ../..

cd src/frontend
npx ng build --configuration production
echo "Frontend build at dist/"
cd ../..
echo "✅ DEPLOY stage passed"

echo "===== ALL STAGES PASSED ✅ ====="