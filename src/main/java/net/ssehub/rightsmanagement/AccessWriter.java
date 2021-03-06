package net.ssehub.rightsmanagement;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import net.ssehub.exercisesubmitter.protocol.frontend.Assignment.State;
import net.ssehub.exercisesubmitter.protocol.frontend.Group;
import net.ssehub.exercisesubmitter.protocol.frontend.ManagedAssignment;
import net.ssehub.exercisesubmitter.protocol.frontend.User;
import net.ssehub.rightsmanagement.model.Course;

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
    private void writeTutorGroup(Course course) throws IOException {
        
        out.append(GROUP_SECTION);
        out.append(LINE_BREAK);
        
        // Write tutor groups first
        Group tutorGroup = course.getTutors();
        if (null != tutorGroup && !tutorGroup.getMembers().isEmpty()) {
            out.append(tutorGroup.getName());
            out.append(RIGHTS_ASSIGNMENT);
            boolean isFirst = true;
            for (User member : tutorGroup) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    out.append(", ");
                }
                out.append(member.getAccountName());
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
        List<ManagedAssignment> assignments = course.getAssignments();
        
        if (assignments != null) {
            // iterates over every homework
            for (ManagedAssignment assignment : assignments) {                
                String rights = "";
                if (assignment.getState() == State.SUBMISSION) {
                    rights = READ_WRITE;
                } else if (assignment.getState() == State.REVIEWED) {
                    rights = READ;
                }
                
                // iterates over every group
                for (Group group : assignment) {
                    out.append(LINE_BREAK);
                    out.append(LINE_BREAK);
                    writePath(svnName, assignment.getName(), group.getName());
                    out.append(ALL_USER);
                    out.append(LINE_BREAK);
                    // Avoid writing an undefined group this will yield in an illegal access file
                    if (null != tutorGroup && !tutorGroup.getMembers().isEmpty()) {
                        out.append(GROUP_PREFIX);                        
                        out.append(tutorGroup.getName());                        
                        out.append(RIGHTS_ASSIGNMENT + READ_WRITE);                        
                        out.append(LINE_BREAK);
                    }
                    for (User member : group) {
                        out.append(member.getAccountName());
                        out.append(RIGHTS_ASSIGNMENT);
                        out.append(rights);
                        out.append(LINE_BREAK);
                    } 
                }
            }
            
        }
    }
    
    /**
     * Writes the groups and the svn path.
     * @param course The set-up of the course, which should be reflected in the repository
     * @param svnName The name of the repository as used inside the URL.
     * @param blacklistedFolders Optional: If not <tt>null</tt> or empty this list of folders will be hidden to all
     *     users except for the tutors. Should be used to hide deprecated folders.
     * @throws IOException If an I/O error occurs during writing.
     */
    public void write(Course course, String svnName, Collection<String> blacklistedFolders) throws IOException {
        writeTutorGroup(course);
        
        out.append(LINE_BREAK);
        writePermissions(course, svnName);
        
        if (null != blacklistedFolders && !blacklistedFolders.isEmpty()) {
            Group tutorGroup = course.getTutors();
            out.append(LINE_BREAK);
            
            for (String folder : blacklistedFolders) {
                writePath(svnName, folder);
                out.append(ALL_USER);
                out.append(LINE_BREAK);
                if (null != tutorGroup && !tutorGroup.getMembers().isEmpty()) {
                    out.append(GROUP_PREFIX);                        
                    out.append(tutorGroup.getName());                        
                    out.append(RIGHTS_ASSIGNMENT + READ_WRITE);                        
                    out.append(LINE_BREAK);
                }
                out.append(LINE_BREAK);
            }
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
