package tk.hackspace;

import com.sun.istack.internal.NotNull;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Tolik on 30.11.2014.
 */
public class NetworkConnection {
    private static final String APK_DIR = "apk";
    static private String myUri = "http://127.0.0.1:5984/";
    static private DbUrlFactory dbUrlFactory = new DbUrlFactory(myUri);
    private static final Logger LOG = Logger.getLogger(NetworkConnection.class);

    /**
     * @param uri Url with port. By default "http://127.0.0.1:5984/". Slash at the end is required.
     */
    public static String setServerURL(String uri) {
        myUri = uri;
        dbUrlFactory = new DbUrlFactory(myUri);
        return myUri;
    }

    public static HttpUriRequest getGetRequest(String uri, Header[] headers) {
        HttpGet request = new HttpGet(URI.create(uri));
        if (headers != null) {
            request.setHeaders(headers);
        }
        return request;
    }

    public static HttpUriRequest getPutRequest(@NotNull String uri, HttpEntity entity, Header[] headers) {
        HttpPut request = new HttpPut(URI.create(uri));
        if (entity != null) {
            request.setEntity(entity);
        }
        if (headers != null) {
            request.setHeaders(headers);
        }
        return request;
    }

    public static HttpResponse sentRequest(HttpUriRequest request) throws IOException {
        HttpResponse response = null;

        CloseableHttpClient client = HttpClients.createDefault();
        System.out.println(new Date().toString());
        System.out.println(request);
        response = client.execute(request);
        System.out.println(request.getRequestLine().toString());
        return response;
    }

    public static String getAnswerAsString(HttpResponse response) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
        } catch (IOException e) {
            LOG.error(e);
        }
        return result;
    }

    public static boolean isServerOnline() {
        HttpResponse response;
        try {
            response = sentRequest(getGetRequest(myUri, null));
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            String string = result.toString();
            System.out.println(string);
            return true;
        } catch (IOException e) {
            if (e.getCause().getClass().equals(ConnectException.class))
                System.out.println(e.getMessage());
            else
                e.printStackTrace();
        }
        return false;
    }

    public static String putFileByItem(File file, String dbName){
        Header[] headers = new Header[3];
        String answer = "";
        String putFileURI = dbUrlFactory.getPutFileURI("mobile_app", "apk", file.getName());
        try {
            String contentType = "default/binary";
            LOG.debug("contentType:"+"");
            headers[0] = new BasicHeader("Accept","application/json");
            headers[1] = new BasicHeader(MIME.CONTENT_TYPE, contentType);
            headers[2] = new BasicHeader("If-Match", getApkRev());

            HttpEntity entity = new FileEntity(file);

            answer = getAnswerAsString(sentRequest(getPutRequest(putFileURI, entity, headers)));

            System.out.println(answer);

            if (((JSONObject) new JSONParser().parse(answer)).containsKey("error")) {
                LOG.error("Ошибка отправки файла на сервер.");
            }
            //item.set_rev((String) ((JSONObject) new JSONParser().parse(answer)).get("rev"));

        } catch (IOException | ParseException e) {
            LOG.error(e);
        }
        return putFileURI;
    }

    public static String getApkRev() {
        try {
            HttpHead httpHead = new HttpHead(dbUrlFactory.getItemURL("apk", "mobile_app"));
            return sentRequest(httpHead).getHeaders("ETag")[0].getValue().replace("\"","");
        } catch (IOException e) {
            LOG.error(e);
        }
        return null;
    }

    public static class DataBaseError extends Throwable {
        public DataBaseError(String s) {
            super(s);
        }
    }

}
