package tk.hackspace;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Installer.
 * Created by Anatoliy on 09.07.2015.
 */
@RunWith(JUnit4.class)
public class CmdArgsTest {
    private static final Logger log = Logger.getLogger(CmdArgsTest.class);

    @Before
    public void setUp() throws Exception {


    }

    @Test
    public void testh() throws Exception {
        Main.main(new String[]{"-h"});
    }
    @Test
    public void testDb()throws Exception {
        Main.main(new String[]{"-db"});
    }
    @Test
    public void testip()throws Exception {
        Main.main(new String[]{"-ip"});
    }
    @Test
    public void testwipe()throws Exception {
        Main.main(new String[]{"-wipe"});
    }
    @Test
    public void testapk()throws Exception {
        Main.main(new String[]{"-apk"});
    }
    @Test
    public void testqed()throws Exception {
        Main.main(new String[]{"-qed"});
    }
    @Test
    public void testmn()throws Exception {
        Main.main(new String[]{"-mn"});
    }
    @Test
    public void testinvalidinput()throws Exception {
        Main.main(new String[]{"-"});
    }
    @Test
    public void testinvalidinput2()throws Exception {
        Main.main(new String[]{"-oaneuthaosentuh"});
    }
}
