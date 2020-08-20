package net.ssehub.rightsmanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Assertions;

import net.ssehub.studentmgmt.backend_api.JSON;
import net.ssehub.studentmgmt.backend_api.model.NotificationDto;

/**
 * Loads an {@link NotificationDto} for testing, which was saved locally as JSON. 
 * @author El-Sharkawy
 *
 */
public class UpdateMessageLoader {
    
    private static final File TEST_FOLDER = new File(AllTests.TEST_FOLDER, "UpdateMessages");

    /**
     * Loads an {@link NotificationDto} for testing, which was saved locally as JSON. 
     * @param jsonFile The jsonFile inside <tt>src/test/resource</tt>.
     * @return The parsed {@link NotificationDto}, otherwise the whole test will be aborted.
     */
    public static NotificationDto load(File jsonFile) {
        String content = null;
        try {
            content = Files.readString(jsonFile.toPath());
        } catch (IOException e) {
            Assertions.fail("Could not read contents from " + jsonFile.getAbsolutePath(), e);
        }
        NotificationDto update = new JSON().deserialize(content, NotificationDto.class);
        Assertions.assertNotNull(update, "Could not parse file " + jsonFile.getAbsolutePath());
        Assertions.assertNotNull(update.getEvent(), "Could not parse file " + jsonFile.getAbsolutePath());
        Assertions.assertNotNull(update.getCourseId(), "Could not parse file " + jsonFile.getAbsolutePath());
        return update;
    }
    
    /**
     * Loads an {@link NotificationDto} for testing, which was saved locally as JSON. 
     * @param fileName The name of the JSON file to load from the default test data folder of {@link NotificationDto}s.
     * @return The parsed {@link NotificationDto}, otherwise the whole test will be aborted.
     */
    public static NotificationDto load(String fileName) {
        return load(new File(TEST_FOLDER, fileName));
    }
}
