# 📊 Prometheus & Grafana Monitoring Setup

## 🎯 Overview

This folder contains everything you need to monitor your e-commerce microservices using **Prometheus** (metrics collection) and **Grafana** (visualization).

---

## 🚀 How to Run

### Step 1: Start All Microservices First

Make sure your microservices are running before starting monitoring:
- Config Server (8888)
- Discovery Service (8761)
- Gateway Service (8222)
- Product Service (8050)
- Customer Service (8090)
- Order Service (8070)
- Payment Service (8060)
- Notification Service (8040)

### Step 2: Start Prometheus & Grafana

Open PowerShell/Terminal in this folder and run:

```powershell
# Navigate to monitoring folder
cd E:\study\tp-microservices\e-commerce\monitoring

# Start the monitoring stack
docker-compose up -d
```

**Expected Output:**
```
Creating network "monitoring_monitoring" with the default driver
Creating volume "monitoring_prometheus-data" with local driver
Creating volume "monitoring_grafana-data" with local driver
Creating prometheus ... done
Creating grafana    ... done
```

### Step 3: Verify Containers Are Running

```powershell
docker-compose ps
```

**Expected Output:**
```
NAME          IMAGE                        STATUS
grafana       ecommerce-grafana:latest     Up
prometheus    ecommerce-prometheus:latest  Up
```

---

## 🔗 Access the Services

### Prometheus (Metrics Collection)
- **URL**: http://localhost:9090
- **No login required**
- **Purpose**: View raw metrics and query data

### Grafana (Visualization)
- **URL**: http://localhost:3000
- **Username**: `admin`
- **Password**: `admin`
- **Purpose**: View pre-built dashboards

---

## 📊 Using Prometheus

### 1. Access Prometheus UI
Open your browser and go to: http://localhost:9090

### 2. Check Service Health
Click **Status** → **Targets** to see all monitored services.

**All services should show:**
- State: **UP** (green)
- Last Scrape: Recent timestamp

**If services show DOWN:**
- Make sure microservices are running
- Check that actuator endpoints are accessible:
  ```powershell
  curl http://localhost:8050/actuator/prometheus
  ```

### 3. Query Metrics

Click on the **Graph** tab and try these queries:

#### See Request Rate per Service
```promql
rate(http_server_requests_seconds_count{application="product-service"}[1m])
```

#### See Circuit Breaker States
```promql
resilience4j_circuitbreaker_state{name="productmicroService"}
```

#### See Memory Usage
```promql
jvm_memory_used_bytes{application="product-service"}
```

#### See Rate Limiter Status
```promql
resilience4j_ratelimiter_available_permissions{name="myRateLimiter"}
```

### 4. Explore Metrics

In the query box, start typing `http_` or `resilience4j_` and Prometheus will suggest available metrics.

---

## 📈 Using Grafana

### 1. Login to Grafana

1. Open: http://localhost:3000
2. Enter credentials:
   - Username: `admin`
   - Password: `admin`
3. (Optional) Skip password change or set a new password

### 2. Access the Dashboard

**Method 1 - From Home:**
1. Click **Dashboards** (left menu, looks like 4 squares)
2. Click **E-Commerce Microservices Dashboard**

**Method 2 - Direct Navigation:**
1. Click **Search** icon (magnifying glass)
2. Type: `e-commerce`
3. Click the dashboard

### 3. Understanding the Dashboard

The dashboard has 6 panels:

#### Panel 1: Request Rate per Service
- **Shows**: How many requests each service receives per second
- **Use**: Identify which services are busiest
- **What to look for**: Sudden spikes or drops in traffic

#### Panel 2: Circuit Breaker States
- **Shows**: Circuit breaker status for each service
- **Values**:
  - 0 = CLOSED (normal, everything working)
  - 1 = OPEN (service is failing, circuit is broken)
  - 2 = HALF_OPEN (testing if service recovered)
- **What to look for**: Any value of 1 means problems

#### Panel 3: Average Response Time
- **Shows**: How long APIs take to respond (in seconds)
- **Use**: Find slow endpoints
- **What to look for**: Times > 1 second need attention

#### Panel 4: Rate Limiter - Available Permissions
- **Shows**: How many more requests can be made before hitting rate limit
- **Default**: 5 requests per second per service
- **What to look for**: Value dropping to 0 means rate limiting is active

#### Panel 5: JVM Memory Usage
- **Shows**: Memory consumption of each service
- **Use**: Detect memory leaks or resource issues
- **What to look for**: Steadily increasing values

#### Panel 6: Retry Attempts
- **Shows**: Number of retry attempts
- **Use**: See if services are having transient failures
- **What to look for**: High retry rates indicate instability

### 4. Customize Time Range

**Top-right corner** has time range selector:
- Click the clock icon
- Choose from presets:
  - Last 5 minutes
  - Last 15 minutes
  - Last 30 minutes
  - Last 1 hour
- Or set custom range

### 5. Refresh Dashboard

**Top-right corner** has refresh button:
- Click dropdown next to time range
- Select auto-refresh interval:
  - 5s (recommended)
  - 10s
  - 30s
  - 1m

### 6. Zoom In on Data

- Click and drag on any graph to zoom into that time period
- Click **Zoom out** button to reset

---

## 🧪 Testing the Setup

### Generate Test Traffic

Run these commands to generate metrics:

```powershell
# Test product service
curl http://localhost:8050/api/v1/products

# Test customer service
curl http://localhost:8090/api/v1/customers

# Test order service
curl http://localhost:8070/api/v1/orders

# Test payment service
curl http://localhost:8060/api/v1/payments

# Test notification service
curl http://localhost:8040/api/v1/notifications
```

**Wait 30 seconds**, then refresh Grafana dashboard. You should see:
- Request rate spikes
- Response time data
- Memory usage updates

### Test Rate Limiter

Send 20 requests quickly to trigger rate limiting:

```powershell
for ($i=1; $i -le 20; $i++) {
    curl http://localhost:8050/api/v1/products
    Write-Host "Request $i sent"
}
```

**In Grafana**, watch the "Rate Limiter - Available Permissions" panel drop to 0.

---

## 🛠️ Common Commands

### View Logs
```powershell
# View all logs
docker-compose logs -f

# View only Prometheus logs
docker-compose logs -f prometheus

# View only Grafana logs
docker-compose logs -f grafana
```

### Restart Services
```powershell
# Restart both services
docker-compose restart

# Restart only Prometheus
docker-compose restart prometheus

# Restart only Grafana
docker-compose restart grafana
```

### Stop Monitoring
```powershell
# Stop but keep data
docker-compose down

# Stop and delete all data (clean slate)
docker-compose down -v
```

### Check Container Status
```powershell
docker-compose ps
```

---

## 🐛 Troubleshooting

### Problem 1: "Targets are DOWN" in Prometheus

**Symptoms**: Services show red/down in http://localhost:9090/targets

**Solutions**:

1. **Check microservices are running:**
   ```powershell
   # Try accessing actuator endpoint
   curl http://localhost:8050/actuator/prometheus
   ```
   If this fails, start the microservice.

2. **Check ports are correct:**
   - Product: 8050
   - Customer: 8090
   - Order: 8070
   - Payment: 8060
   - Notification: 8040

3. **Restart Prometheus:**
   ```powershell
   docker-compose restart prometheus
   ```

### Problem 2: "No Data" in Grafana Dashboard

**Symptoms**: All panels show "No data"

**Solutions**:

1. **Wait 1-2 minutes** for data to accumulate

2. **Generate traffic** to your services:
   ```powershell
   curl http://localhost:8050/api/v1/products
   ```

3. **Check Prometheus connection:**
   - Grafana → Configuration (gear icon) → Data Sources
   - Click "Prometheus"
   - Click "Test" button
   - Should say "Data source is working"

4. **Check time range:**
   - Make sure time range is "Last 15 minutes" or similar
   - Not a future time or very old time

5. **Verify Prometheus has data:**
   - Open http://localhost:9090
   - Run query: `http_server_requests_seconds_count`
   - Should show results

### Problem 3: Can't Login to Grafana

**Symptoms**: Login page doesn't accept admin/admin

**Solutions**:

1. **Try default credentials:**
   - Username: `admin`
   - Password: `admin`

2. **Clear browser cache:**
   - Press Ctrl+Shift+Delete
   - Clear cookies and cache
   - Try again

3. **Reset Grafana:**
   ```powershell
   docker-compose down -v
   docker-compose up -d
   ```
   Wait 30 seconds, then try logging in again.

### Problem 4: Port Already in Use

**Symptoms**: Error like "port is already allocated"

**Solutions**:

1. **Check what's using the port:**
   ```powershell
   # Check port 9090 (Prometheus)
   netstat -ano | findstr :9090
   
   # Check port 3000 (Grafana)
   netstat -ano | findstr :3000
   ```

2. **Stop the conflicting process** or **change ports** in `docker-compose.yml`:
   ```yaml
   ports:
     - "9091:9090"  # Change 9091 to any free port
   ```

### Problem 5: Dashboard Not Showing Up

**Symptoms**: Can't find "E-Commerce Microservices Dashboard"

**Solutions**:

1. **Wait 30 seconds** after starting Grafana

2. **Manually import dashboard:**
   - Grafana → Dashboards → Import
   - Click "Upload JSON file"
   - Select: `grafana/provisioning/dashboards/ecommerce-dashboard.json`
   - Click "Load"
   - Select "Prometheus" as datasource
   - Click "Import"

### Problem 6: Containers Won't Start

**Symptoms**: `docker-compose up` shows errors

**Solutions**:

1. **Check Docker is running:**
   - Open Docker Desktop
   - Make sure Docker engine is started

2. **Check logs for errors:**
   ```powershell
   docker-compose logs
   ```

3. **Clean restart:**
   ```powershell
   docker-compose down -v
   docker-compose build --no-cache
   docker-compose up -d
   ```

4. **Check disk space:**
   ```powershell
   docker system df
   ```
   If low, clean up:
   ```powershell
   docker system prune -a
   ```

---

## 📚 Useful Prometheus Queries

Copy these into Prometheus UI (http://localhost:9090):

### Overall System Metrics

```promql
# Total requests per second across all services
sum(rate(http_server_requests_seconds_count[1m]))

# Total errors per second
sum(rate(http_server_requests_seconds_count{status=~"5.."}[1m]))

# Error rate percentage
sum(rate(http_server_requests_seconds_count{status=~"5.."}[1m])) / sum(rate(http_server_requests_seconds_count[1m])) * 100
```

### Per-Service Metrics

```promql
# Product service request rate
rate(http_server_requests_seconds_count{application="product-service"}[1m])

# Product service average response time
rate(http_server_requests_seconds_sum{application="product-service"}[1m]) / rate(http_server_requests_seconds_count{application="product-service"}[1m])

# Product service memory usage
jvm_memory_used_bytes{application="product-service",area="heap"}
```

### Resilience4j Metrics

```promql
# All circuit breaker states
resilience4j_circuitbreaker_state

# Circuit breaker failure rate
resilience4j_circuitbreaker_failure_rate{name="productmicroService"}

# Rate limiter available permissions
resilience4j_ratelimiter_available_permissions{name="myRateLimiter"}

# Retry attempts
rate(resilience4j_retry_calls_total{name="myRetry"}[1m])
```

---

## 🎓 Learning Path

### Day 1: Getting Started (30 minutes)
1. ✅ Start monitoring with `docker-compose up -d`
2. ✅ Access Prometheus at http://localhost:9090
3. ✅ Check all targets are UP
4. ✅ Access Grafana at http://localhost:3000
5. ✅ View the E-Commerce dashboard
6. ✅ Generate test traffic
7. ✅ Watch metrics update in real-time

### Day 2: Understanding Metrics (1 hour)
1. Study each dashboard panel
2. Try Prometheus queries
3. Test rate limiting (send 20+ requests)
4. Observe circuit breaker behavior
5. Check memory usage patterns

### Week 1: Customization (2-3 hours)
1. Create custom Prometheus queries
2. Add new panels to Grafana
3. Set up time-series comparisons
4. Export and backup dashboards

### Ongoing: Monitoring in Practice
1. Check dashboard daily
2. Monitor for anomalies
3. Investigate slow endpoints
4. Track memory usage trends
5. Review error rates

---

## 🎯 What to Monitor Daily

### ✅ Health Checks
- [ ] All Prometheus targets are UP
- [ ] No circuit breakers in OPEN state
- [ ] Memory usage is stable
- [ ] No services hitting rate limits

### ⚠️ Warning Signs
- **Response time > 1 second**: Investigate slow endpoints
- **Circuit breaker OPEN**: Service is failing
- **Rate limiter at 0**: Too many requests
- **Memory increasing**: Possible memory leak
- **Error rate > 1%**: Check application logs

### 🚨 Critical Issues
- **Prometheus targets DOWN**: Service not responding
- **Circuit breaker stays OPEN**: Persistent failures
- **Memory > 80%**: Service may crash
- **Error rate > 5%**: Major issues

---

## 💡 Tips & Best Practices

1. **Keep Grafana open** in a browser tab for continuous monitoring
2. **Set auto-refresh** to 5-10 seconds for real-time data
3. **Bookmark useful queries** in Prometheus
4. **Take screenshots** of normal behavior for comparison
5. **Document anomalies** when you see them
6. **Test during development** not just in production
7. **Check metrics before and after deployments**

---

## 📖 Key Concepts

### Prometheus
- **Scraping**: Prometheus pulls metrics from services every 15 seconds
- **Time-series**: All data is stored with timestamps
- **PromQL**: Query language for analyzing metrics
- **Targets**: Services being monitored

### Grafana
- **Datasource**: Where Grafana gets data (Prometheus)
- **Dashboard**: Collection of panels
- **Panel**: Individual graph/chart
- **Time range**: Period of data shown

### Metrics
- **Counter**: Always increasing (e.g., total requests)
- **Gauge**: Can go up or down (e.g., memory usage)
- **Histogram**: Distribution of values (e.g., response times)

---

## 🔗 Quick Links

| What | URL |
|------|-----|
| Grafana Dashboard | http://localhost:3000 |
| Prometheus UI | http://localhost:9090 |
| Prometheus Targets | http://localhost:9090/targets |
| Product Service Metrics | http://localhost:8050/actuator/prometheus |
| Customer Service Metrics | http://localhost:8090/actuator/prometheus |
| Order Service Metrics | http://localhost:8070/actuator/prometheus |
| Payment Service Metrics | http://localhost:8060/actuator/prometheus |
| Notification Service Metrics | http://localhost:8040/actuator/prometheus |

---

## 🎉 Success!

You're now monitoring your microservices! 

**Next steps:**
1. Keep the monitoring stack running
2. Check the dashboard regularly
3. Learn to recognize normal vs. abnormal patterns
4. Use metrics to optimize your services

**Happy Monitoring! 📊📈**

---

**Need more help?** Check the other documentation files:
- `QUICKSTART.md` - Fast 3-step setup
- `SETUP_GUIDE.md` - Detailed configuration guide
- `SUMMARY.md` - Overview and features

**Questions?** Common issues are covered in the Troubleshooting section above.

