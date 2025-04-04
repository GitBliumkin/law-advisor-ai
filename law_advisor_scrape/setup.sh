#!/bin/bash

echo "Installing Python dependencies..."
pip install -r requirements.txt

echo "Installing Playwright browsers..."
python -m playwright install

echo "Setup complete. You're ready to run the crawler!"
