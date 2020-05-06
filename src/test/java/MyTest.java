import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MyTest {
    private InMemoryDirectoryServer ds;

    @Before
    public void setup() {
        // Create the configuration to use for the server.
        InMemoryDirectoryServerConfig config =
                null;
        try {
            config = new InMemoryDirectoryServerConfig("o=UT");
            InMemoryListenerConfig listenerConfig = InMemoryListenerConfig.createLDAPConfig("test", 389);
            config.setListenerConfigs(listenerConfig);
            config.addAdditionalBindCredentials("cn=Directory Manager", "password");
            this.ds = new InMemoryDirectoryServer(config);
            this.ds.applyChangesFromLDIF("src/test/resources/base-schema.ldif");
            this.ds.importFromLDIF(true, "src/test/resources/test-data.ldif");
            this.ds.startListening("test");
        } catch (LDAPException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        // Get a client connection to the server and use it to perform various
        // operations.
        LDAPConnection conn = null;
        try {
            conn = new LDAPConnection("localhost", 389,"cn=Directory Manager","password");
            SearchResultEntry entry = conn.getEntry("uid=jsmith1,ou=People,o=UT");
            assertNotNull(entry);
            assertEquals("John Smith", entry.getAttributeValue("cn"));
        } catch (LDAPException e) {
            e.printStackTrace();
        }

        // Do more stuff here....

        // Disconnect from the server and cause the server to shut down.
        conn.close();
    }

    @After
    public void cleanup() {
        ds.shutDown(true);
    }
}
