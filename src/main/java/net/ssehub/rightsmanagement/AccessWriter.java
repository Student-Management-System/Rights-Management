package net.ssehub.rightsmanagement;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
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
    private static final String RIGHTS_ASSIGNMENT = " = ";
    private static final String LINE_BREAK = System.lineSeparator();
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
     * @param course The set-up of the course, which should be reflected in the repository
     * @throws IOException If an I/O error occurs during writing.
     */
    private void writeGroups(Course course) throws IOException {
        
        out.append(GROUP_SECTION);
        out.append(LINE_BREAK);
        
        // Write tutor groups first
        Group tutorGroup = course.getTutors();
        if (null != tutorGroup && !tutorGroup.getMembers().isEmpty()) {
            out.append(tutorGroup.getName());
            out.append(RIGHTS_ASSIGNMENT);
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
        
    }
    
    /**
     * Writes a Path section to the access file, which can be configured.<p>
     * This will be:
     * <tt>repository:path</tt>
     * @param repositoryName The name of the repository
     * @param path The full path inside the repository.
     * @throws IOException If an I/O error occurs
     */
    private void writePath(String repositoryName, String... path) throws IOException {
        out.append(SECTION_START);
        out.append(repositoryName);
        out.append(REPOSITORY_SEPARATOR);
        out.append(PATH_SEPARATOR);
        
        if (null != path && path.length > 0) {
            for (int i = 0; i < path.length - 1; i++) {
                out.append(path[i]);
                out.append(PATH_SEPARATOR);                
            }
            
            /* 
             * Apache web server will deny all paths, if they end with an /
             * Also example doesn't show ending slashes:
             * https://svn.apache.org/repos/asf/subversion/trunk/subversion/mod_authz_svn/INSTALL
             */        
            out.append(path[path.length - 1]);
        }
        out.append(SECTION_END);
        out.append(LINE_BREAK);
    }
    
    /**
     * Writes the permissions per assignment and group.
     * @param course The set-up of the course, which should be reflected in the repository
     * @param svnName The name of the repository as used inside the URL.
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
                if (assignment.getState() == State.SUBMISSION) {
                    rights = READ_WRITE;
                } else if (assignment.getState() == State.REVIEWED) {
                    rights = READ;
                }
                
                // iterates over every group
                for (IParticipant participant : assignment) {
                    out.append(LINE_BREAK);
                    out.append(LINE_BREAK);
                    writePath(svnName, assignment.getName(), participant.getName());
                    out.append(ALL_USER);
                    out.append(LINE_BREAK);
                    // Avoid writing an undefined group this will yield in an illegal access file
                    if (null != tutorGroup && !tutorGroup.getMembers().isEmpty()) {
                        out.append(GROUP_PREFIX);                        
                        out.append(tutorGroup.getName());                        
                        out.append(RIGHTS_ASSIGNMENT + READ_WRITE);                        
                        out.append(LINE_BREAK);
                    }
                    if (participant instanceof Group && assignment.isGroupWork()) { 
                        Group group = (Group) participant;
                        boolean isFirst = true;
                        for (String member : group) {
                            out.append(member + RIGHTS_ASSIGNMENT + rights);
                            if (isFirst) {
                                out.append(LINE_BREAK);
                                isFirst = false;
                            }
                        } 
                    } else {
                        out.append(participant.getName() + RIGHTS_ASSIGNMENT + rights);
                    }
                }
            }
            
        }
        
        out.append(LINE_BREAK);
    }
    
    /**
     * Writes the groups and the svn path.
     * @param course The set-up of the course, which should be reflected in the repository
     * @param svnName The name of the repository as used inside the URL.
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
