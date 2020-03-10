package net.ssehub.rightsmanagement;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.AssignmentStates;
import net.ssehub.rightsmanagement.model.Group;

/**
 * Writes the accessess for the svn.
 * 
 * @author Kunold
 *
 */
public class AccessWriter {
    private static final String GROUP_SECTION = "[groups]";
    private static final String ASSIGNMENT = " = ";
    private static final String LINE_BREAK = "\n";
    private static final String AT = "@";
    private static final String BASE_PATH = "[abgabe:/";
    private static final String SLASH = "/";
    private static final String BRACE = "]";
    private static final String ALL_USER = "* = ";
    private static final String READ = "r";
    private static final String READ_WRITE = "rw";
    
    private List<Group> groups = new ArrayList<>();
    
    private Group tutorGroup;
    
    private List<Assignment> assignments = new ArrayList<>();
    
    private Writer out;

    /**
     * Constructor of the AccessWriter class.
     * @param out the Writer.
     */
    public AccessWriter(Writer out) {
        this.out = out;
    }
    
    /**
     * Adds a group to the groups List.
     * @param group that is added to the groups List.
     */
    public void addGroup(Group group) {
        this.groups.add(group);
    }
    
    /**
     * Getter for the tutor group.
     * @return the tutor group.
     */
    public Group getTutorGroup() {
        return tutorGroup;
    }
    
    /**
     * Adds a group for the tutors.
     * @param group of the tutors.
     */
    public void addTutorGroup(Group group) {
        this.tutorGroup = group;
    }
    
    /**
     * Adds a homework to the homeworks List.
     * @param homework that is added to the groups List.
     */
    public void addHomework(Assignment homework) {
        this.assignments.add(homework);
    }
    
    /**
     * Writes the groups to AccessWriter.
     * @throws IOException If an I/O error occurs during writing.
     */
    private void writeGroups() throws IOException {
        
        out.append(GROUP_SECTION);
        out.append(LINE_BREAK);
        
        // Write tutor groups first
        if (null != tutorGroup) {
            out.append(tutorGroup.getName());
            out.append(ASSIGNMENT);
            boolean isFirst = true;
            for (String member : tutorGroup) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    out.append(", ");
                }
                out.append(member);
            }  

            out.append(LINE_BREAK);
        }
        
        // Write course member groups
        for (Group group : groups) {
            out.append(group.getName());
            out.append(ASSIGNMENT);
            boolean isFirst = true;
            for (String member : group) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    out.append(", ");
                }
                out.append(member);
            }  

            out.append(LINE_BREAK);
        }
    }
    
    /**
     * Writes the permissions per assignment and group.
     * @throws IOException If an I/O error occurs during writing.
     */
    private void writePermissions() throws IOException {
        out.append(BASE_PATH + BRACE);
        out.append(LINE_BREAK);
        out.append(ALL_USER + READ);

        if (assignments != null) {
            // iterates over every homework
            for (Assignment assignment : assignments) {
                
                String rights = "";
                if (assignment.getStatus() == AssignmentStates.IN_PROGRESS) {
                    rights = READ_WRITE;
                } else if (assignment.getStatus() == AssignmentStates.EVALUATED) {
                    rights = READ;
                }
                
                // iterates over every group
                for (Group group : groups) {
                    out.append(LINE_BREAK);
                    out.append(LINE_BREAK);
                    out.append(BASE_PATH + assignment.getName() + SLASH + group.getName() + BRACE);
                    out.append(LINE_BREAK);
                    out.append(ALL_USER);
                    out.append(LINE_BREAK);
                    out.append(AT + tutorGroup.getName() + ASSIGNMENT + READ_WRITE);
                    out.append(LINE_BREAK);
                    out.append(AT + group.getName() + ASSIGNMENT + rights);
                }
            }
            
        }
        
        out.append(LINE_BREAK);
    }
    
    /**
     * Writes the groups and the svn path.
     * @throws IOException If an I/O error occurs during writing.
     */
    public void write() throws IOException {
        writeGroups();
        
        out.append(LINE_BREAK);
        
        writePermissions();
    }
}
