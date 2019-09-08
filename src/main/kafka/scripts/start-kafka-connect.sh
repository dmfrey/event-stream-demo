connect-standalone \
    $(pwd)/src/main/kafka/connect-standalone.properties \
    $(pwd)/src/main/kafka/nodes/local/connect-local-file-sink.properties \
    $(pwd)/src/main/kafka/nodes/node-17/connect-node17-file-sink.properties \
    $(pwd)/src/main/kafka/nodes/node-34/connect-node34-file-sink.properties \
    $(pwd)/src/main/kafka/cloud/connect-local-file-source.properties \
    $(pwd)/src/main/kafka/cloud/connect-node17-file-source.properties \
    $(pwd)/src/main/kafka/cloud/connect-node34-file-source.properties \
    $(pwd)/src/main/kafka/cloud/connect-distribution-local-file-sink.properties \
    $(pwd)/src/main/kafka/cloud/connect-distribution-node17-file-sink.properties \
    $(pwd)/src/main/kafka/cloud/connect-distribution-node34-file-sink.properties \
    $(pwd)/src/main/kafka/nodes/local/connect-distribution-local-file-source.properties \
    $(pwd)/src/main/kafka/nodes/node-17/connect-distribution-node17-file-source.properties \
    $(pwd)/src/main/kafka/nodes/node-34/connect-distribution-node34-file-source.properties
