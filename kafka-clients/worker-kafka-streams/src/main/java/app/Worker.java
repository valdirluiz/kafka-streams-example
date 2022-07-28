package app;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Worker {
    
    public static void main(String[] args) {
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        final Properties streamsConfiguration = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "application-reset-demo");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "application-reset-demo-client");
        // Where to find Kafka broker(s).
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // Read the topic from the very beginning if no previous consumer offsets are found for this app.
        // Resetting an app will set any existing consumer offsets to zero,
        // so setting this config combined with resetting will cause the application to re-process all the input data in the topic.
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    
        final KafkaStreams streams = buildKafkaJoinStreams(streamsConfiguration);

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        startKafkaStreamsSynchronously(streams);
    }

    static KafkaStreams buildKafkaJoinStreams(final Properties streamsConfiguration) {
      
        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> topic1 = builder.stream("streams.topic1");
        KStream<String, String> topic2 = builder.stream("streams.topic2");

        KStream<String, String> joined =
             topic1.join(topic2, (leftValue, rightValue) -> rightValue, JoinWindows.of(Duration.ofMinutes(5)));

        joined.to("streams.all");
        return new KafkaStreams(builder.build(), streamsConfiguration);
      }

    static KafkaStreams buildKafkaMergeStreams(final Properties streamsConfiguration) {
      
        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> topic1 = builder.stream("streams.topic1");
        KStream<String, String> topic2 = builder.stream("streams.topic2");
        KStream<String, String> allPayloads = topic1.merge(topic2);

        allPayloads.to("streams.all");
    
        return new KafkaStreams(builder.build(), streamsConfiguration);
      }

    static void startKafkaStreamsSynchronously(final KafkaStreams streams) {
        final CountDownLatch latch = new CountDownLatch(1);
        streams.setStateListener((newState, oldState) -> {
          if (oldState == KafkaStreams.State.REBALANCING && newState == KafkaStreams.State.RUNNING) {
            latch.countDown();
          }
        });
    
        streams.start();
    
        try {
          latch.await();
        } catch (final InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

}
