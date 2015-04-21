#!/bin/sh

# create a local directory to store a new pair of RSA keys, change directory into that
mkdir -p local

# create a RSA key pair
# Generate a new key pair with 4096 bits
openssl genrsa -out local/local_private_key.pem 4096

# extract the public key:
openssl rsa -pubout -in local/local_private_key.pem -out local/local_public_key.pem

# For use in java/scala, convert PEM encoded keys to java-understandable DER (pkcs8) format:
openssl pkcs8 -topk8 -inform PEM -outform DER -in local/local_private_key.pem  -nocrypt > local/local_private_key.der
openssl rsa -in local/local_private_key.pem -pubout -outform DER -out local/local_public_key.der

mv local/local_public_key.der public/keys/local_public_key.der
mv local/local_public_key.pem public/keys/local_public_key.pem

# copy the default application-example to application.conf
cp conf/application-example.conf conf/application.conf
echo "Initialised conf/application.conf"
echo "Start the app with either of:"
echo "\t sbt run"
echo "\t ./activator run   # if you don't have sbt installed"