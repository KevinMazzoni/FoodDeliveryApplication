package user_service;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import user_service.models.AdminObject;
import user_service.models.CustomerObject;
import user_service.models.DeliveryManObject;

public class UserProducer {
    private static final String consumerTopic = "consumer_topic";
    private static final String consumerKey = "consumer_key";
    private static final String adminTopic = "admin_topic";
    private static final String adminKey = "admin_key";
    private static final String deliveryManTopic = "delivery_man_topic";
    private static final String deliveryManKey = "delivery_man_key";
    

    
    private static final int numMessages = 100000;
    private static final int waitBetweenMsgs = 500;
    private static final boolean waitAck = true;
    private static final String serverAddr = "localhost:9092";

    public static void main(String[] args) {

    }

    public static void addCostumer(CustomerObject customer) {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        final String topic = consumerTopic;
        final String key = consumerKey;
        final String value = customer.serialize();
        System.out.println(
                "Topic: " + topic +
                "\tKey: " + key +
                "\tValue: " + value
        );

        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        final KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        final Future<RecordMetadata> future = producer.send(record);

        if (waitAck) {
            try {
                RecordMetadata ack = future.get();
                System.out.println("Ack for topic " + ack.topic() + ", partition " + ack.partition() + ", offset " + ack.offset());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        }

        try {
            Thread.sleep(waitBetweenMsgs);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        producer.close();
    }

    public static void addAdmin(AdminObject admin) {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        final String topic = adminTopic;
        final String key = adminKey;
        final String value = admin.serialize();
        System.out.println(
                "Topic: " + topic +
                "\tKey: " + key +
                "\tValue: " + value
        );

        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        final KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        final Future<RecordMetadata> future = producer.send(record);

        if (waitAck) {
            try {
                RecordMetadata ack = future.get();
                System.out.println("Ack for topic " + ack.topic() + ", partition " + ack.partition() + ", offset " + ack.offset());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        }

        try {
            Thread.sleep(waitBetweenMsgs);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        producer.close();
    }

    public static void addDeliveryMan(DeliveryManObject deliveryMan) {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        final String topic = deliveryManTopic;
        final String key = deliveryManKey;
        final String value = deliveryMan.serialize();
        System.out.println(
                "Topic: " + topic +
                "\tKey: " + key +
                "\tValue: " + value
        );

        final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        final KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        final Future<RecordMetadata> future = producer.send(record);

        if (waitAck) {
            try {
                RecordMetadata ack = future.get();
                System.out.println("Ack for topic " + ack.topic() + ", partition " + ack.partition() + ", offset " + ack.offset());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        }

        try {
            Thread.sleep(waitBetweenMsgs);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        producer.close();
    }
}