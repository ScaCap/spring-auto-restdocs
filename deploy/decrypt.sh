#!/usr/bin/env bash

openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in "$DEPLOY_DIR/pubring.gpg.enc" -out "$DEPLOY_DIR/pubring.gpg" -d
openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in "$DEPLOY_DIR/secring.gpg.enc" -out "$DEPLOY_DIR/secring.gpg" -d
