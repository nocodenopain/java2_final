package springproject.service;

public class Developer implements Comparable {
    String name;
    long contributions;

    public Developer(String name, long contributions) {
        this.name = name;
        this.contributions = contributions;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Developer){
            long a = contributions;
            long b = ((Developer) o).contributions;
            if (a > b) return 1;
            if (a == b) return 0;
        }
        return -1;
    }
}
