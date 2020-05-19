package net.ssehub.rightsmanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Assertions;

import net.ssehub.studentmgmt.backend_api.JSON;
import net.ssehub.studentmgmt.backend_api.model.UpdateMessage;

/**
 * Loads an {@link UpdateMessage} for testing, which was saved locally as JSON. 
 * @author El-Sharkawy
 *
 */
public class UpdateMessageLoader {
    
    private static final File TEST_FOLDER = new File(AllTests.TEST_FOLDER, "UpdateMessages");

    /**
     * Loads an {@link UpdateMessage} for testing, which was saved locally as JSON. 
     * @param jsonFile The jsonFile inside <tt>src/test/resource</tt>.
     * @return The parsed {@link UpdateMessage}, otherwise the whole test will be aborted.
     */
    public static UpdateMessage load(File jsonFile) {
        String content = null;
        try {
            content = Files.readString(jsonFile.toPath());
        } catch (IOException e) {
            Assertions.fail("Could not read contents from " + jsonFile.getAbsolutePath(), e);
        }
        UpdateMessage update = new JSON().deserialize(content, UpdateMessage.class);
        Assertions.assertNotNull(update, "Could not parse file " + jsonFile.getAbsolutePath());
        return update;
    }
    
    /**
     * Loads an {@link UpdateMessage} for testing, which was saved locally as JSON. 
     * @param fileName The name of the JSON file to load from the default test data folder of {@link UpdateMessage}s.
     * @return The parsed {@link UpdateMessage}, otherwise the whole test will be aborted.
     */
    public static UpdateMessage load(String fileName) {
        return load(new File(TEST_FOLDER, fileName));
    }
}
