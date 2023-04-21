package shipping_service.kafka_handlers;

import java.util.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import order_service.models.ItemObject;
import order_service.models.ItemTable;
import order_service.models.OrderObject;
import user_service.models.CustomerObject;


public class ShippingProducer {
    private static final String OrderTopic = "order_topic";
    private static final String ShippingTopic = "shipping_topic";
    private static final String serverAddr = "localhost:9092";

    private static final String defaultGroupId = "groupB";
    private static final boolean autoCommit = false;
    private static final int autoCommitIntervalMs = 15000;

    private static final boolean waitAck = true;
    
    public void start() {
        // Initialize the list of items from the item topic at startup

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Listen forever for events on the item topic
                while (true) {
                    // Poll for new events of 5 seconds
                    listenForOrderEvents();
                }
            }
        });  
        t1.start();
    }
    public static CustomerObject getCustomer(long offset) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("earliest", "groupE"));
        consumer.assign(Arrays.asList(new TopicPartition("customer_topic", 0)));
        consumer.seek(new TopicPartition("customer_topic", 0), offset);
        // wait 5 seconds
        final ConsumerRecords<String, String> records = consumer.poll(Duration.of(1, ChronoUnit.SECONDS));
        for (final ConsumerRecord<String, String> record : records) {
            System.out.print("Consumer group: " + defaultGroupId + "\t");
            System.out.println("Partition: " + record.partition() +
                    "\tOffset: " + record.offset() +
                    "\tKey: " + record.key() +
                    "\tValue: " + record.value()
            );
            // return CustomerObject.deserialize(record.value());
            consumer.close();
            String offsetStr = String.valueOf(record.offset()); // Yes it should be equal to the input offset that is passed in the function but is better safe than sorry
            return CustomerObject.deserialize(offsetStr, record.value());  
        }
        consumer.close();
        return null;
    }


    public static void addShipping(OrderObject order) {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        order.setStatus("shipped");
        final String value = order.serialize();
        final String key = order.getOrderKey();
        //  TODO: Check if the user actually exists
        System.out.println(
                "Topic: " + ShippingTopic +
                "\tKey: " + key +
                "\tValue: " + value
        );
        CustomerObject customer = getCustomer(Long.valueOf(order.getUserOffset()));
        if (customer == null) {
            System.out.println("Customer does not exist");
            return;
        }
        final ProducerRecord<String, String> record = new ProducerRecord<>(ShippingTopic, key, value);
        // final KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        // Future<RecordMetadata> future = producer.send(record);

        // if (waitAck) {
        //     try {
        //         RecordMetadata metadata = future.get();
        //         System.out.println("Record sent to partition " + metadata.partition() + " with offset " + metadata.offset());
        //     } catch (InterruptedException | ExecutionException e) {
        //         e.printStackTrace();
        //     }
        // }
        // System.out.println("Adding shipping for order: " + order.getOrderKey());
        // producer.close();
    }

    public static void listenForOrderEvents() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("latest", "groupC"));
        TopicPartition partition0 = new TopicPartition(OrderTopic, 0);
        consumer.assign(Arrays.asList(partition0));
        // consumer.subscribe(Collections.singletonList(OrderTopic));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            System.out.println("Listening for order events");
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Received event: " + record.value());
                OrderObject order = OrderObject.deserialize(record.value());
                order.setOrderKey(record.key());
                // if (order.ge.equals("shipped")) {
                    // }
                addShipping(order);
            }
        }

    }

    private static Properties kafkaConfig(String offsetResetStrategy, String groupId) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(autoCommit));
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, String.valueOf(autoCommitIntervalMs));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetResetStrategy);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return props;
    }
}
