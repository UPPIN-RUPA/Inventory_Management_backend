#!/bin/sh
set -eu

BASE_URL="${BASE_URL:-http://127.0.0.1:8080}"
USERNAME="${APP_SECURITY_USERNAME:-admin}"
PASSWORD="${APP_SECURITY_PASSWORD:-admin123}"

echo "Smoke testing ${BASE_URL}"

health_response="$(curl -fsS "${BASE_URL}/api/health")"
case "$health_response" in
  *'"status":"ok"'*|*'"status" : "ok"'*)
    echo "health: ok"
    ;;
  *)
    echo "health: unexpected response"
    echo "$health_response"
    exit 1
    ;;
esac

products_response="$(curl -fsS "${BASE_URL}/api/products")"
case "$products_response" in
  *'"items"'*)
    echo "products: ok"
    ;;
  *)
    echo "products: unexpected response"
    echo "$products_response"
    exit 1
    ;;
esac

inventories_response="$(curl -fsS "${BASE_URL}/api/inventories")"
case "$inventories_response" in
  *'"items"'*)
    echo "inventories: ok"
    ;;
  *)
    echo "inventories: unexpected response"
    echo "$inventories_response"
    exit 1
    ;;
esac

sales_response="$(curl -fsS "${BASE_URL}/api/sales")"
case "$sales_response" in
  *'"items"'*)
    echo "sales: ok"
    ;;
  *)
    echo "sales: unexpected response"
    echo "$sales_response"
    exit 1
    ;;
esac

create_status="$(curl -s -o /dev/null -w "%{http_code}" \
  -u "${USERNAME}:${PASSWORD}" \
  -H "Content-Type: application/json" \
  -d '{"name":"Smoke Product","category":"Testing","price":42.0}' \
  "${BASE_URL}/api/products")"

case "$create_status" in
  200|201)
    echo "authenticated create: ok (${create_status})"
    ;;
  *)
    echo "authenticated create: failed (${create_status})"
    exit 1
    ;;
esac

echo "smoke test passed"
