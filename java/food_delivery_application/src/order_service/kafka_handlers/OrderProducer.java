package order_service.kafka_handlers;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import kafka.admin.TopicCommand.TopicDescription;
import order_service.models.ItemObject;
import order_service.models.ItemTable;

public class OrderProducer {
    private static final String ItemTopic = "item_topic";
    // private static final String consumerKey = "consumer_key";
    private static final Hashtable<String, Integer> topicPartitions = ItemTable.instance();


    private static final int numMessages = 100000;
    private static final int waitBetweenMsgs = 500;
    private static final boolean waitAck = true;
    private static final String serverAddr = "localhost:9092";
    private static int partition = 1;

    public static void main(String[] args) {

    }

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
            System.out.println("hey!");

        } catch (InterruptedException e) {
            System.out.println("nope!");

            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Topic already has 10 partitions");
            
        }
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

}