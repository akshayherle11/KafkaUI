package kql;

import static kafka.KafkaUtil.readFromKafka;

import com.kafkaui.models.KafkaMessage;
import db.TopicDataDB;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kafka.KafkaConsumerClient;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.apache.commons.lang3.Range;
import org.apache.kafka.common.TopicPartitionInfo;

public class KQL {


  private String query = "";

  String topic = "";


  public KQL(String topic, String query) {
    this.topic = topic;
    if (!query.trim().isEmpty()) {

      this.query = String.format("Select * from %s where %s %s", topic, query,"ORDER BY ts desc");

    }
    else {
      this.query = String.format("Select * from %s %s", topic, "ORDER BY ts desc");

    }
  }


  String jsonQ = "json_extract(\"data\",'$.%s')";

  public List<KafkaMessage> executeQuery(
      KafkaConsumerClient consumerClient, int N, List<TopicPartitionInfo> tpi) throws Exception {
    PlainSelect select = parse();

    List<KafkaMessage> msg = new ArrayList<>();
    if (select != null) {

      Limit l = new Limit();
      l.setRowCount(new LongValue(N));
      select.setLimit(l);

      select.setLimit(l);
      Map<Integer, Range<Long>> offsetRange = consumerClient.getOffsetRange(topic, tpi);
      for (int p : offsetRange.keySet()) {
        Range<Long> range = offsetRange.get(p);
        long start = Math.max(range.getMinimum(), range.getMaximum() - N);
        long end = range.getMaximum();
        range = Range.of(start, end);
        offsetRange.put(p, range);
        readFromKafka(consumerClient, topic, p, range);
      }

      for (TopicPartitionInfo tp : tpi) {
        String eTopic = "topic_" + topic + "_" + tp.partition();
        select.setFromItem(new Table(eTopic));
        System.err.println(select.toString());
        msg.addAll(
            TopicDataDB.select(select.toString()));
      }
      //
    } else {
      throw new Exception("Wrong SQL");
    }

    return msg;
  }


  public PlainSelect parse() {
    try {
      PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(query);

      Expression x = select.getWhere();

      if (x != null) {

        x.accept(new ExpressionVisitorAdapter<Void>() {
          @Override
          public <S> Void visit(EqualsTo expr, S parameters) {
            System.err.println(expr.getLeftExpression().getClass());
            expr.setLeftExpression(new Column(String.format(
                jsonQ, expr.getLeftExpression().toString()
            )));

            super.visitBinaryExpression(expr, parameters);
            return null;
          }


          public <S> Void visit(Column column, S context) {
            System.out.println("Found a Column " + column.getColumnName());
            return null;
          }
        }, null);
      }

      return select;

    } catch (JSQLParserException e) {
      System.err.println(e);
    }

    return null;
  }

}
