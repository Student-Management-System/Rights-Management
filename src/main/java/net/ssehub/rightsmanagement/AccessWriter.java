package net.ssehub.rightsmanagement;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import io.swagger.client.model.AssignmentDto.StateEnum;
import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;
import net.ssehub.rightsmanagement.model.IParticipant;

/**
 * Writes the access file (containing access right set-up) for the svn.
 * 
 * @author Kunold
 * @author El-Sharkawy
 *
 */
public class AccessWriter implements Closeable {
    private static final String SECTION_START = "[";
    private static final String SECTION_END = "]";
    private static final String GROUP_SECTION = SECTION_START + "groups" + SECTION_END;
    private static final String ASSIGNMENT = " = ";
    private static final String LINE_BREAK = "\n";
    private static final String GROUP_PREFIX = "@";
    private static final String REPOSITORY_SEPARATOR = ":";
    private static final String PATH_SEPARATOR = "/";
    private static final String ALL_USER = "* = ";
    private static final String READ = "r";
    private static final String READ_WRITE = "rw";
    
    private Writer out;

    /**
     * Constructor of the AccessWriter class.
     * @param out the Writer.
     */
    public AccessWriter(Writer out) {
        this.out = out;
    }
    
    /**
     * Writes the groups to AccessWriter.
     * @throws IOException If an I/O error occurs during writing.
     */
    private void writeGroups(Course course) throws IOException {
        
        out.append(GROUP_SECTION);
        out.append(LINE_BREAK);
        
        // Write tutor groups first
        Group tutorGroup = course.getTutors();
        if (null != tutorGroup && !tutorGroup.getMembers().isEmpty()) {
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
        List<Group> groups = course.getHomeworkGroups();
        if (null != groups) {
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
    }
    
    private void writePath(String repositoryName, String... path) throws IOException {
        out.append(SECTION_START);
        out.append(repositoryName);
        out.append(REPOSITORY_SEPARATOR);
        out.append(PATH_SEPARATOR);
        
        if (null != path) {
            for (String segment : path) {
                out.append(segment);
                out.append(PATH_SEPARATOR);
            }
        }
        out.append(SECTION_END);
        out.append(LINE_BREAK);
    }
    
    /**
     * Writes the permissions per assignment and group.
     * @throws IOException If an I/O error occurs during writing.
     */
    private void writePermissions(Course course, String svnName) throws IOException {
        writePath(svnName);
        out.append(ALL_USER + READ);

        Group tutorGroup = course.getTutors();
        
        List<Assignment> assignments = course.getAssignments();
        if (assignments != null) {
            // iterates over every homework
            for (Assignment assignment : assignments) {
                
                String rights = "";
                if (assignment.getStatus() == StateEnum.IN_PROGRESS) {
                    rights = READ_WRITE;
                } else if (assignment.getStatus() == StateEnum.EVALUATED) {
                    rights = READ;
                }
                
                // iterates over every group
                for (IParticipant participant : assignment) {
                    out.append(LINE_BREAK);
                    out.append(LINE_BREAK);
                    writePath(svnName, assignment.getName(), participant.getName());
                    out.append(ALL_USER);
                    out.append(LINE_BREAK);
                    out.append(GROUP_PREFIX + tutorGroup.getName() + ASSIGNMENT + READ_WRITE);                        
                    out.append(LINE_BREAK);
                    if (participant instanceof Group) {
                        out.append(GROUP_PREFIX);
                    }
                    out.append(participant.getName() + ASSIGNMENT + rights);
                }
            }
            
        }
        
        out.append(LINE_BREAK);
    }
    
    /**
     * Writes the groups and the svn path.
     * @throws IOException If an I/O error occurs during writing.
     */
    public void write(Course course, String svnName) throws IOException {
        writeGroups(course);
        
        out.append(LINE_BREAK);
        
        writePermissions(course, svnName);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
