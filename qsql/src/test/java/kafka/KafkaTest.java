package kafka;

import blxt.qjava.autovalue.util.ObjectPool;
import blxt.qjava.qsql.kafka.KafkaConfiguration;
import blxt.qjava.qsql.kafka.KafkaConnection;
import blxt.qjava.qsql.kafka.KafkaPool;
import blxt.qjava.qsql.kafka.KafkaProducerConnection;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @Author: Zhang.Jialei
 * @Date: 2020/8/9 21:54
 */
public class KafkaTest {
    static public  String topic = "rule.user";//定义主题
    static String msg = "{\"modelData\":{\"clientInformation\":{\"clientId\":\"d123123\",\"groupId\":\"p1\",\"tags\":\"g1\",\"userId\":\"user1\"},\"fields\":{\"temperature\":15,\"humidity\":58,\"active\":false,\"fengsu\":50},\"tags\":{\"devicekey\":\"dev132123\",\"productKey\":\"a1wsYOqfoAi\",\"user\":\"user1\",\"groupkey\":\"group1\"},\"topic\":\"/sys/data/p1/d123123/things/properts\"},\"ruleEntitry\":{\"empty\":false,\"identifier\":\"temperature\",\"name\":\"属性测试\",\"symbos\":\"notmore\",\"type\":1,\"value\":\"30\"}}";

    public static void main(String[] args) throws Exception {

        KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();
        kafkaConfiguration.setBootstrap_servers("ip:port");
        KafkaPool.newInstance(kafkaConfiguration);
        KafkaPool.getInstance().addProducer("Producer");

        KafkaProducerConnection connection = KafkaPool.getInstance().getProducer("Producer");
        connection.put("topic", "msg");

        //String serverIp = "47.242.60.114:21006"; // 192.168.3.30:9092
        String serverIp = "192.168.3.30:9092";
        // 生产者配置
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverIp);//kafka地址，多个地址用逗号分割
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.qjava.qsql.kafka.CidPartitioner"); // 自定义主题分区规则
        // 消费者配置
        Properties properties2 = new Properties();
        properties2.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverIp);//kafka地址，多个地址用逗号分割
        properties2.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties2.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties2.put(ConsumerConfig.GROUP_ID_CONFIG, "user.test"); // 消费者组id


        // 消费者
        KafkaConnection kafkaConnection_c = new KafkaConnection(properties2);
        kafkaConnection_c.buildConsumer();
        kafkaConnection_c.subscribe(topic);

        Runnable runnable = kafkaConnection_c.makeListener(new SubscribeListenerImpTest(), 1000);

        new Thread(runnable).start();

        // 生产者
        KafkaConnection KafkaConnection = new KafkaConnection(properties);
        KafkaConnection.buildProducer();

       // KafkaManager.put(topic, "测试2");
        try {
            KafkaConnection.putSync(topic,"test", msg);
            System.out.println("发送" + topic + "->" + msg);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
