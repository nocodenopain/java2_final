package springproject.service;

import com.example.springproject.domain.Repo;
import com.example.springproject.web.RepoController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RepoServiceImpl implements RepoService {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Repo findInfo() {
        return new Repo();
    }

    @Override
    public void getValue(String url) throws IOException {
        String newUrl = url.replace("https://", "https://api.").replace(".com", ".com/repos");
        String issues = newUrl + "/issues?state=all&per_page=100";
        String commits = newUrl + "/commits";
        String contributors = newUrl + "/contributors";
        String releases = newUrl + "/releases";
        issues = crawl(issues);
        contributors = crawl(contributors);
        commits = crawl(commits);
        releases = crawl(releases);
        writeFile(issues, "issues");
        writeFile(contributors, "contributors");
        writeFile(commits, "commits");
        writeFile(releases, "releases");
    }

    public String crawl(String u) throws IOException {
        URL url = new URL(u);
        URLConnection connection = url.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
        int code = httpURLConnection.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) return null;
        InputStream inputStream = httpURLConnection.getInputStream();
        Scanner in = new Scanner(inputStream);
        StringBuilder ans = new StringBuilder();
        while (in.hasNext()) {
            ans.append(in.nextLine());
        }
        return ans.toString();
    }

    public void writeFile(String s, String title) {
        FileWriter writer;
        try {
            writer = new FileWriter("D:\\CS209-project-22fall\\src\\main\\java\\data\\" + title + ".json"); // 如果已存在，以覆盖的方式写文件
            // writer = new FileWriter("testFileWriter.txt", true); // 如果已存在，以追加的方式写文件
            writer.write(""); //清空原文件内容
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Commits> readCommits(String path) throws Exception {
        String json = RepoController.readJsonFile(path);
        JSONParser parser = new JSONParser();
        JSONArray o = (JSONArray) parser.parse(json);
        List<Commits> list = new ArrayList<>();
        for (Object object : o) {
            JSONObject tmp = (JSONObject) object;
            JSONObject j = (JSONObject) tmp.get("commit");
            JSONObject jj = (JSONObject) j.get("committer");
            String date = (String) jj.get("date");
            list.add(new Commits(date));
        }
        return list;
    }

    public List<Releases> readReleases(String path) throws Exception {
        String json = RepoController.readJsonFile(path);
        JSONParser parser = new JSONParser();
        JSONArray o = (JSONArray) parser.parse(json);
        List<Releases> list = new ArrayList<>();
        for (Object object : o) {
            JSONObject tmp = (JSONObject) object;
            list.add(new Releases((String) tmp.get("published_at")));
        }
        return list;
    }

    public int releasesNumber(List<Releases> list){
        return list.size();
    }

    public List<Integer> commitBetweenRelease(List<Releases> releases, List<Commits> commits){
        List<Integer> ans = new ArrayList<>();
        if (releases.size() == 0){
            ans.add(commits.size());
            return ans;
        }
        if (releases.size() == 1){
            int temp = 0;
            for (Commits commit : commits) {
                if (commit.date.getTime() <= releases.get(0).date.getTime()) {
                    temp++;
                }
            }
            ans.add(temp);
            temp = 0;
            for (Commits commit : commits) {
                if (commit.date.getTime() > releases.get(0).date.getTime()) {
                    temp++;
                }
            }
            ans.add(temp);
            return ans;
        }
        int t = 0;
        int temp = 0;
        for (Commits commit : commits) {
            if (commit.date.getTime() <= releases.get(0).date.getTime()) {
                temp++;
            }
        }
        ans.add(temp);
        for (int i = 0; i < releases.size() - 1; i++) {
            Date a = releases.get(i).date;
            Date b = releases.get(i + 1).date;
            int cnt = 0;
            for (int j = t; j < commits.size(); j++) {
                long tmp = commits.get(j).date.getTime();
                if (tmp <= b.getTime() && tmp > a.getTime()) {
                    cnt++;
                    t++;
                } else {
                    break;
                }
            }
            ans.add(cnt);
        }
        temp = 0;
        for (Commits commit : commits) {
            if (commit.date.getTime() > releases.get(releases.size() - 1).date.getTime()) {
                temp++;
            }
        }
        return ans;
    }

    public List<Integer> getCommitsByYear(List<Commits> list){
        List<Integer> ans = new ArrayList<>();
        Commits[] commits = new Commits[list.size()];
        for (int i = 0; i < list.size(); i++) {
            commits[i] = list.get(i);
        }
        Arrays.sort(commits);
        int t = 0;
        for (int i = 2008; i < 2023; i++) {
            int tmp = 0;
            for (int j = t; j < commits.length; j++) {
                if (commits[j].date.getYear() == i - 1900) {
                    tmp++;
                    t++;
                } else{
                    break;
                }
            }
            ans.add(tmp);
        }
        return ans;
    }

    public List<Developer> readDeveloper(String path) throws Exception {
        String json = RepoController.readJsonFile(path);
        JSONParser parser = new JSONParser();
        JSONArray o = (JSONArray) parser.parse(json);
        List<Developer> list = new ArrayList<>();
        for (Object object : o) {
            JSONObject tmp = (JSONObject) object;
            String name = (String) tmp.get("login");
            if (name == null || name.equals("null")) name = (String) tmp.get("name");
            long contributions = (long) tmp.get("contributions");
            list.add(new Developer(name, contributions));
        }
        return list;
    }

    public int DevelopNumber(List<Developer> list) {
        return list.size();
    }

    public List<String> getTopTen(List<Developer> list) {
        Developer[] developers = new Developer[list.size()];
        for (int i = 0; i < list.size(); i++) {
            developers[i] = list.get(i);
        }
        Arrays.sort(developers);
        if (developers.length > 10) {
            int t = 0;
            List<String> ans = new ArrayList<>();
            while (t++ < 11) {
                ans.add(developers[list.size() - t].name);
            }
            return ans;
        }
        List<String> ans = new ArrayList<>();
        for (Developer developer : developers) {
            ans.add(developer.name);
        }
        return ans;
    }

    public List<Issue> readIssues(String path) throws Exception {
        String json = RepoController.readJsonFile(path);
        JSONParser parser = new JSONParser();
        JSONArray o = (JSONArray) parser.parse(json);
        List<Issue> list = new ArrayList<>();
        addIssueToList(o, list);
        return list;
    }

    private void addIssueToList(JSONArray o, List<Issue> list) {
        for (Object object : o) {
            JSONObject tmp = (JSONObject) object;
            String start = (String) tmp.get("created_at");
            String end = (String) tmp.get("closed_at");
            String open = (String) tmp.get("state");
            Issue i = new Issue(start, end, open);
            list.add(i);
        }
    }

    public int getOpenedNumber(List<Issue> list) {
        int ans = 0;
        for (Issue i : list) {
            if (i.open) ans += 1;
        }
        return ans;
    }

    public double getAverage(List<Issue> list) throws ParseException {
        double sum = 0;
        int t = 0;
        for (Issue i : list) {
            if (i.endTime != null && i.finished) {
                t++;
                sum += calDiff(i);
            }
        }
        return sum / t;
    }

    public long getBigDiff(List<Issue> list) throws ParseException {
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        for (Issue i : list) {
            if (i.endTime != null && i.finished) {
                long tmp = calDiff(i);
                max = Math.max(tmp, max);
                min = Math.min(tmp, min);
            }
        }
        return max - min;
    }

    public double getSquare(List<Issue> list) throws Exception {
        double average = getAverage(list);
        double sum = 0;
        int t = 0;
        for (Issue i : list) {
            if (i.endTime != null && i.finished) {
                t++;
                sum += Math.pow(average - calDiff(i), 2);
            }
        }
        return sum / t;
    }

    public long calDiff(Issue i) throws ParseException {
        String start = i.startTime.replace("T", " ").replace("Z", "");
        String end = i.endTime.replace("T", " ").replace("Z", "");
//        System.out.println(start);
//        System.out.println(end);
        Date begin = simpleDateFormat.parse(start);
        Date over = simpleDateFormat.parse(end);
        return over.getTime() - begin.getTime();
    }

    public static void main(String[] args) throws Exception {
        RepoServiceImpl re = new RepoServiceImpl();
//        re.getValue("https://github.com/yuk1i/cs305-2022fall-homework1-student");
//        System.out.println(re.readIssues("D:\\CS209-project-22fall\\src\\main\\java\\data\\opened_issues.json").get(10).startTime);
//        String t = re.readIssues("D:\\CS209-project-22fall\\src\\main\\java\\data\\opened_issues.json").get(0).startTime;
//        String date = t.split("T")[0];
//        String time = t.split("T")[1].replace("Z", "");
//        System.out.println(date);
//        System.out.println(time);
//        List<Developer> list = new ArrayList<>();
//        list = re.readDeveloper("D:\\CS209-project-22fall\\src\\main\\java\\contributors.json");
//        System.out.println(re.DevelopNumber(list));
//        System.out.println(re.getTopTen(list));
        //        System.out.println(re.calDiff(list.get(1)));
//        List<Releases> list = re.readReleases("D:\\\\CS209-project-22fall\\\\src\\\\main\\\\java\\releases.json");
//        System.out.println(list);
//        for (Releases commits : list){
//            System.out.println(commits.date);
//        }
//        List<Developer> developers = re.readDeveloper("D:\\CS209-project-22fall\\src\\main\\java\\data\\contributors.json");
        List<Commits> list = re.readCommits("D:\\CS209-project-22fall\\src\\main\\java\\data\\commits.json");
        System.out.println(re.getCommitsByYear(list));
    }
}

//TODO:issue还要抓closed的,现在是只抓了opened的，后面抓完只计算closed就可以了，路径是这个：issues?q=is%3Aissue+is%3Aclosed
