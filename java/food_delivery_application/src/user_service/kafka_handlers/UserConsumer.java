package user_service.kafka_handlers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import user_service.models.AdminObject;
import user_service.models.CustomerObject;
import user_service.models.DeliveryManObject;

public class UserConsumer {
    private static final String defaultGroupId = "groupH";
    private static final String customerTopic = "customer_topic";
    private static final String adminTopic = "admin_topic";
    private static final String deliveryManTopic = "delivery_man_topic";
    private static final String serverAddr = "localhost:9092";
    private static final boolean autoCommit = false;
    private static final int autoCommitIntervalMs = 15000;

    // Default is "latest": try "earliest" instead
    private static final String offsetResetStrategy = "earliest";
    public static void main(String[] args) {

    }

    public static CustomerObject getCustomer(long offset) {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("bnmjytrdfg"));
        consumer.subscribe(Collections.singletonList(customerTopic), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println("Partitions revoked: " + partitions);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println("Partitions assigned: " + partitions);
                consumer.seek(new TopicPartition(customerTopic, 0), offset);

            }
        });
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

    public static List<CustomerObject> getCustomers() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("bytfrws"));
        consumer.subscribe(Collections.singletonList(customerTopic), new ConsumerRebalanceListener() {
            // TODO: This can be simplified by using the consumer.assign method
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                System.out.println("Partitions revoked: " + partitions);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                System.out.println("Partitions assigned: " + partitions);
                consumer.seek(new TopicPartition(customerTopic, 0), 0);

            }
        });
        // wait 5 seconds
        final ConsumerRecords<String, String> records = consumer.poll(Duration.of(1000, ChronoUnit.MILLIS));

        // create new list of customers
        List<CustomerObject> customers = new ArrayList<CustomerObject>();
        for (final ConsumerRecord<String, String> record : records) {
            System.out.print("Consumer group: " + defaultGroupId + "\t");
            System.out.println("Partition: " + record.partition() +
                    "\tOffset: " + record.offset() +
                    "\tKey: " + record.key() +
                    "\tValue: " + record.value()
            );
            
            try {
                String offset = String.valueOf(record.offset());
                CustomerObject customer = CustomerObject.deserialize(offset, record.value());
                customers.add(customer);
            } catch (Exception e) {
                consumer.close();

                System.out.println("Error: " + e);
                return customers;
            }
        }
        consumer.close();
        return customers;
    }

    public static List<AdminObject> getAdmins() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("njuvfxd"));
        consumer.subscribe(Collections.singletonList(adminTopic));
        final ConsumerRecords<String, String> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));

        // create new list of customers
        List<AdminObject> admins = new ArrayList<AdminObject>();
        for (final ConsumerRecord<String, String> record : records) {
            System.out.print("Consumer group: " + defaultGroupId + "\t");
            System.out.println("Partition: " + record.partition() +
                    "\tOffset: " + record.offset() +
                    "\tKey: " + record.key() +
                    "\tValue: " + record.value()
            );
            
            try {
                String offset = String.valueOf(record.offset());
                AdminObject admin = AdminObject.deserialize(offset, record.value());
                admins.add(admin);
            } catch (Exception e) {
                consumer.close();

                System.out.println("Error: " + e);
                return admins;
            }
        }
        consumer.close();
        return admins;
    }

    public static List<DeliveryManObject> getDeliveryMen() {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("vdsagsf"));
        consumer.subscribe(Collections.singletonList(deliveryManTopic));
        final ConsumerRecords<String, String> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));

        // create new list of customers
        List<DeliveryManObject> deliveryMen = new ArrayList<DeliveryManObject>();
        for (final ConsumerRecord<String, String> record : records) {
            System.out.print("Consumer group: " + defaultGroupId + "\t");
            System.out.println("Partition: " + record.partition() +
                    "\tOffset: " + record.offset() +
                    "\tKey: " + record.key() +
                    "\tValue: " + record.value()
            );
            
            try {
                String offset = String.valueOf(record.offset());
                DeliveryManObject deliveryMan = DeliveryManObject.deserialize(offset, record.value());
                deliveryMen.add(deliveryMan);
            } catch (Exception e) {
                consumer.close();

                System.out.println("Error: " + e);
                return deliveryMen;
            }
        }
        consumer.close();
        return deliveryMen;
    }

    private static Properties kafkaConfig(String groupId) {
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