package net.ssehub.rightsmanagement;

import java.util.HashSet;
import java.util.Set;

import io.swagger.client.model.UpdateMessage;
import net.ssehub.rightsmanagement.conf.Settings;

/**
 * Observer that specifies how to react on changes at the student management system.
 * @author El-Sharkawy
 *
 */
public class StudentManagementChangeListener {
    
    public static final StudentManagementChangeListener INSTANCE = new StudentManagementChangeListener();
    private Set<String> observedCourses = new HashSet<>();
    
    /**
     * Singleton constructor
     */
    private StudentManagementChangeListener() {
        observedCourses.add(Settings.INSTANCE.get("mgmtsystem.course")
              + "-" + Settings.INSTANCE.get("mgmtsystem.semester"));
    }
    
    public boolean onChange(UpdateMessage update) {
        return observedCourses.contains(update.getCourseId());
    }

}
