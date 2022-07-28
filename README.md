# kafka-examples

In this project, I demonstrate the basic use of kafka via command line and java application

## Startup

```
cd docker
docker-compose up
```

## Services and ports

- Kafka - 9092
- Kafdrop - 19000
- Zookeeper - 2181

## How to use with cli

Download de last binary version of kafka from https://kafka.apache.org/downloads

Unzip the file and execute the commands:

Create a new topic

```
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 \
 --replication-factor 1 --partitions 1 --topic ecommerce_new_order
```

List all topics:

```
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

Producing messages

```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic ecommerce_new_order
```

Consuming messages
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \ 
--topic ecommerce_new_order
```

Consuming messages from beginning

```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
 --topic ecommerce_new_order --from-beginning
```

Change partitions number of a topic:

```
bin/kafka-topics.sh --alter --bootstrap-server localhost:9092 \
 --topic ecommerce_new_order --partitions 3
```



ref

https://mydeveloperplanet.com/2019/10/30/kafka-streams-joins-explored/