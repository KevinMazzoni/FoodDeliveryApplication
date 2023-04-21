package order_service.kafka_handlers;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import order_service.models.ItemObject;
import order_service.models.ItemTable;
import order_service.models.OrderObject;

public class OrderProducer {
    private static final String ItemTopic = "item_topic";
    private static final String OrderTopic = "order_topic";
    private static final String ShippingTopic = "shipping_topic";

    private static final Hashtable<String, Integer> topicPartitions = ItemTable.instance();

    private static final int waitBetweenMsgs = 500;
    private static final boolean waitAck = true;
    private static final String serverAddr = "localhost:9092";

    /**
     * Add an item to the item topic
     * @param item the object representing the item to add
     */
    public static void addItem(ItemObject item) {
        final Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);

        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        final String topic = ItemTopic;
        final String key = item.getName();
        final String value = item.serialize();
        System.out.println(
                "Topic: " + topic +
                "\tKey: " + key +
                "\tValue: " + value
        );

        AdminClient adminClient = AdminClient.create(props);
        Map<String, NewPartitions> newPartitions = new HashMap<>();
        newPartitions.put(ItemTopic, NewPartitions.increaseTo(10));
        
        try {
            adminClient.createPartitions(newPartitions).all().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Topic already has 10 partitions");
        }

        // Check if the partition for the item exists
        if (topicPartitions.get(item.getName()) != null) {
            final KafkaProducer<String, String> producer = new KafkaProducer<>(props);
            final ProducerRecord<String, String> record = new ProducerRecord<>(topic, topicPartitions.get(item.getName()), key, value);
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
        } else {
            System.out.println("Item not found");
        }

    }

    public static void addOrder(OrderObject order) {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // Assign a random key that is only 8 characters long
        final String key = UUID.randomUUID().toString().substring(0, 8)+":"+order.getUserOffset();
        final String value = order.serialize();
        System.out.println(
                "Topic: " + OrderTopic +
                "\tKey: " + key +
                "\tValue: " + value
        );
        final KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        final ProducerRecord<String, String> record = new ProducerRecord<>(OrderTopic, key, value);
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

        System.out.println("Order Produced");
        producer.close();
    }

}