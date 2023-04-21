package order_service.kafka_handlers;

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
import org.apache.kafka.common.serialization.StringDeserializer;

import order_service.models.ItemTable;
import order_service.models.ItemObject;

public class OrderConsumer {
    private static final String itemTopic = "item_topic";
    private static final String serverAddr = "localhost:9092";
    private static final boolean autoCommit = false;
    private static final int autoCommitIntervalMs = 15000;

    private static final Hashtable<String, Integer> topicPartitions = ItemTable.instance();
    private static List<ItemObject> items = new ArrayList<ItemObject>();
    private static boolean getItemEvent = false;

    public void start() {
        // Initialize the list of items from the item topic at startup
        items = getItems();

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Listen forever for events on the item topic
                while (true) {
                    // Poll for new events of 5 seconds
                    listenForItemEvent();
                }
            }
        });  
        t1.start();
    }


    /**
     * Polls for new events on the item topic, separated by partition.
     * From each partition only the last event in the queue is taken.
     * This method is called by the thread in the start() method.
     * Also, this method can take a long time to execute, so it is
     * not recommended to call it directly.
     *
     * @return the list of items in the item topic
     *
     */
    public static List<ItemObject> getItems() {
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("earliest", "groupB"));
        
        // wait 5 seconds
        List<ItemObject> items = new ArrayList<ItemObject>();
        
        for (String partitionKey : topicPartitions.keySet()) {
            
            TopicPartition partition = new TopicPartition(itemTopic, topicPartitions.get(partitionKey));
            consumer.assign(Collections.singletonList(partition));
            // consumer.assign(Arrays.asList(partition0));
            consumer.seekToEnd(Collections.singletonList(partition));
            Map<TopicPartition, Long>endOffsets = consumer.endOffsets((consumer.assignment()));
            
            if (endOffsets.get(partition) == 0) {
                System.out.println("Partition " + partitionKey + " is empty");
                
            } else {

            consumer.seek(partition, endOffsets.get(partition) - 1);
            final ConsumerRecords<String, String> records = consumer.poll(Duration.of(500, ChronoUnit.MILLIS));
        

            // final ConsumerRecords<String, String> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
            
            for (final ConsumerRecord<String, String> record : records) {
                System.out.println("Partition: " + record.partition() +
                        "\tOffset: " + record.offset() +
                        "\tKey: " + record.key() +
                        "\tValue: " + record.value()
                );
                try {
                        ItemObject item = ItemObject.deserialize(topicPartitions.get(partitionKey).toString(), record.value());
                        items.add(item);
                    } catch (Exception e) {
                        consumer.close();

                        System.out.println("Error: " + e);
                        return items;
                    }
            }
        }
        }
       
         
        consumer.close();
        System.out.println("Done updating items");

        return items;
    }

    /** 
     * Listen for new events on the item topic with a timeout of 5 seconds.
     * Whenever a new event is received, the getItems() method is called.
     * 
     */
    private static void listenForItemEvent() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaConfig("latest", "groupA"));
        consumer.subscribe(Arrays.asList(itemTopic));
        
        // It is necessary to wait for a bit before polling for new events
        // Otherwise, the consumer will not receive any events
        // This is because the consumer is not yet subscribed to the topic, I suppose
        try{
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        System.out.println("start listening for item events");
        
        while (true) {
            final ConsumerRecords<String, String> records = consumer.poll(Duration.of(2, ChronoUnit.SECONDS));
            if (records.count() > 0) {
                //  A new thread is launched so that the getItems() method can be called asynchronously 
                // and no messages get lost while waiting
                Thread t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Tell the getItemsFast() method to wait for the getItems() method to finish
                        getItemEvent = true;
                        items = getItems();
                        getItemEvent = false;
                    }
                });  
                t2.start();
            }
        }
        // consumer.close();
    }
    
    /**
     * Returns the list of items in the item topic.
     * This method is usually fast but can take a long time if a new event is received at the same time.
     * This is to ensure that the returned list is always up to date.
     * 
     * @return the list of items in the item topic
     */
    public List<ItemObject> getItemsFast() {
        while (getItemEvent) {
            try{ 
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
        return items;
    }

    /**
     * Returns the set of properties for the Kafka consumer.
     * @param offsetResetStrategy the offset reset strategy, 
     * either "earliest" or "latest".
     * "earliest" means that the consumer will start reading from the beginning of the topic.
     * "latest" means that the consumer will start reading from the end of the topic.
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
        return props;
    }
}