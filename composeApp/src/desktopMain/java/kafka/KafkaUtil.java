package kafka;

import static util.RangeUtil.findRageToRead;
import static util.RangeUtil.mergeRanges;
import static util.RangeUtil.sortRange;

import db.ReadOffsetDB;
import db.TopicDataDB;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Range;
import org.apache.kafka.common.TopicPartitionInfo;

public class KafkaUtil {



  public static void readLastNMessage( KafkaConsumerClient consumerClient,String topic, List<TopicPartitionInfo> tpi,  long N)
  {
    Map<Integer, Range<Long>> offsetRange = consumerClient.getOffsetRange(topic,tpi);
    for(int p:offsetRange.keySet())
    {
      Range<Long> range = offsetRange.get(p);
      long start = Math.max(range.getMinimum(),range.getMaximum()-N);
      long end = range.getMaximum();
      range = Range.of(start,end);
      offsetRange.put(p,range);
      readFromKafka(consumerClient,topic,p,range);
    }
  }


  public void readFromLite()
  {

  }

  public static void readFromKafka(
      KafkaConsumerClient consumerClient ,String topic, int partation, Range<Long> toRead) {
    String eTopic = topic +"_"+ partation;
    TopicDataDB.createTable(eTopic);

    List<Range<Long>> current = ReadOffsetDB.select(eTopic);
    System.err.println(current);
    List<Range<Long>> readRange = findRageToRead(current,toRead);
    System.err.println(readRange );
   // KafkaConsumerClient consumerClient =    new KafkaConsumerClient("localhost:29092","grpID");
    for(Range<Long> r: readRange)
    {
      System.err.println("Reading from "+r.getMinimum()+" to " + r.getMaximum());
      consumerClient.readMsg(topic, partation,r.getMinimum(),r.getMaximum());
    }
    current.addAll(readRange);
    sortRange(current);
    System.err.println(current);

    //merge
    List<Range<Long>> merge =   mergeRanges(current);
    System.err.println(merge);

    ReadOffsetDB.deleteAll(eTopic);
    ReadOffsetDB.insertMultiple(eTopic,merge);
  }
}
