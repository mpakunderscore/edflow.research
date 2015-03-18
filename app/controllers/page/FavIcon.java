package controllers.page;

//import com.amazonaws.services.s3.model.CannedAccessControlList;
//import com.amazonaws.services.s3.model.PutObjectRequest;
import models.Page;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import play.Logger;
//import plugins.S3Plugin;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

/**
 * Created by pavelkuzmin on 24/04/14.
 */
public class FavIcon {

    public static String produce(String url, Document doc) {

        String domainString = Page.getDomainString(url);

        String protocol = url.split("://")[0];

        String domainStringWithProtocol =  protocol + "://" + domainString; //TODO !!!

        Logger.debug("[produce domain favicon] " + domainString);

        String favIconFormat = null;

        String link = null;

//        Set<String> favIconUrls = new HashSet<String>();

        Elements links = doc.head().select("link[rel~=(^(shortcut )?icon$)]");

        if (links.size() > 0) {

            link = links.first().attr("href").split(Pattern.quote("?"))[0];

            Logger.debug("[found favicon link] " + link);

            if (link.startsWith("//"))
                favIconFormat = check(protocol + ":" + link, domainString);

            else if (link.startsWith("/"))
                favIconFormat = check(domainStringWithProtocol + link, domainString);

            else if (link.startsWith("http"))
                favIconFormat = check(link, domainString);

            else
                favIconFormat = check(domainStringWithProtocol + "/" + link, domainString);

        } else
            favIconFormat = check(domainStringWithProtocol + "/favicon.ico", domainString);

        return favIconFormat;
    }

    public static String check(String favIconUrl, String domainString) {

        String format;

        try {

            String[] bits = favIconUrl.split(Pattern.quote("."));
            format = bits[bits.length-1]; //TODO without format

            File file = copyFileFromWeb(favIconUrl, domainString + "." + format);

            if (file.length() == 0)
                return null;

//            if (S3Plugin.amazonS3 == null) {
//
//                Logger.debug("[amazonS3 is null]");
//
//                throw
//                        new RuntimeException("Could not save");
//
//            } else {
//
//                PutObjectRequest putObjectRequest = new PutObjectRequest(S3Plugin.s3Bucket, "favicons/" + domainString + "." + format, file);
//
//                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead); // public for all
//                S3Plugin.amazonS3.putObject(putObjectRequest); // upload file
//            }

        } catch (MalformedURLException e) {
            return null;
        }

        return format;
    }

    public static File copyFileFromWeb(String address, String filePath) throws MalformedURLException {

        Logger.debug("[download favicon] " + address);

        byte[] buffer = new byte[1024];
        int bytesRead;

        URL url = new URL(address);
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;

        URLConnection connection = null;
        try {


            connection = url.openConnection();
            connection.setReadTimeout(15000);

            connection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            connection.addRequestProperty("User-Agent", "Mozilla");
            connection.addRequestProperty("Referer", "https://dry-tundra-9556.herokuapp.com");

            inputStream = new BufferedInputStream(connection.getInputStream());
            File file = new File(filePath);
            outputStream = new BufferedOutputStream(new FileOutputStream(file));

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return file;

        } catch (IOException e) {
            Logger.error("[can't get favicon] " + address + " [" + e.getMessage() + "]");
        }

        return null;
    }
}
