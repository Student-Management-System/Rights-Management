package net.ssehub.rightsmanagement.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.IParticipant;
import net.ssehub.rightsmanagement.rest.resources.UpdateCallback;


/**
 * This class manages the SVN repository and creates the folders if needed.
 * 
 * @author El-Sharkawy
 * @author Kunold
 *
 */
public class Repository {
    
    private static final int LATEST_REVISION = -1;
    
    private static final Logger LOGGER = LogManager.getLogger(UpdateCallback.class);
    
    private File file;
    
    private String author;
    
    /**
     * Constructor of Repository.
     * @param path to the repository.
     * @param author Optional an author name to set when changes are applied to the repository.
     *     Will be ignored if <tt>null</tt> or an empty string.
     * @throws RepositoryNotFoundException if the repository is not found.
     */
    public Repository(String path, String author) throws RepositoryNotFoundException {
        file = new File(path);
        this.author = author;
        if (!file.exists()) {
            throw new RepositoryNotFoundException(file.getAbsolutePath() + " does not point to a repository location.");
        }
        if (!file.isDirectory()) {
            throw new RepositoryNotFoundException(file.getAbsolutePath()
                + " does not point to a repository directory.");
        }
        
    }
    
    /**
     * Loads the repository.
     * @return the loaded repository.
     * @throws SVNException If there's no implementation for the specified protocol (the user may have forgotten to
     *     register a specific factory that creates <b>SVNRepository</b> instances for that protocol or the SVNKit
     *     library does not support that protocol at all)
     */
    private SVNRepository loadRepository() throws SVNException {   
        FSRepositoryFactory.setup();
        return SVNRepositoryFactory.create(SVNURL.fromFile(file));
    }
    
    
    /**
     * Checks if a specified path in the repository exists.
     * @param path that is checked for existence.
     * @param readOnlyConnection builds a connection only to read..
     * @return <tt>true</tt> if the path exists and <tt>false</tt> if the path doesn`t exist.
     * @throws SVNException if a failure occurred while connecting to a repository or the user's authentication failed
     *     (see {@link org.tmatesoft.svn.core.SVNAuthenticationException})
     */
    private boolean pathExists(SVNRepository readOnlyConnection, String path) throws SVNException {
        boolean exists = true;
        SVNNodeKind nodeKind = readOnlyConnection.checkPath(path , LATEST_REVISION);
        if (nodeKind == SVNNodeKind.NONE) {
            exists = false;
        }
        
        return exists;
    }
    
    /**
     * Checks if a specified path in the repository exists.
     * Only intended for testing purpose.
     * @param path  that is checked for existence.
     * @return <tt>true</tt> if the path exists and <tt>false</tt> if the path doesn`t exist.
     * @throws SVNException If a failure occurred while connecting to a repository or the user's authentication failed
     *     (see {@link org.tmatesoft.svn.core.SVNAuthenticationException})
     */
    boolean pathExists(String path) throws SVNException {
        SVNRepository readOnlyConnection = loadRepository();
        boolean result = pathExists(readOnlyConnection, path);
        readOnlyConnection.closeSession();
        return result;
    }
    
    /**
     * Returns the Latest revision of the repository.
     * Only intended for testing purpose to check if repository has changed.
     * @return The latest revision number
     * @throws SVNException If a failure occurred while connecting to a repository or the user's authentication failed
     *     (see {@link org.tmatesoft.svn.core.SVNAuthenticationException})
     */
    long lastRevision() throws SVNException {
        SVNRepository readOnlyConnection = loadRepository();
        long lastRevision = readOnlyConnection.getLatestRevision();
        readOnlyConnection.closeSession();
        return lastRevision;
    }
    
    /**
     * Creates folders for assignment and groups if the assignment folder doesn`t exist yet.
     * @param updateAssignment <tt>true</tt> if an existing assignment shall be updated, <tt>false</tt>otherwise.
     * @param assignment The name of the assignment that is created/updated.
     * @param groups The name of the participants for which sub folders shall be created.
     * @throws SVNException If a failure occurred while connecting to a repository
     */
    private void createFolders(boolean updateAssignment, String assignment, String... groups) throws SVNException {
        SVNRepository con = loadRepository();
        try {
            String msg = updateAssignment ? "Update " + assignment : "Initialize " + assignment;
            SVNProperties commitProperties = new SVNProperties();
            if (null != author && !author.isEmpty()) {
                commitProperties.put(SVNRevisionProperty.AUTHOR, author);
            }
            ISVNEditor svnEditor = con.getCommitEditor(msg, null, false, commitProperties, null);
            
            LOGGER.debug("Open root");
            svnEditor.openRoot(LATEST_REVISION);
          
            // We checked that assignment already exists -> No need to re-create it
            if (!updateAssignment) {
                LOGGER.debug("Create folder for assignment: {}", assignment);
                svnEditor.addDir(assignment, null, LATEST_REVISION);
                svnEditor.closeDir();
            }
            
            // Add missing groups inside of assignment
            LOGGER.debug("Create group folders for assignment: {}", assignment);
            for (int i = 0; i < groups.length; i++) {
                svnEditor.addDir(toPath(assignment, groups[i]), null, LATEST_REVISION);         
                svnEditor.closeDir();
            }
            LOGGER.debug("Write delta");
            svnEditor.closeEdit();
        } finally {
            con.closeSession();
        }
        LOGGER.debug("Finished");
    }
    
    /**
     * Creates the absolute path to an submission folder for an assignment.
     * @param assignmentName The name of the assignment
     * @param participantName The name of the participant (group name for group assignments or user name for
     * single user assignments)
     * @return <tt>assignmentName / participantName)</tt>
     */
    private String toPath(String assignmentName, String participantName) {
        return assignmentName + "/" + participantName;
    }
    
    /**
     * Checks if for an assignment and the groups that belongs to the assignment, already exists folder or if they need
     * to be created.
     * @param assignment is checked if there already exists a folder for it or if a folder must be created.
     * folders need to be created.
     * @throws SVNException If a failure occurred while connecting to a repository
     */
    public void createOrModifyAssignment(Assignment assignment) throws SVNException {
        SVNRepository repos = loadRepository();
        try {
            List<String> newSubmisionFolders = new ArrayList<>();
            if (pathExists(repos, assignment.getName())) {
                // If folders exists do nothing
                LOGGER.debug("Folder of assignment \"{}\" already existing", assignment.getName());
                for (IParticipant member : assignment) {
                    if (!pathExists(repos, toPath(assignment.getName(), member.getName()))) {
                        newSubmisionFolders.add(member.getName());
                    }
                }
                repos.closeSession();
                // only create folders if newGroups is not empty
                if (!newSubmisionFolders.isEmpty()) {
                    String[] groupArray = newSubmisionFolders.toArray(new String[0]);
                    createFolders(true, assignment.getName(), groupArray);
                }
            } else {
                // If folders doesn`t exists create folders for assignment and groups
                createFolders(false, assignment.getName(), assignment.getParticipants());
            }
        } finally {
            repos.closeSession();
        }
    }
}
