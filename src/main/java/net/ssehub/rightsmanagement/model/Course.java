package net.ssehub.rightsmanagement.model;

import java.util.ArrayList;
import java.util.List;

import net.ssehub.exercisesubmitter.protocol.frontend.Group;
import net.ssehub.exercisesubmitter.protocol.frontend.ManagedAssignment;
import net.ssehub.exercisesubmitter.protocol.frontend.User;

/**
 * Stores the complete information of a course to configure a repository for the course.
 * 
 * @author El-Sharkawy
 */
public class Course {
    
    private String courseName;
    private String semester;
    
    private Group tutors;
    private List<User> students = new ArrayList<>();
    
    private List<ManagedAssignment> assignments = new ArrayList<>();

    /**
     * The name of the course.
     * @return The name of the course.
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * The semester of the managed course.
     * @return the semester
     */
    public String getSemester() {
        return semester;
    }

    /**
     * The group of tutors (LECTURERs and TUTORs) of the course.
     * @return the tutors
     */
    public Group getTutors() {
        return tutors;
    }
    
    /**
     * The students participating at the curse.
     * @return the students, shouldn't be <tt>null</tt>.
     */
    public List<User> getStudents() {
        return students;
    }
    
    /**
     * The assignments (homework, exams, ...) of the course.
     * @return the assignments
     */
    public List<ManagedAssignment> getAssignments() {
        return assignments;
    }

    /**
     * The name of the course.
     * @param courseName the courseName to set
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * The semester of the managed course.
     * @param semester the semester to set
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * The group of tutors (LECTURERs and TUTORs) of the course.
     * @param tutors the tutors to set
     */
    public void setTutors(Group tutors) {
        this.tutors = tutors;
    }

    /**
     * The students participating at the curse.
     * @param students the students (must not be <tt>null</tt>).
     */
    public void setStudents(List<User> students) {
        this.students = students;
    }

    /**
     * The assignments (homework, exams, ...) of the course.
     * @param assignments the assignments to set
     */
    public void setAssignments(List<ManagedAssignment> assignments) {
        this.assignments = assignments;
    }

    /**
     * Only intended for the Debugger: Returns a meaningful name during debugging.
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Course: " + getCourseName() + "-" + getSemester();
    }
}
