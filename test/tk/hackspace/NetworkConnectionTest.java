package tk.hackspace;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

/** 
* NetworkConnection Tester. 
* 
* @author <Authors name> 
* @since <pre>èþë 9, 2015</pre> 
* @version 1.0 
*/ @RunWith(JUnit4.class)
public class NetworkConnectionTest {
    private static final Logger  log = Logger.getLogger(NetworkConnectionTest.class);

@Before
public void before() throws Exception {
    NetworkConnection.setServerURL("http://192.168.1.8:5984/");
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: setServerURL(String uri) 
* 
*/ 
@Test
public void testSetServerURL() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getGetRequest(String uri, Header[] headers) 
* 
*/ 
@Test
public void testGetGetRequest() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getPutRequest(@NotNull String uri, HttpEntity entity, Header[] headers) 
* 
*/ 
@Test
public void testGetPutRequest() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: sentRequest(HttpUriRequest request) 
* 
*/ 
@Test
public void testSentRequest() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getAnswerAsString(HttpResponse response) 
* 
*/ 
@Test
public void testGetAnswerAsString() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: isServerOnline() 
* 
*/ 
@Test
public void testIsServerOnline() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: putFileByItem(File file, String dbName) 
* 
*/ 
@Test
public void testPutFileByItem() throws Exception {
    final String apk_name = "TGLIAMZ Mobile guide.apk";
    final String WGET_PATH = Main.HOME_DIR + "\\res\\wget\\bin\\";

    assertTrue(NetworkConnection.isServerOnline());
    Path expectedFile = Main.newAPKFile;
    String from ="http://192.168.1.8:5984/mobile_app/apk/TGLIAMZ%20Mobile%20guide.apk";
    NetworkConnection.putFileByItem(Paths.get(Main.HOME_DIR+"\\"+apk_name).toFile(),"mobile_app");
    Files.deleteIfExists(Paths.get(WGET_PATH + apk_name));
    ArrayList<String> commands = new ArrayList<>();
    commands.addAll(Arrays.asList("cmd", "cd " + WGET_PATH,"wget "+from));
    Path fileFromServer = Paths.get(WGET_PATH + apk_name);
    Main.execCmd(commands);

    String expectedFileMD5 = getMD5(expectedFile);
    String fileFromServerMD5 = getMD5(fileFromServer);
    log.info("ex="+expectedFileMD5+" server="+fileFromServerMD5);
    assertEquals(expectedFileMD5, fileFromServerMD5);
    //assertTrue("files must be the same.", Files.isSameFile(expectedFile, fileFromServer));
}

    private String getMD5(Path expectedFile) {
        byte[] data = new byte[0];
        MessageDigest md = null;
        try {
            data = Files.readAllBytes(expectedFile);
            md = MessageDigest.getInstance("MD5");
        } catch (IOException | NoSuchAlgorithmException e) {
           log.error(e);
        }
        byte[] digest = md.digest(data);
        return DatatypeConverter.printHexBinary(digest);
    }

    /**
* 
* Method: getApkRev() 
* 
*/ 
@Test
public void testGetApkRev() throws Exception { 
//TODO: Test goes here... 
} 


} 
