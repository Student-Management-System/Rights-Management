package net.ssehub.rightsmanagement.update;

import io.swagger.client.model.UpdateMessage;

/**
 * Handles the updates for <b>one</b> repository.
 * @author El-Sharkawy
 *
 */
class UpdateHandler {
    
    private String courseID;
    
    UpdateHandler(String courseID) {
        this.courseID = courseID;
    }
    
    public void update(UpdateMessage msg) {
        
    }

}
