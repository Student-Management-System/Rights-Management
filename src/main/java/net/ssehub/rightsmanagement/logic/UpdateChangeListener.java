package net.ssehub.rightsmanagement.logic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;

/**
 * Observer that specifies how to react on changes at the student management system.
 * @author El-Sharkawy
 *
 */
public class UpdateChangeListener {
    
    public static final UpdateChangeListener INSTANCE = new UpdateChangeListener();
    
    private Map<String, AbstractUpdateHandler> observedCourses = new HashMap<>();
    
    /**
     * Singleton constructor
     */
    private UpdateChangeListener() { }
    
    /**
     * Registers an {@link AbstractUpdateHandler} for a managed course.
     * @param handler An {@link AbstractUpdateHandler} for course managed by this service.
     */
    public void register(AbstractUpdateHandler handler) {
        observedCourses.put(handler.getCourseID(), handler);
    }
    
    /**
     * Handles the incoming update request and selects the {@link AbstractUpdateHandler} for the specified repository.
     * Will throw an exception if a course was specified that is not managed by the RightsManagement service.
     * @param update The update message received by the student management system.
     * @throws WrongFormatException If the update message points not to a managed course / repository.
     * @throws IOException If the update message couldn't be written to disk
     */
    public void onChange(UpdateMessage update) throws WrongFormatException, IOException {
        // Not ensured that mandatory fields are set, the JSON parser will accept also empty mandatory fields!
        if (null == update.getCourseId()) {
            throw new WrongFormatException("No course specified");
        }
        
        AbstractUpdateHandler handler = observedCourses.get(update.getCourseId());
        if (null == handler) {
            throw new WrongFormatException(update.getCourseId() + " not managed by this service.");
        }
        
        handler.update(update);
    }
    
    /**
     * Factory method to create an {@link AbstractUpdateHandler} for the specified configuration.<p>
     * <b>Note:</b> This won't register the handler.
     * @param config The configuration of the course specifying which course is handled.
     * @param pullFullconfigOnChange <tt>true</tt> handler will always pull the full configuration on an update,
     *     <tt>false</tt> the handler will operate on local cache.
     * @return The handler to handle updates.
     * @throws IOException If caching was specified and the required file cannot be created and does not exist.
     * @see {@link #register(AbstractUpdateHandler)}
     */
    public AbstractUpdateHandler createHandler(CourseConfiguration config, boolean pullFullconfigOnChange) throws IOException {
        return pullFullconfigOnChange ? new RestUpdateHandler(config) : new IncrementalUpdateHandler(config);
    }

}