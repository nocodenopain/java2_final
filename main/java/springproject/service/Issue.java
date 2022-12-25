package springproject.service;

public class Issue {
    String startTime;
    String endTime;
    Boolean open;

    Boolean finished;

    public Issue(String startTime, String endTime, String open) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.open = open.equals("open");
        this.finished = endTime != null;
    }
}
