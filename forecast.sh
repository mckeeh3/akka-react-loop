#!/bin/bash

# Weather Forecaster API Script
# Usage: ./forecast.sh <user> <prompt>
# Example: ./forecast.sh alice "What should I wear today?"

# Check if correct number of arguments provided
if [ $# -ne 2 ]; then
    echo "Usage: $0 <user> <prompt>"
    echo "Example: $0 alice \"What should I wear today?\""
    exit 1
fi

USER=$1
PROMPT=$2

# Execute curl command to the weather forecaster endpoint
curl -X POST "http://localhost:9000/forecast" \
    --header "Content-Type: application/json" \
    --data "{
        \"user\": \"$USER\",
        \"prompt\": \"$PROMPT\"
    }"

echo "" 