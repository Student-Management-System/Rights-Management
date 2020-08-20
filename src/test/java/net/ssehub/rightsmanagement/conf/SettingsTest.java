package net.ssehub.rightsmanagement.conf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link Settings} class.
 * @author El-Sharkawy
 *
 */
public class SettingsTest {


    /**
     * Tests parsing/reading of REST server specific settings.
     */
    @Test
    public void testReadRestServerConfiguration() {
        int expectedPort = 314159;
        // Test of valid precondition: A different port is used, which will be changed during test
        Assertions.assertNotEquals(expectedPort, Settings.getConfig().getRestPort());
        
        String json = "{\"restServerPort\": " + expectedPort + "}";
        Settings.INSTANCE.loadConfig(json);
        
        // At the moment we have only one server configuration, add others here
        Assertions.assertEquals(expectedPort, Settings.getConfig().getRestPort());
    }
}
