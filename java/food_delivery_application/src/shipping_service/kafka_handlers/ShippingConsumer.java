package shipping_service.kafka_handlers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Hashtable;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import order_service.models.ItemTable;
import order_service.models.OrderObject;
import order_service.models.ItemObject;

public class ShippingConsumer {
    private static final String serverAddr = "localhost:9092";
    private static final String shippingTopic = "shipping_topic";
    private static final boolean autoCommit = false;
    private static final int autoCommitIntervalMs = 15000;

    private static final Hashtable<String, Integer> topicPartitions = ItemTable.instance();
    private static List<ItemObject> items = new ArrayList<ItemObject>();
    private static boolean getItemEvent = false;

    public static OrderObject getShipment(String shippingKey) {
        
        
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("earliest", "groupF"));
        consumer.assign(Arrays.asList(new TopicPartition(shippingTopic, 0)));

        List<OrderObject> orders = new ArrayList<OrderObject>();
        
        final ConsumerRecords<String, String> records = consumer.poll(Duration.of(1, ChronoUnit.SECONDS));
        Hashtable<String, Hashtable<String, OrderObject>> orderTable = new Hashtable<String, Hashtable<String, OrderObject>>();
        OrderObject order = new OrderObject("", items);
        for (final ConsumerRecord<String, String> record : records) {
            String[] key = record.key().split(":");
            if (key.length > 1 && key[0].equals(shippingKey)) {
                order = OrderObject.deserialize(record.value());    
                order.setOrderKey(shippingKey);
            }
        }
        consumer.close();
        return order;
        }   
    
    public static List<OrderObject> getShippings() {
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("earliest", "groupD"));
        consumer.assign(Arrays.asList(new TopicPartition(shippingTopic, 0)));

        List<OrderObject> orders = new ArrayList<OrderObject>();
        
        final ConsumerRecords<String, String> records = consumer.poll(Duration.of(1, ChronoUnit.SECONDS));
        Hashtable<String, Hashtable<String, OrderObject>> orderTable = new Hashtable<String, Hashtable<String, OrderObject>>();
        
        for (final ConsumerRecord<String, String> record : records) {
            String[] key = record.key().split(":");
            if (key.length > 1) {
                Hashtable<String, OrderObject> order = new Hashtable<String, OrderObject>();
                OrderObject orderObj = OrderObject.deserialize(record.value());
                orderObj.setOrderKey(key[0]);

                if (orderTable.containsKey(key[1])) {
                    orderTable.get(key[1]).put(key[0], orderObj);
                } else {
                    order.put(key[0], orderObj);
                    orderTable.put(key[1], order);
                }
            }   
        }
        for (String userOffset : orderTable.keySet()) {
            for (String key : orderTable.get(userOffset).keySet()) {
                orders.add(orderTable.get(userOffset).get(key));
            }
        }
            // return CustomerObject.deserialize(record.value());
        consumer.close();
          
        return orders;
    }

    public static List<OrderObject> getCustomerShippings(String userOffset) {
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("earliest", "groupD"));
        consumer.assign(Arrays.asList(new TopicPartition(shippingTopic, 0)));

        List<OrderObject> orders = new ArrayList<OrderObject>();
        
        final ConsumerRecords<String, String> records = consumer.poll(Duration.of(1, ChronoUnit.SECONDS));
        Hashtable<String, Hashtable<String, OrderObject>> orderTable = new Hashtable<String, Hashtable<String, OrderObject>>();
        
        for (final ConsumerRecord<String, String> record : records) {
            String[] key = record.key().split(":");
            if (key.length > 1 && key[1].equals(userOffset)) {
                Hashtable<String, OrderObject> order = new Hashtable<String, OrderObject>();
                OrderObject orderObj = OrderObject.deserialize(record.value());
                orderObj.setOrderKey(key[0]);

                if (orderTable.containsKey(key[1])) {
                    orderTable.get(key[1]).put(key[0], orderObj);
                } else {
                    order.put(key[0], orderObj);
                    orderTable.put(key[1], order);
                }
            }   
        }

        if (orderTable.containsKey(userOffset)) {
            for (String key : orderTable.get(userOffset).keySet()) {
                orders.add(orderTable.get(userOffset).get(key));
            }
        }
            // return CustomerObject.deserialize(record.value());
        consumer.close();

        // StreamsBuilder builder = new StreamsBuilder();
        // KStream<String, String> stream = builder.stream(shippingTopic);        
        // KGroupedStream<String, String> groupedStream = stream.groupBy((key, value) -> key);
        // KTable<String, String> aggregatedTable = groupedStream.reduce((aggValue, newValue) -> aggValue + newValue);
        // KStream<String, String> resultStream = aggregatedTable.toStream();
        // resultStream.foreach((key, value) -> System.out.println(key + ": " + value));

      
    
        return orders;
    }


    /**
     * Returns the set of properties for the Kafka consumer.
     * @param offsetResetStrategy the offset reset strategy, 
     * either "earliest" or "latest".
     * "earliest" means that the consumer will start reading from the beginning of the topic.
     * "latest" means that the consumer will start reading from the end of the topic.
     * @param groupId the group id of the consumer
     * @return
     */
    private static Properties kafkaConfig(String offsetResetStrategy, String groupId) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(autoCommit));
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, String.valueOf(autoCommitIntervalMs));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetResetStrategy);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        return props;
    }
}
