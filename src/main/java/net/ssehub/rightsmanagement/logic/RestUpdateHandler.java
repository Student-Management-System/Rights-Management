package net.ssehub.rightsmanagement.logic;

import java.util.ArrayList;
import java.util.List;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.GroupsApi;
import io.swagger.client.model.GroupDto;
import io.swagger.client.model.UpdateMessage;
import io.swagger.client.model.UserDto;
import net.ssehub.rightsmanagement.conf.Configuration.CourseConfiguration;
import net.ssehub.rightsmanagement.conf.Settings;
import net.ssehub.rightsmanagement.model.Course;
import net.ssehub.rightsmanagement.model.Group;

/**
 * Pulls with each update message the whole configuration from the student management system.
 * @author El-Sharkawy
 *
 */
public class RestUpdateHandler extends AbstractUpdateHandler {

    private GroupsApi groupsAPI;
    
    public RestUpdateHandler(CourseConfiguration courseConfig) {
        super(courseConfig);
        String url = Settings.getConfig().getMgmtURL();
        groupsAPI =  new GroupsApi(new ApiClient().setBasePath(url));
    }

    @Override
    protected Course computeFullConfiguration(UpdateMessage msg) {
        Course course = new Course();
        course.setCourseName(getConfig().getCourseName());
        course.setSemester(getConfig().getSemester());
        
        List<Group> homeworkGroups = new ArrayList<>();       
        try {
            List<GroupDto> groupsOfServer = groupsAPI.getGroupsOfCourse(getCourseID());
            for (GroupDto groupDto : groupsOfServer) {
                Group group = new Group();
                group.setGroupName(groupDto.getName());
                
                List<UserDto> userofGroup = groupsAPI.getUsersOfGroup(getCourseID(), groupDto.getId());
                for (UserDto userDto : userofGroup) {
                    group.addMembers(userDto.getId());
                }
            }
            course.setHomeworkGroups(homeworkGroups);
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return course;
    }

}
