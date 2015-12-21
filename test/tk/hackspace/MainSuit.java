package tk.hackspace;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Installer.
 * Created by Anatoliy on 05.07.2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
//        InteringIP.class,
//        InstallingDB.class,
//        SetupStructure.class,
        SetupServerIP.class,
//        SetupAdminPassword.class,
        UnZipTest.class
        ,GetDBServiceName.class
        ,CmdArgsTest.class
})
public class MainSuit {
    private static final Logger log = Logger.getLogger(MainSuit.class);

}
