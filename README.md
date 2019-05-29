# camel-kafka-authz

## Requirements

- [Apache Maven 3.x](http://maven.apache.org)
- [OpenShift 3.11+](https://access.redhat.com/products/red-hat-openshift-container-platform)
- [AMQ Streams 1.x](https://access.redhat.com/products/red-hat-amq)

## Preparing

Install the AMQ Streams "Cluster Operator" according to the "Getting Started" documentation. 

__Notes:__
- Assume all steps are run as a user with "clusteradmin" privileges for simplicity. 
- The provided configs assume that the broker will be installed in an OpenShift namespace named "strimzi", and that the Fuse/Spring Boot apps will be deployed in an OpenShift namespace named "fuse".

Create a Kafka broker cluster, topic, and users

```
cd $PROJECT_ROOT
oc apply -f ./kube/strimzi/
```

Create the "fuse" project and the "secret sync" CronJobs

```
cd $PROJECT_ROOT
oc new-project fuse
oc apply -f ./kube/fuse/
```

Build the project source code and deploy to OpenShift

```
cd $PROJECT_ROOT
mvn clean install
oc project fuse
cd $PROJECT_ROOT/camel-kafka-scram
mvn -P openshift fabric8:deploy
cd $PROJECT_ROOT/camel-kafka-tls
mvn -P openshift fabric8:deploy
```
