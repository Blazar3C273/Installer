package tk.hackspace;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;


/**
 * Installer.
 * Created by Anatoliy on 06.07.2015.
 */
@RunWith(JUnit4.class)
public class UnZipTest {
    private static final Logger log = Logger.getLogger(UnZipTest.class);
    private String  testZipFile = "/res/var.zip";
    private Path expectedFolder;
    final String zipFileName = System.getProperty("user.dir").replace('\\', '/').substring(2) + testZipFile;

    @Test
    public void testUnzip() throws Exception {
        Main.unzip(zipFileName,Paths.get("/output"));
    }

    @Test
    public void testlink() throws Exception {


                Path file1 = Paths.get(zipFileName);
                log.info(file1.toRealPath());
                Path hLink = Paths.get("test1.hLink");
                Path sLink = Paths.get("test1.symLink");

                try{
                    Files.createSymbolicLink(sLink, file1);

                }catch(UnsupportedOperationException ex){
                    log.info("This OS doesn't support creating Sym links");
                }
                try{
                    Files.createLink(hLink, file1);
                    log.info(hLink.toRealPath());
                }catch(UnsupportedOperationException ex){
                    log.info("This OS doesn't support creating Sym links");
                }


    }
}
