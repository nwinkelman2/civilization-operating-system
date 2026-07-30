#!/bin/bash
# Register the Debezium PostgreSQL connector
# Usage: ./debezium/setup-connector.sh [debezium-host]

HOST=${1:-localhost}
CONNECTOR_URL="http://$HOST:8083/connectors"

echo "Registering Debezium PostgreSQL connector at $CONNECTOR_URL..."

curl -X POST "$CONNECTOR_URL" \
  -H "Content-Type: application/json" \
  -d @debezium/connector.json

echo ""
echo "Connector status:"
curl -s "$CONNECTOR_URL/civos-postgres-connector/status" | python3 -m json.tool
