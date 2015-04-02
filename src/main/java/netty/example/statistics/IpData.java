package netty.example.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Владислав on 02.04.2015.
 */
public class IpData {

    private AtomicInteger count;
    private String time;


    public IpData() {
        this.time=CurrentTimeStamp();
        count=new AtomicInteger(1);
    }

    public void incrementCount ()  {
        count.incrementAndGet();
    }

    public void updateTime() {
        time=CurrentTimeStamp();
    }

    public  String CurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        String formatCurrentTime = sdfDate.format(currentTime);
        return formatCurrentTime;
    }

    public AtomicInteger getCount() {
        return count;
    }

    public String getTime() {
        return time;
    }
}
