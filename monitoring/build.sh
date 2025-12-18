# Build Prometheus image
echo "Building Prometheus image..."
cd prometheus
docker build -t ecommerce-prometheus:latest .
cd ..

# Build Grafana image
echo "Building Grafana image..."
cd grafana
docker build -t ecommerce-grafana:latest .
cd ..

echo "Images built successfully!"
echo "Run 'docker-compose up -d' to start the monitoring stack."

