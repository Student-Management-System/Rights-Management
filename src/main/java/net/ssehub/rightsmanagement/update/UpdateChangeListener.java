package net.ssehub.rightsmanagement.update;

import java.util.HashMap;
import java.util.Map;

import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.conf.Settings;

/**
 * Observer that specifies how to react on changes at the student management system.
 * @author El-Sharkawy
 *
 */
public class UpdateChangeListener {
    
    public static final UpdateChangeListener INSTANCE = new UpdateChangeListener();
    
    private Map<String, UpdateHandler> observedCourses = new HashMap<>();
    
    /**
     * Singleton constructor
     */
    private UpdateChangeListener() {
        String courseID = Settings.INSTANCE.get("mgmtsystem.course") 
            + "-" + Settings.INSTANCE.get("mgmtsystem.semester");
        
        observedCourses.put(courseID, new UpdateHandler(courseID));
    }
    
    /**
     * Handles the incoming update request and selects the {@link UpdateHandler} for the specified repository.
     * Will throw an exception if a course was specified that is not managed by the RightsManagement service.
     * @param update The update message received by the student management system.
     * @throws WrongFormatException If the update message points not to a managed course / repository.
     */
    public void onChange(UpdateMessage update) throws WrongFormatException {
        // Not ensured that mandatory fields are set, the JSON parser will accept also empty mandatory fields!
        if (null == update.getCourseId()) {
            throw new WrongFormatException("No course specified");
        }
        
        UpdateHandler handler = observedCourses.get(update.getCourseId());
        if (null == handler) {
            throw new WrongFormatException(update.getCourseId() + " not managed by this service.");
        }
        
        handler.update(update);
    }

}
