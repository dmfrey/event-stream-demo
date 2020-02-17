FROM debian:buster

# Install cURL
RUN apt update

# Install Maven
RUN apt install -y maven

# Fix error: Release file for http://archive.debian.org/debian/dists/buster-backports/InRelease is expired
RUN echo 'Acquire::Check-Valid-Until no;' > /etc/apt/apt.conf.d/99no-check-valid-until

# Install OpenJDK
RUN apt install -y openjdk-11-jdk

RUN apt clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

