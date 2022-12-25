package springproject.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Commits implements Comparable{

    Date date;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Commits(String date) throws ParseException {
        date = date.replace("T", " ").replace("Z", "");
        this.date = simpleDateFormat.parse(date);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Commits){
            if (this.date.getTime() > ((Commits) o).date.getTime()) return 1;
            if (this.date.getTime() == ((Commits) o).date.getTime()) return 0;
        }
        return -1;
    }
}
