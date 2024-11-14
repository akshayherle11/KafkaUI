package db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Range;

public class ReadOffsetDB {

  static String tableDDL = "CREATE TABLE If not exists read_offset (topic TEXT NOT NULL,\"start\" NUMERIC NOT NULL,\"end\" NUMERIC);";

  static String insertQuery = "INSERT INTO read_offset (topic, \"start\", \"end\") VALUES('%s', %d, %d);";
  static String insertMultiple = "INSERT INTO read_offset (topic, \"start\", \"end\") VALUES ";

  static String select ="SELECT * from read_offset ro where ro.topic like '%s' order by start;";
  static String delete ="DELETE FROM read_offset  WHERE topic like '%s';";
  public static void createTable() {
    LiteDb.runQuery(tableDDL);
  }

  public static void insertSingle(String topic, Range<Long> range)
  {
    String query = String.format(insertQuery,topic,range.getMinimum(),range.getMaximum());
    LiteDb.runQuery(query);
  }
  public static void insertMultiple(String topic, List<Range<Long>> ranges)
  {
    //
    String s = "('%s', %d, %d)"; //;
    List<String> vals = new ArrayList<>();
   for(Range<Long> range:ranges)
   {
     vals.add(String.format(s, topic, range.getMinimum(), range.getMaximum()));
   }
    LiteDb.runQuery(insertMultiple + String.join(",", vals));
  }

  public static void deleteAll(String topic)
  {
    String query = String.format(delete,topic);
    LiteDb.runQuery(query);
  }


  public static List<Range<Long>> select(String topic)  {
    List<Range<Long>> ranges= new ArrayList<>();

      ResultSet rs =  LiteDb.runQuery(String.format(select,topic));
        try
        {
          while( rs.next())
          {
            Range<Long> range = Range.of(rs.getLong("start"),rs.getLong("end"));
            ranges.add(range);
          }
        }
        catch (Exception ignored)
        {
          System.err.println(ignored);
        }
    return ranges;
  }


}
