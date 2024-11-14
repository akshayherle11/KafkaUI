package db;

import com.kafkaui.models.KafkaMessage;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class TopicDataDB {

  static String tableDDL = "CREATE TABLE  If not exists topic_%s (\"offset\" NUMERIC NOT NULL,\"data\" TEXT, ts NUMERIC, CONSTRAINT %s_PK PRIMARY KEY (\"offset\"));";

  static String insertMultiple = "INSERT  INTO topic_%s (\"offset\", \"data\", ts) VALUES %s on CONFLICT (\"offset\")   DO UPDATE SET \"offset\" =\"offset\" ";

  public static void createTable(String topic) {
    LiteDb.runQuery(String.format(tableDDL,topic,topic));
  }

  public static List<KafkaMessage> select(String q)
  {
    List<KafkaMessage> msg = new ArrayList<>();

    ResultSet rs =  LiteDb.runQuery(q);
    try
    {
      while( rs.next())
      {
        KafkaMessage m  = new KafkaMessage(rs.getString("data"),rs.getLong("offset"),rs.getLong("ts"));
        msg.add(m);
      }
    }
    catch (Exception ignored)
    {
      System.err.println(ignored);
    }
    return msg;
  }


  public static void insertMultiple(String topic, List<ConsumerRecord> msgs)
  {
    //
    if(msgs.size()==0)
    {
      return;
    }
    String s = "(%d, '%s', %d)"; //;
    List<String> vals = new ArrayList<>();
    for(ConsumerRecord msg : msgs)
    {
      vals.add(String.format(s, msg.offset(), msg.value().toString(),msg.timestamp()));
    }
    LiteDb.runQueryCommit(String.format(insertMultiple,topic,String.join(",", vals))  );
  }

}
