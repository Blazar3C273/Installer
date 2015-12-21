package tk.hackspace;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * Installer.
 * Created by Anatoliy on 07.07.2015.
 */
public class SyncPipe implements Runnable {
    private static final Logger log = Logger.getLogger(SyncPipe.class);

    public SyncPipe(InputStream istrm, OutputStream ostrm) {
        istrm_ = istrm;
        ostrm_ = ostrm;
    }
    public void run() {
        try {
            final byte[] buffer = new byte[1024];
            for (int length = 0; (length = istrm_.read(buffer)) != -1; ) {
                ostrm_.write(buffer, 0, length);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private final OutputStream ostrm_;
    private final InputStream istrm_;
}
