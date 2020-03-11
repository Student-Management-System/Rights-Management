package net.ssehub.rightsmanagement.model;

import java.util.List;

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
    
    private List<Assignment> assignments;

    /**
     * @return the courseName
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @param courseName the courseName to set
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * @return the semester
     */
    public String getSemester() {
        return semester;
    }

    /**
     * @param semester the semester to set
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * @return the tutors
     */
    public Group getTutors() {
        return tutors;
    }

    /**
     * @param tutors the tutors to set
     */
    public void setTutors(Group tutors) {
        this.tutors = tutors;
    }

    /**
     * @return the homeworkGroups
     */
    public List<Group> getHomeworkGroups() {
        return homeworkGroups;
    }

    /**
     * @param homeworkGroups the homeworkGroups to set
     */
    public void setHomeworkGroups(List<Group> homeworkGroups) {
        this.homeworkGroups = homeworkGroups;
    }

    /**
     * @return the assignments
     */
    public List<Assignment> getAssignments() {
        return assignments;
    }

    /**
     * @param assignments the assignments to set
     */
    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
    
    

}
