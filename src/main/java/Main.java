import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String args[]) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> pathSuffixes = new ArrayList<>();
        try {
            JsonNode techArray = mapper.readTree(new File("data/technologies.json"))
                    .get("technologies");
            for (final JsonNode tech : techArray) {
                if (tech.has("icon")) {
                    pathSuffixes.add(tech.get("icon").asText());
                }
            }
            ResourceDownloader downloader = new ResourceDownloader();
            downloader.setBasePath("https://www.wappalyzer.com/images/icons");
            downloader.setDownloadLocation("result");
            downloader.setPathSuffixes(pathSuffixes);
            List<String> result = downloader.runCrawler();
            System.out.println("DONE DOWNLOADING RESOURCES: ");
            System.out.println(result.size() + " urls resources couldn't be fetched: ");
            result.forEach(System.out::println);


        } catch (MalformedURLException e) {
            System.out.println("It seems the entered base path is not a correct url. are you sure that's a url? :");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error Occured:");
            e.printStackTrace();
        }
    }


}
