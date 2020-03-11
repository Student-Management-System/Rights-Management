package net.ssehub.rightsmanagement.rest.update;

import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;

/**
 * Handles the updates for <b>one</b> repository.
 * @author El-Sharkawy
 *
 */
class UpdateHandler {
    
    private CourseConfiguration courseConfig;
    
    /**
     * Creates a handler to manage updates for a course.
     * @param courseID The ID of the managed course.
     *     Must be the same ID as send by the student management service as part of the {@link UpdateMessage}s.
     *     Must not be <tt>null</tt>.
     */
    UpdateHandler(CourseConfiguration courseConfig) {
        this.courseConfig = courseConfig;
    }
    
    public void update(UpdateMessage msg) {
        
    }

    /**
     * Returns the ID of the managed course.
     * @return The ID of the course, won't be <tt>null</tt>.
     */
    public String getCourseID() {
        return courseConfig.getCourseName() + "-" + courseConfig.getSemester();
    }
}
