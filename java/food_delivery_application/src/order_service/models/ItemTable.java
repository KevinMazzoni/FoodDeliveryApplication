package order_service.models;

import java.util.Hashtable;

public class ItemTable {
    private static final Hashtable<String, Integer> topicPartitions = new Hashtable<String, Integer>();
    static {
        topicPartitions.put("banana", 0);
        topicPartitions.put("mela", 1);
        topicPartitions.put("arancia", 2);
        topicPartitions.put("mandarino", 3);
        topicPartitions.put("pesca", 4);
        topicPartitions.put("pera", 5);
        topicPartitions.put("albicocca", 6);
        topicPartitions.put("fragola", 7);
        topicPartitions.put("melone", 8);
        topicPartitions.put("uva", 9);
    }
    public static Hashtable<String, Integer> instance() {
        return topicPartitions;
    }
}
