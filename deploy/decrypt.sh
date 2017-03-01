#!/usr/bin/env bash

openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in deploy/pubring.gpg.enc -out deploy/pubring.gpg -d
openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in deploy/secring.gpg.enc -out deploy/secring.gpg -d
