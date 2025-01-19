package mindpath.security.utility;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FirebaseStorageUtils {

    public static String extractPathFromUrl(String fileUrl) throws MalformedURLException {
        if (fileUrl.contains("https://devfoknje7ik.com:8081")) {
            int blobNameIndex = fileUrl.indexOf("blobName=");
            return fileUrl.substring(blobNameIndex + 9);
        }

        URL url = new URL(fileUrl);
        String fullPath = url.getPath();
        String decodedPath = URLDecoder.decode(fullPath, StandardCharsets.UTF_8);

        if (decodedPath.contains("/foknje7ikblob/")) {
            decodedPath = decodedPath.replace("/foknje7ikblob/", "");
        }

        return decodedPath;
    }
}