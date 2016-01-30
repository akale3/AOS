#!/bin/sh

clear
#Java Installation
sudo apt-get update
sudo apt-add-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
# Ant installation
sudo apt-get install ant
sudo apt-get update
# Couch DB
sudo apt-get install couchdb
# Mongo DB
sudo apt-get update
sudo apt-get install -y mongodb-org-server mongodb-org-shell mongodb-org-tools
sudo rm /var/lib/dpkg/lock
sudo apt-get install mongodb
sudo service mongod start
sudo apt-get install mongodb-clients
# Redis DB
sudo apt-get update
sudo apt-get install -y python-software-properties
sudo add-apt-repository -y ppa:rwky/redis
sudo apt-get update
sudo apt-get install -y redis-server
sudo service redis-server restart
#Riak DB
curl http://apt.basho.com/gpg/basho.apt.key | sudo apt-key add -
sudo bash -c "echo deb http://apt.basho.com $(lsb_release -sc) main > /etc/apt/sources.list.d/basho.list"
sudo apt-get update
sudo apt-get install libpam0g-dev
sudo apt-get install libssl0.9.8
wget http://s3.amazonaws.com/downloads.basho.com/riak/2.1/2.1.1/ubuntu/lucid/riak_2.1.1-1_amd64.deb
sudo dpkg -i riak_2.1.1-1_amd64.deb
sudo apt-get install riak
clear
