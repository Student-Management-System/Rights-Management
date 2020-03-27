package net.ssehub.rightsmanagement.model;

import java.util.List;
import java.util.Map;

/**
 * Stores the complete information of a course to configure a repository for the course. 
 * @author El-Sharkawy
 *
 */
public class Course {
    
    private String courseName;
    private String semester;
    
    private Group tutors;
    private List<Group> homeworkGroups;
    private Map<String, Member> students;
    
    private List<Assignment> assignments;

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
     * The homework groups of the course.
     * @return the homeworkGroups
     */
    public List<Group> getHomeworkGroups() {
        return homeworkGroups;
    }

    /**
     * The students participating at the curse.
     * @return the students (ID as used in the management system, student), maybe <tt>null</tt>.
     */
    public Map<String, Member> getStudents() {
        return students;
    }
    
    /**
     * The assignments (homework, exams, ...) of the course.
     * @return the assignments
     */
    public List<Assignment> getAssignments() {
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
     * The homework groups of the course.
     * @param homeworkGroups the homeworkGroups to set
     */
    public void setHomeworkGroups(List<Group> homeworkGroups) {
        this.homeworkGroups = homeworkGroups;
    }
    
    /**
     * The students participating at the curse.
     * @param students the students (ID as used in the management system, student)
     */
    public void setStudents(Map<String, Member> students) {
        this.students = students;
    }

    /**
     * The assignments (homework, exams, ...) of the course.
     * @param assignments the assignments to set
     */
    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    /**
     * Only intended for the Debugger: Returns a meaningful name during debugging.
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getCourseName() + "-" + getSemester();
    }
}
