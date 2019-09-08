kafka-topics --zookeeper localhost:2181 --delete --topic cloud.event-stream-demo-workorder-events-by-id-changelog
kafka-topics --zookeeper localhost:2181 --delete --topic cloud.event-stream-demo-workorder-events-by-id-repartition
kafka-topics --zookeeper localhost:2181 --delete --topic cloud.workorder-events

kafka-topics --zookeeper localhost:2181 --delete --topic local.event-stream-demo-workorder-events-by-id-changelog
kafka-topics --zookeeper localhost:2181 --delete --topic local.event-stream-demo-workorder-events-by-id-repartition
kafka-topics --zookeeper localhost:2181 --delete --topic local.workorder-events

kafka-topics --zookeeper localhost:2181 --delete --topic node-17.event-stream-demo-workorder-events-by-id-changelog
kafka-topics --zookeeper localhost:2181 --delete --topic node-17.event-stream-demo-workorder-events-by-id-repartition
kafka-topics --zookeeper localhost:2181 --delete --topic node-17.workorder-events

kafka-topics --zookeeper localhost:2181 --delete --topic node-34.event-stream-demo-workorder-events-by-id-changelog
kafka-topics --zookeeper localhost:2181 --delete --topic node-34.event-stream-demo-workorder-events-by-id-repartition
kafka-topics --zookeeper localhost:2181 --delete --topic node-34.workorder-events

kafka-topics --zookeeper localhost:2181 --delete --topic cloud.distribution-local
kafka-topics --zookeeper localhost:2181 --delete --topic cloud.distribution-node-17
kafka-topics --zookeeper localhost:2181 --delete --topic cloud.distribution-node-34

kafka-topics --zookeeper localhost:2181 --delete --topic local.distribution-cloud
kafka-topics --zookeeper localhost:2181 --delete --topic node-17.distribution-cloud
kafka-topics --zookeeper localhost:2181 --delete --topic node-34.distribution-cloud
