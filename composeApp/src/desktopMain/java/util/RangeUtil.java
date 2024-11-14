package util;


import static kafka.KafkaUtil.readFromKafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kafka.KafkaConsumerClient;
import org.apache.commons.lang3.Range;

public class RangeUtil {



    public static List<Range<Long>> findRageToRead(List<Range<Long>> currentRange, Range<Long> r)
    {
        List<Range<Long>> newRange = new ArrayList<>();
        for(Range<Long> range:currentRange)
        {

            if(r.isOverlappedBy(range))
            {
                Range<Long> interSection = r.intersectionWith(range);
                Range<Long> sr1 = Range.of(r.getMinimum(),interSection.getMinimum());
                r = Range.of(interSection.getMaximum(),r.getMaximum());
                if(!Objects.equals(sr1.getMinimum(), sr1.getMaximum())) {
                    newRange.add(sr1);
                }
            }
        }

        if(!Objects.equals(r.getMinimum(), r.getMaximum())) {
            newRange.add(r);
        }
        System.err.println("Range : " + newRange);

        return newRange;
    }

    public static List<Range<Long>> mergeRanges(List<Range<Long>> currentRange)
    {
        List<Range<Long>> newRange = new ArrayList<>();


        // Index of the last merged
        int resIdx = 0;

        for (int i = 1; i < currentRange.size(); i++) {

            // If current interval overlaps with the
            // last merged interval
            if (currentRange.get(resIdx).getMaximum() >= currentRange.get(i).getMinimum()) {
                Range<Long> rn = Range.of(currentRange.get(resIdx).getMinimum(),
                    Math.max(currentRange.get(resIdx).getMaximum(), currentRange.get(i).getMaximum()));
                currentRange.set(resIdx,rn);
            }
                // Move to the next interval
            else {
                resIdx++;
                currentRange.set(resIdx,currentRange.get(i));
            }
        }
        return currentRange.subList(0,resIdx+1);
    }

    public static void  sortRange(List<Range<Long>> currentRange)
    {
        currentRange.sort((o1, o2) -> (int) (o1.getMinimum() - o2.getMinimum()));
    }

    public static void xyz(){
        System.err.println("lol");
        String topic = "akshay";
        int partation = 0;
        KafkaConsumerClient consumerClient =    new KafkaConsumerClient("localhost:29092","grpID");
        readFromKafka(consumerClient,topic, 0,Range.of(0L,2500L));
        readFromKafka(consumerClient,topic, 1,Range.of(0L,2500L));
        readFromKafka(consumerClient,topic, 2,Range.of(0L,2500L));
        readFromKafka(consumerClient,topic, 3,Range.of(0L,2500L));
    }


}
