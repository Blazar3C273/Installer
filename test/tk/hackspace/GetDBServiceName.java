package tk.hackspace;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;


/**
 * Installer.
 * Created by Anatoliy on 07.07.2015.
 */
@RunWith(JUnit4.class)
public class GetDBServiceName {
    private static final Logger log = Logger.getLogger(GetDBServiceName.class);

    @Test
    public void testGettingName() throws Exception {
        String name= Main.getDBServiceName();
        assertEquals("Apache CouchDB01d0b851ffeae3f0",name);
    }

    @Test
    public void testgetRev() throws Exception {
        String string = NetworkConnection.getApkRev();
        assertEquals("6-7ebfab112a66c3f293cf0f8f405e8480",string);

    }
}
