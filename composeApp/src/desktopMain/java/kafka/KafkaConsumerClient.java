package kafka;

import com.kafkaui.models.JsonFilter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static util.JsonUtil.getValueFromPath;

public class KafkaConsumerClient {


    KafkaConsumer<String, String> consumer;

    public KafkaConsumerClient(String bootstrapServers, String groupId) {


        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // create consumer
        consumer = new KafkaConsumer<>(properties);
    }


    public List<ConsumerRecord> getNMessages(String topic, int n, boolean fromStart, List<TopicPartitionInfo> tpi, KafkaFilterOption options) throws ExecutionException, InterruptedException {

        Map<Integer, Long> beginningOffset = getBeginningOffsets(topic, tpi);
        Map<Integer, Long> endOffset = getEndOffsets(topic, tpi);

        long msgToRead = 0;

        consumer.unsubscribe();

        consumer.assign(buildPartitionList(topic, tpi));
        //find the offset to seek
        for (int partition : beginningOffset.keySet()) {
            long offset = 0;
            if ((endOffset.get(partition) - beginningOffset.get(partition)) >= n) {
                offset = fromStart ? beginningOffset.get(partition) : endOffset.get(partition) - n;
                msgToRead += n;
            } else {
                offset = beginningOffset.get(partition);
                msgToRead += endOffset.get(partition) - offset;
            }
            consumer.seek(new TopicPartition(topic, partition), offset);
        }

        // TODO: 03-11-2024 logic needs to be changed
        long msgRead = 0;
        List<ConsumerRecord> msgs = new ArrayList<>();
        boolean flag = true;
        while (msgToRead > 0 && flag) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
            for (ConsumerRecord record : records) {
                msgRead++;
                filterMessage(record, msgs, options);
                if (msgRead == msgToRead) {
                    flag = false;
                    break;
                }
            }
        }

        return msgs;
    }

    private void filterMessage(ConsumerRecord msg, List<ConsumerRecord> msgs, KafkaFilterOption option) {
        if (option.getFilterType()==KafkaFilterOption.JSON_FILTER) {
            try {
                JSONObject obj = new JSONObject(msg.value().toString());
                boolean shouldAdd = true;
                for (JsonFilter filter : option.getJsonFilters()) {
                    if (!getValueFromPath(obj, filter.getPath()).equalsIgnoreCase(filter.getValue())) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd) {
                    msgs.add(msg);
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            if (option.getSearchText().length() == 0 || msg.value().toString().toLowerCase().contains(option.getSearchText().toLowerCase())) {
                msgs.add(msg);
            }
        }
    }

    public Map<Integer, Long> getBeginningOffsets(String topic, List<TopicPartitionInfo> tpi) throws ExecutionException, InterruptedException {
        List<TopicPartition> partitions = buildPartitionList(topic, tpi);
        HashMap<Integer, Long> offsets = new HashMap<>();
        Map<TopicPartition, Long> offs = consumer.beginningOffsets(partitions);
        extractOffsetPerPartition(offs, offsets);
        return offsets;
    }

    private static void extractOffsetPerPartition(Map<TopicPartition, Long> offs, HashMap<Integer, Long> offsets) {
        for (TopicPartition tp : offs.keySet()) {
            offsets.put(tp.partition(), offs.get(tp));
        }
    }

    public Map<Integer, Long> getEndOffsets(String topic, List<TopicPartitionInfo> tpi) throws ExecutionException, InterruptedException {
        List<TopicPartition> partitions = buildPartitionList(topic, tpi);
        HashMap<Integer, Long> offsets = new HashMap<>();
        Map<TopicPartition, Long> offs = consumer.endOffsets(partitions);
        extractOffsetPerPartition(offs, offsets);
        return offsets;
    }


    private List<TopicPartition> buildPartitionList(String topic, List<TopicPartitionInfo> partition) {
        List<TopicPartition> partitions = new ArrayList<>();
        for (TopicPartitionInfo tpi : partition) {
            partitions.add(new TopicPartition(topic, tpi.partition()));
        }
        return partitions;
    }


    public Map<String, List<PartitionInfo>> getAllTopics() {
        Map<String, List<PartitionInfo>> list = consumer.listTopics();
        return list;
    }
}
