package tk.hackspace;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;


/**
 * Installer.
 * Created by Anatoliy on 07.07.2015.
 */
@RunWith(JUnit4.class)
public class SetupServerIP {
    private static final Logger log = Logger.getLogger(SetupServerIP.class);
    private String sb ="; CouchDB Configuration Settings\n" +
            "\n" +
            "; Custom settings should be made in this file. They will override settings\n" +
            "; in default.ini, but unlike changes made to default.ini, this file won't be\n" +
            "; overwritten on server upgrade.\n" +
            "\n" +
            "[couchdb]\n" +
            ";max_document_size = 4294967296 ; bytes\n" +
            "uuid = d948b80e000b04f70ad2c92431f770c3\n" +
            "\n" +
            "[httpd]\n" +
            ";port = 5984\n" +
            ";bind_address = 127.0.0.1\n" +
            "; Options for the MochiWeb HTTP server.\n" +
            ";server_options = [{backlog, 128}, {acceptor_pool_size, 16}]\n" +
            "; For more socket options, consult Erlang's module 'inet' man page.\n" +
            ";socket_options = [{recbuf, 262144}, {sndbuf, 262144}, {nodelay, true}]\n" +
            "\n" +
            "; Uncomment next line to trigger basic-auth popup on unauthorized requests.\n" +
            ";WWW-Authenticate = Basic realm=\"administrator\"\n" +
            "\n" +
            "; Uncomment next line to set the configuration modification whitelist. Only\n" +
            "; whitelisted values may be changed via the /_config URLs. To allow the admin\n" +
            "; to change this value over HTTP, remember to include {httpd,config_whitelist}\n" +
            "; itself. Excluding it from the list would require editing this file to update\n" +
            "; the whitelist.\n" +
            ";config_whitelist = [{httpd,config_whitelist}, {log,level}, {etc,etc}]\n" +
            "bind_address = 192.168.1.2\n" +
            "\n" +
            "[query_servers]\n" +
            ";nodejs = /usr/local/bin/couchjs-node /path/to/couchdb/share/server/main.js\n" +
            "\n" +
            "\n" +
            "[httpd_global_handlers]\n" +
            ";_google = {couch_httpd_proxy, handle_proxy_req, <<\"http://www.google.com\">>}\n" +
            "\n" +
            "[couch_httpd_auth]\n" +
            "; If you set this to true, you should also uncomment the WWW-Authenticate line\n" +
            "; above. If you don't configure a WWW-Authenticate header, CouchDB will send\n" +
            "; Basic realm=\"server\" in order to prevent you getting logged out.\n" +
            "; require_valid_user = false\n" +
            "\n" +
            "[log]\n" +
            ";level = debug\n" +
            "\n" +
            "[log_level_by_module]\n" +
            "; In this section you can specify any of the four log levels 'none', 'info',\n" +
            "; 'error' or 'debug' on a per-module basis. See src/*/*.erl for various\n" +
            "; modules.\n" +
            ";couch_httpd = error\n" +
            "\n" +
            "\n" +
            "[os_daemons]\n" +
            "; For any commands listed here, CouchDB will attempt to ensure that\n" +
            "; the process remains alive. Daemons should monitor their environment\n" +
            "; to know when to exit. This can most easily be accomplished by exiting\n" +
            "; when stdin is closed.\n" +
            ";foo = /path/to/command -with args\n" +
            "\n" +
            "[daemons]\n" +
            "; enable SSL support by uncommenting the following line and supply the PEM's below.\n" +
            "; the default ssl port CouchDB listens on is 6984\n" +
            "; httpsd = {couch_httpd, start_link, [https]}\n" +
            "\n" +
            "[ssl]\n" +
            ";cert_file = /full/path/to/server_cert.pem\n" +
            ";key_file = /full/path/to/server_key.pem\n" +
            ";password = somepassword\n" +
            "; set to true to validate peer certificates\n" +
            "verify_ssl_certificates = false\n" +
            "; Path to file containing PEM encoded CA certificates (trusted\n" +
            "; certificates used for verifying a peer certificate). May be omitted if\n" +
            "; you do not want to verify the peer.\n" +
            ";cacert_file = /full/path/to/cacertf\n" +
            "; The verification fun (optional) if not specified, the default\n" +
            "; verification fun will be used.\n" +
            ";verify_fun = {Module, VerifyFun}\n" +
            "; maximum peer certificate depth\n" +
            "ssl_certificate_max_depth = 1\n" +
            "\n" +
            "; To enable Virtual Hosts in CouchDB, add a vhost = path directive. All requests to\n" +
            "; the Virual Host will be redirected to the path. In the example below all requests\n" +
            "; to http://example.com/ are redirected to /database.\n" +
            "; If you run CouchDB on a specific port, include the port number in the vhost:\n" +
            "; example.com:5984 = /database\n" +
            "[vhosts]\n" +
            ";example.com = /database/\n" +
            "\n" +
            "[update_notification]\n" +
            ";unique notifier name=/full/path/to/exe -with \"cmd line arg\"\n" +
            "\n" +
            "; To create an admin account uncomment the '[admins]' section below and add a\n" +
            "; line in the format 'username = password'. When you next start CouchDB, it\n" +
            "; will change the password to a hash (so that your passwords don't linger\n" +
            "; around in plain-text files). You can add more admin accounts with more\n" +
            "; 'username = password' lines. Don't forget to restart CouchDB after\n" +
            "; changing this.\n" +
            "[admins]\n" +
            ";admin = mysecretpassword\n";

    String expected = "; CouchDB Configuration Settings\n" +
            "\n" +
            "; Custom settings should be made in this file. They will override settings\n" +
            "; in default.ini, but unlike changes made to default.ini, this file won't be\n" +
            "; overwritten on server upgrade.\n" +
            "\n" +
            "[couchdb]\n" +
            ";max_document_size = 4294967296 ; bytes\n" +
            "uuid = d948b80e000b04f70ad2c92431f770c3\n" +
            "\n" +
            "[httpd]\n" +
            ";port = 5984\n" +
            ";bind_address = 192.168.1.8\n" +
            "; Options for the MochiWeb HTTP server.\n" +
            ";server_options = [{backlog, 128}, {acceptor_pool_size, 16}]\n" +
            "; For more socket options, consult Erlang's module 'inet' man page.\n" +
            ";socket_options = [{recbuf, 262144}, {sndbuf, 262144}, {nodelay, true}]\n" +
            "\n" +
            "; Uncomment next line to trigger basic-auth popup on unauthorized requests.\n" +
            ";WWW-Authenticate = Basic realm=\"administrator\"\n" +
            "\n" +
            "; Uncomment next line to set the configuration modification whitelist. Only\n" +
            "; whitelisted values may be changed via the /_config URLs. To allow the admin\n" +
            "; to change this value over HTTP, remember to include {httpd,config_whitelist}\n" +
            "; itself. Excluding it from the list would require editing this file to update\n" +
            "; the whitelist.\n" +
            ";config_whitelist = [{httpd,config_whitelist}, {log,level}, {etc,etc}]\n" +
            "bind_address = 192.168.1.8\n" +
            "\n" +
            "[query_servers]\n" +
            ";nodejs = /usr/local/bin/couchjs-node /path/to/couchdb/share/server/main.js\n" +
            "\n" +
            "\n" +
            "[httpd_global_handlers]\n" +
            ";_google = {couch_httpd_proxy, handle_proxy_req, <<\"http://www.google.com\">>}\n" +
            "\n" +
            "[couch_httpd_auth]\n" +
            "; If you set this to true, you should also uncomment the WWW-Authenticate line\n" +
            "; above. If you don't configure a WWW-Authenticate header, CouchDB will send\n" +
            "; Basic realm=\"server\" in order to prevent you getting logged out.\n" +
            "; require_valid_user = false\n" +
            "\n" +
            "[log]\n" +
            ";level = debug\n" +
            "\n" +
            "[log_level_by_module]\n" +
            "; In this section you can specify any of the four log levels 'none', 'info',\n" +
            "; 'error' or 'debug' on a per-module basis. See src/*/*.erl for various\n" +
            "; modules.\n" +
            ";couch_httpd = error\n" +
            "\n" +
            "\n" +
            "[os_daemons]\n" +
            "; For any commands listed here, CouchDB will attempt to ensure that\n" +
            "; the process remains alive. Daemons should monitor their environment\n" +
            "; to know when to exit. This can most easily be accomplished by exiting\n" +
            "; when stdin is closed.\n" +
            ";foo = /path/to/command -with args\n" +
            "\n" +
            "[daemons]\n" +
            "; enable SSL support by uncommenting the following line and supply the PEM's below.\n" +
            "; the default ssl port CouchDB listens on is 6984\n" +
            "; httpsd = {couch_httpd, start_link, [https]}\n" +
            "\n" +
            "[ssl]\n" +
            ";cert_file = /full/path/to/server_cert.pem\n" +
            ";key_file = /full/path/to/server_key.pem\n" +
            ";password = somepassword\n" +
            "; set to true to validate peer certificates\n" +
            "verify_ssl_certificates = false\n" +
            "; Path to file containing PEM encoded CA certificates (trusted\n" +
            "; certificates used for verifying a peer certificate). May be omitted if\n" +
            "; you do not want to verify the peer.\n" +
            ";cacert_file = /full/path/to/cacertf\n" +
            "; The verification fun (optional) if not specified, the default\n" +
            "; verification fun will be used.\n" +
            ";verify_fun = {Module, VerifyFun}\n" +
            "; maximum peer certificate depth\n" +
            "ssl_certificate_max_depth = 1\n" +
            "\n" +
            "; To enable Virtual Hosts in CouchDB, add a vhost = path directive. All requests to\n" +
            "; the Virual Host will be redirected to the path. In the example below all requests\n" +
            "; to http://example.com/ are redirected to /database.\n" +
            "; If you run CouchDB on a specific port, include the port number in the vhost:\n" +
            "; example.com:5984 = /database\n" +
            "[vhosts]\n" +
            ";example.com = /database/\n" +
            "\n" +
            "[update_notification]\n" +
            ";unique notifier name=/full/path/to/exe -with \"cmd line arg\"\n" +
            "\n" +
            "; To create an admin account uncomment the '[admins]' section below and add a\n" +
            "; line in the format 'username = password'. When you next start CouchDB, it\n" +
            "; will change the password to a hash (so that your passwords don't linger\n" +
            "; around in plain-text files). You can add more admin accounts with more\n" +
            "; 'username = password' lines. Don't forget to restart CouchDB after\n" +
            "; changing this.\n" +
            "[admins]\n" +
            ";admin = mysecretpassword\n";

    @Test
    public void testFindBindAddress() throws Exception {
        Pattern pattern = Pattern.compile("bind_address = .+");
        String resultString = pattern.matcher(sb).replaceAll(String.format("bind_address = %s", "192.168.1.8"));
        log.debug(resultString);
        assertEquals("String must be equals",expected,resultString);

    }
}
