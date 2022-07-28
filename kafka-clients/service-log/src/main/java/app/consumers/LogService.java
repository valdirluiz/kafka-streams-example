package app.consumers;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import ecommerce.consumers.KafkaService;

import java.util.Map;


public class LogService {

    public static void main(String[] args) {
        var logService = new LogService();
        try(var kafkaService =
                    new KafkaService<>(LogService.class.getName(),
                            "streams.all",
                            logService::consume, String.class,
                            Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                                    StringDeserializer.class.getName()));) {
            kafkaService.run();
        }
    }

    private void consume(ConsumerRecord<String, String> record) {
        System.out.println("---------------------------------------------");
        System.out.println("LOG: " + record.topic());
        System.out.println("Key: " + record.key());
        System.out.println("Payload: " + record.value());
        System.out.println("Partition: " + record.partition());
        System.out.println("Offset: " + record.offset());
        System.out.println("---------------------------------------------");
    }







}
