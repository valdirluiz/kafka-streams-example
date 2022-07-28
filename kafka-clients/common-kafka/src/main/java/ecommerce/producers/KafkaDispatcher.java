package ecommerce.producers;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, T> producer;

    public KafkaDispatcher(){
       this.producer =  new KafkaProducer<>(properties());
    }


    public void send(String topicName, String key, T value) throws ExecutionException, InterruptedException {
        var record = new ProducerRecord<>(topicName, key, value);
        producer.send(record, callback()).get();
    }

    private static Callback callback() {
        return (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else {
                System.out.printf("::: Message has been sent! Result: partition: %d /offset: %d /timestamp: %d%n",
                        data.partition(), data.offset(), data.timestamp());
            }
        };
    }

    private static Properties properties() {
        var properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    @Override
    public void close()  {
        this.producer.close();
    }
}
