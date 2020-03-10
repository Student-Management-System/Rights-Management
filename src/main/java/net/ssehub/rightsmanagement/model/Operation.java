package net.ssehub.rightsmanagement.model;

import java.util.ArrayList;
import java.util.List;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.AssignmentsApi;
import io.swagger.client.api.CoursesApi;
import io.swagger.client.api.GroupsApi;
import io.swagger.client.api.UsersApi;
import io.swagger.client.model.AssignmentDto;
import io.swagger.client.model.CourseDto;
import io.swagger.client.model.GroupDto;
import io.swagger.client.model.UserDto;
import net.ssehub.rightsmanagement.svn.Repository;

public class Operation {
    
    private ApiClient apiClient;
    
    private AssignmentsApi apiAssignments;

    private GroupsApi apiGroups;
    
    private CoursesApi apiCourse;
    
    private UsersApi apiUser;
    
    private String basePath;
    
    private List<AssignmentDto> assignments = new ArrayList<>();
    
    private List<GroupDto> groups = new ArrayList<>();
    
    private List<UserDto> users = new ArrayList<>();
    
    private String courseName;
    
    private String courseId;
    
    // muss konfigurierbar seien -> properties/config datei (erstmal als platzhalter um keien fehler zu bekommen)
    private String semester;
    
    public Operation(String basePath, String courseName) {
        apiClient = new ApiClient();
        apiClient.setBasePath(basePath);
        apiAssignments = new AssignmentsApi(apiClient);
        apiGroups = new GroupsApi(apiClient);
        apiCourse = new CoursesApi(apiClient);
        apiUser = new UsersApi(apiClient);
        this.basePath = basePath;
        this.courseName = courseName;
    }
    
    
    /**
     * Getter for courseID. Requests the course Id from the API.
     * @return the course Id.
     */
    public String getCourseId() {
        if(courseId == null || courseId.isEmpty()) {
            
            try {
                CourseDto course = apiCourse.getCourseByNameAndSemester(courseName, semester);
                courseId = course.getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
                
        return courseId;
    }
    
    /**
     * Getter for assignments. Requests the assignments from the API.
     * @return list of assignments.
     */
    public List<AssignmentDto> getAssignment() {
        
        try {
            assignments = apiAssignments.getAssignmentsOfCourse(courseId);
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return assignments;
    }
    
    /**
     * Getter for groups. Requests the groups from the API.
     * @return list of groups.
     */
    public List<GroupDto> getGroups() {
        
        try {
            groups = apiGroups.getGroupsOfCourse(courseId);
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return groups;
    }
    
    /**
     * Getter for users. Requests the users from the API.
     * @return list of users.
     */
    public List<UserDto> getUsers() {
        
        try {
            users = apiUser.getAllUsers();
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * 
     */
    private void addData() {
        // unterscheidung in gruppen und einzelabgaben noch notwendig.
        Assignment ass = new Assignment();
        for (AssignmentDto assignment : assignments) {
            ass.setName(assignment.getName());
            
            if (true) { // Platzhalter bis unterscheidung in gruppen und einzelabgaben möglich ist
                Group g = new Group();
                for (GroupDto group : groups) {
                    g.addMembers(group.getName());
                }
                ass.addParticipant(g);  
            } else {
                Member member = new Member();
                for (UserDto user : users) {
                    // member = user.get                keine Namen aus dem User Dto holbar.
                }
                ass.addParticipant(member);
            }
            
            try {
                Repository repository = new Repository("/repository/abgabe");
                repository.createOrModifyAssignment(ass);
             } catch (Exception e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
             }
        }
        
    }
    
    
}
