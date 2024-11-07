package kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KafkaAdminClient {
    Properties properties = new Properties();
    AdminClient adminClient;

    public KafkaAdminClient(String bootstrapServers, String groupId) {
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    }

    public KafkaAdminClient connectClient() throws ConfigException {
        if(adminClient !=null)
        {
            adminClient.close();
        }
        adminClient = AdminClient.create(properties);
        return this;
    }


    public List<String> getAllTopics() throws ExecutionException, InterruptedException, TimeoutException {
        Set<String> topics = adminClient.listTopics().names().get(10, TimeUnit.SECONDS);
        List<String> topicsList = new ArrayList<>();
        topicsList.addAll(topics);
        Collections.sort(topicsList);
        return topicsList;
    }


    public Map<String, TopicDescription> describeTopics(List<String> topics) throws ExecutionException, InterruptedException, TimeoutException {
        Map<String, TopicDescription> describeTopicsResult = adminClient.describeTopics(topics).allTopicNames().get(10, TimeUnit.SECONDS);
        return describeTopicsResult;
    }

    public TopicDescription describeSingleTopic(String topic) throws ExecutionException, InterruptedException, TimeoutException {
        List<String> topics = new ArrayList<>();
        topics.add(topic);
        return describeTopics(topics).get(topic);
    }

    public void close() {
        adminClient.close();
    }
}
