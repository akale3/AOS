#!/bin/sh

chmod 400 aditya.pem
ssh-add aditya.pem

# connect to all servers
parallel-ssh -o uptime.out -h ips.txt -O StrictHostKeyChecking=no echo hi
# Copy project to remote server
parallel-scp -h ips.txt -r aos_project /home/ubuntu/

