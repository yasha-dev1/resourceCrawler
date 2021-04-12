import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceDownloader {
    private String basePath;
    private File downloadLocation;
    private List<String> pathSuffixes;

    public String getBasePath() {
        return basePath;
    }

    public File getDownloadLocation() {
        return downloadLocation;
    }

    public List<String> getPathSuffixes() {
        return pathSuffixes;
    }

    public void setBasePath(String basePath) throws MalformedURLException {
        this.basePath = new URL(basePath).toString();
    }

    public void setDownloadLocation(File downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    public void setDownloadLocation(String downloadLocation) throws IOException {
        File f = new File(downloadLocation);
        if (f.mkdirs() || f.exists())
            this.downloadLocation = new File(downloadLocation);
        else
            throw new IOException("Path couldn't be created");
    }

    public void setPathSuffixes(List<String> pathSuffixes) {
        this.pathSuffixes = pathSuffixes
                .stream()
                .map(d -> URLEncoder.encode(d, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(Collectors.toList());
    }

    public List<String> runCrawler() throws IOException {
        List<String> failedResources = new ArrayList<>();
        pathSuffixes.forEach(suffix -> {
            String url = basePath + "/" + suffix;
            try {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpGet request = new HttpGet(url);
                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        HttpEntity responseEntity = response.getEntity();
                        responseEntity.writeTo(new FileOutputStream(downloadLocation.getAbsolutePath() + "/" + suffix));
                        print("INFO", "Saved resource from url " + url + " successfully to base path - SUCCESS");
                    }
                }
            } catch (IOException e) {
                print("ERROR", "Couldn't fetch resource from url " + url + " - FAILED");
                failedResources.add(url);
            }
        });
        return failedResources;
    }

    private void print(String logType, String logMsg) {
        System.out.printf("[%1$s][%2$s] - %3$s", logType, new Date().toString(), logMsg);
        System.out.println("");
    }
}
