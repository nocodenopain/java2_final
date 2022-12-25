package springproject.web;

import com.example.springproject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;

/**
 * 总的目录是localhost:8084/repo
 * 中间每个方法我都写了注解表名返回类型
 * 函数接口是按照评分标准里的来写的
 * 你只读这一个文件就够了
 */

@RestController
@RequestMapping("/repo")
public class RepoController {

    /**
     * 读取json文件，返回json串
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }

            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Autowired
    private RepoServiceImpl repoServiceImpl;

    //返回developer的总数，类型为单个int
    @GetMapping("/developerNumber")
    public int getDevelopNumber() throws Exception {
        List<Developer> list = repoServiceImpl.readDeveloper("D:\\CS209-project-22fall\\src\\main\\java\\data\\contributors.json");
        return repoServiceImpl.DevelopNumber(list);
    }
    //返回提交次数前十的developer，如果总数不到十个，那么将返回所有developer，返回格式为list
    @GetMapping("/topTenDeveloper")
    public List<String> getTopTenDeveloper() throws Exception {
        List<Developer> list = repoServiceImpl.readDeveloper("D:\\CS209-project-22fall\\src\\main\\java\\data\\contributors.json");
        return repoServiceImpl.getTopTen(list);
    }
    //返回issue个数，int
    @GetMapping("IssueNumber")
    public int[] getIssueNumber() throws Exception {
        List<Issue> list = repoServiceImpl.readIssues("D:\\CS209-project-22fall\\src\\main\\java\\data\\issues.json");
        int[] a = new int[2];
        a[0] = repoServiceImpl.getOpenedNumber(list);
        a[1] = list.size() - a[0];
        return a;
    }
    //返回issue的一些特征数据，为double数组，共三个元素，分别为平均值，极差和方差，单位为秒
    @GetMapping("IssueData")
    public double[] getIssueDate() throws Exception{
        List<Issue> list = repoServiceImpl.readIssues("D:\\CS209-project-22fall\\src\\main\\java\\data\\issues.json");
        double[] a = new double[3];
        a[0] = repoServiceImpl.getAverage(list);
        a[1] = repoServiceImpl.getBigDiff(list);
        a[2] = repoServiceImpl.getSquare(list);
        return a;
    }
    //返回release的总数
    @GetMapping("releasesNumber")
    public int getReleasesNumber() throws Exception {
        List<Releases> list = repoServiceImpl.readReleases("D:\\CS209-project-22fall\\src\\main\\java\\data\\releases.json");
        return repoServiceImpl.releasesNumber(list);
    }
    //返回每两个release之间的commit数，假设有三次release的话，返回值格式大概为{1,2,3,4}，意思是第一次之前提交一次。。。
    //我抓的数据只有一个release，所以返回值应该只有两个，一个是release前，一个是后
    @GetMapping("commitsBetweenRelease")
    public List<Integer> getCommitBetweenRelease() throws Exception {
        List<Releases> releases = repoServiceImpl.readReleases("D:\\CS209-project-22fall\\src\\main\\java\\data\\releases.json");
        List<Commits> commits = repoServiceImpl.readCommits("D:\\CS209-project-22fall\\src\\main\\java\\data\\commits.json");
        return repoServiceImpl.commitBetweenRelease(releases, commits);
    }
    //返回每一年的commit数量，从2008到2022，格式大概为{0,0,0,...,100}
    @GetMapping("commitByYear")
    public List<Integer> getCommitByYear() throws Exception {
        List<Commits> list = repoServiceImpl.readCommits("D:\\CS209-project-22fall\\src\\main\\java\\data\\commits.json");
        return repoServiceImpl.getCommitsByYear(list);
    }

}
