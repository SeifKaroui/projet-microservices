FROM grafana/grafana:latest

# Copy provisioning files
COPY provisioning /etc/grafana/provisioning

# Expose Grafana port
EXPOSE 3000

# Set environment variables
ENV GF_SECURITY_ADMIN_USER=admin
ENV GF_SECURITY_ADMIN_PASSWORD=admin
ENV GF_USERS_ALLOW_SIGN_UP=false

# Run Grafana
CMD ["/run.sh"]

