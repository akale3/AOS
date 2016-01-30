# Foe Amazon EC2 instance Setup.

# Create one t3.micro instance.
# Setup all databases on that instance and then create its image.
# Then launch 16 instances of m3.medium using that image.

# installation guide.
# Connect to an amazon instance.
# Send all system evaluation code and installation script to that instance using following command.

chmod 400 aditya.pem
ssh-add aditya.pem
scp -i user.pem -r aos_project/ ubuntu@52.25.172.43:./home/ubuntu/

# Now go to that instance and run installation script.

./Installation.sh

# Modify bind ip in config files.

# For couchdb
cd /etc/couchdb
sudo vi default.ini
# change --> bindIP = 0.0.0.0

# Restart couchdb server
sudo service couchdb stop
sudo service couchdb start

# For Mongodb
cd /etc/
sudo vi mongodb.config
# change bindIP --> # bindIp (Comment that line)

# Restart Mongodb Server
sudo service redis-server restart

# start Redisdb
sudo service mongodb stop
sudo service mongodb start

# All server will be installed and up on that system.
# Now create 16 instances. Note down there private and public ips.
# modify config.properties file of each evaluation code to have 16 client private ips. (./resources/config.properties)
# Update all public ips in ips.txt file.
# Send modified config files/ complete project to all 16 instances using parallel-scp command.

./sendCode.sh

# Now to perform evaluation on 16 instances and to note there time.
# I have used cluster-ssh which allows to give command to different 16 instances parallely.

# Setup for CSSH
# Install openSSH on your Remote systems

sudo apt-get install openssh-server

# Install cluster SSH

sudo apt-get install clusterssh
  
# Configure host addresses to cluster ssh.

sudo nano /etc/clusters

# Write cluster name and add entries for all public Ips in that cluster and save the file.

clusters = testcluster
testcluster = ubuntu@52.34.72.18 ubuntu@52.25.172.43 ubuntu@52.34.72.114 ubuntu@52.33.255.103 ubuntu@52.34.74.53 ubuntu@52.34.71.221 ubuntu@52.34.59.28 ubuntu@52.34.80.114 ubuntu@52.34.35.186 ubuntu@52.27.40.194 ubuntu@52.34.68.219 ubuntu@52.33.44.234 ubuntu@52.33.242.38 ubuntu@52.34.66.35 ubuntu@52.34.69.5 ubuntu@52.34.8.233

# Now run following command from console and all 16 instances terminal will be opened for parallel execution.

cssh -l ubuntu -o "-i user.pem" testcluster

# Go to different projects compile them using ant and run. 

ant
./runClient.sh