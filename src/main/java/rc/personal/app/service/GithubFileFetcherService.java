package rc.personal.app.service;


import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class GithubFileFetcherService {

    public static List<File> getJavaFilesFromGithub(String owner, String repository) throws IOException {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/contents", owner, repository);
        String response = FileUtils.readFileToString(new File("github_token.txt"), "UTF-8");
        String token = response.trim();
        List<File> javaFiles = new ArrayList<>();
        Collection<Object> files = new ArrayList<>();

        int i = 1;
        while (true) {
            String url = String.format("%s?page=%d", apiUrl, i);
            String fileResponse = fetchFiles(url, token);
            if (fileResponse == null || fileResponse.isEmpty()) {
                break;
            }
            files = JsonPath.parse(fileResponse).read("$[*]", Collection.class);
            for (Object f : files) {
                String type = JsonPath.parse(f).read("$.type");
                String path = JsonPath.parse(f).read("$.path");
                if (type.equals("file") && path.endsWith(".java")) {
                    String fileUrl = JsonPath.parse(f).read("$.url");
                    String javaCode = fetchJavaCode(fileUrl, token);
                    String fileName = path.substring(path.lastIndexOf('/') + 1);
                    FileUtils.writeStringToFile(new File(fileName), javaCode, "UTF-8");
                    javaFiles.add(new File(fileName));
                }
            }
            i++;
        }
        return javaFiles;
    }

    private static String fetchFiles(String url, String token) throws IOException {
        URL githubApiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) githubApiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    private static String fetchJavaCode(String url, String token) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Accept", "application/vnd.github.v3.raw");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }
}
