# Build Prometheus image
Write-Host "Building Prometheus image..." -ForegroundColor Green
Set-Location prometheus
docker build -t ecommerce-prometheus:latest .
Set-Location ..

# Build Grafana image
Write-Host "Building Grafana image..." -ForegroundColor Green
Set-Location grafana
docker build -t ecommerce-grafana:latest .
Set-Location ..

Write-Host "Images built successfully!" -ForegroundColor Green
Write-Host "Run 'docker-compose up -d' to start the monitoring stack." -ForegroundColor Yellow

