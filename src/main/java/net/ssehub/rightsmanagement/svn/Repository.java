package net.ssehub.rightsmanagement.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import net.ssehub.rightsmanagement.model.Assignment;
import net.ssehub.rightsmanagement.model.IParticipant;


/**
 * This class manages the svn repository and creates the folders if needed.
 * 
 * @author Kunold
 * @author El-Sharkawy
 *
 */
public class Repository {
    
    private static final int LATEST_REVISION = -1;
    
    private static final Logger LOGGER = Log.getLog();
    
    private File file;
    
    /**
     * Constructor of Repository.
     * @param path to the repository.
     * @throws RepositoryNotFoundException if the repository is not found.
     */
    public Repository(String path) throws RepositoryNotFoundException {
        file = new File(path);
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
     * @throws SVNException .
     */
    private SVNRepository loadRepository() throws SVNException {   
        FSRepositoryFactory.setup();
        return SVNRepositoryFactory.create(SVNURL.fromFile(file));
    }
    
    
    /**
     * Checks if a specified path in the repository exists.
     * @param path that is checked for existence.
     * @param readOnlyConnection builds a connection only to read..
     * @return true if the path exists and false if the path doesn`t exist.
     * @throws SVNException .
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
     * Creates folders for assignment and groups if the assignment folder doesn`t exist yet.
     * @param updateAssignment is true if not needed to update and is false if needed to update. 
     * @param assignment that is created.
     * @param groups that are created.
     * @throws Exception .
     */
    private void createFolders(boolean updateAssignment, String assignment, String... groups) throws Exception {
        SVNRepository con = loadRepository();
        String msg = updateAssignment ? "Update " + assignment : "Initialize " + assignment;
        ISVNEditor svnEditor = con.getCommitEditor(msg, null, false, null);
        
        LOGGER.debug("Open root");
        svnEditor.openRoot(LATEST_REVISION);
      
        // We checked that assignment already exists -> No need to re-create it
        if (!updateAssignment) {
            LOGGER.debug("Folder for assignment: {}", assignment);
            svnEditor.addDir(assignment, null, LATEST_REVISION);
        }
        
        // Add missing groups inside of assignment
        LOGGER.debug("Create group folders for assignment: {}", assignment);
        for (int i = 0; i < groups.length; i++) {
            svnEditor.addDir(assignment + "/" + groups[i], null, LATEST_REVISION);            
        }
        LOGGER.debug("Write delta");
        svnEditor.closeDir();
        svnEditor.closeEdit();
        con.closeSession();
        LOGGER.debug("Finished");
    }
    
    /**
     * Checks if for an assignment and the groups that belongs to the assignment, already exists folder or if they need
     * to be created.
     * @param assignment is checked if there already exists a folder for it or if a folder must be created.
     * @param groups that belongs to a assignment and it`s checked if for the groups already exist folders or if the 
     * folders need to be created.
     * @throws Exception .
     */
    public void createOrModifyAssignment(Assignment assignment) throws Exception {
        SVNRepository repos = loadRepository();
        try {
            List<String> newGroups = new ArrayList<>();
            if (pathExists(repos, assignment.getName())) {
                // If folders exists do nothing
                LOGGER.debug("Folder of assignment \"{}\" already existing", assignment);
                for (IParticipant member : assignment) {
                    if (!pathExists(repos, assignment + "/" + member.getName())) {
                        newGroups.add(member.getName());
                    }
                }
                // only create folders if newGroups is not empty
                if (!newGroups.isEmpty()) {
                    String[] groupArray = newGroups.toArray(new String[0]);
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
