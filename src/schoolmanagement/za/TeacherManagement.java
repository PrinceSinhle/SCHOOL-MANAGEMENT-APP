
package schoolmanagement.za;

/**
 *
 * @author Donald
 */


import java.util.ArrayList;

public class TeacherManagement {
    private ArrayList<Teacher> teachers;
    private TeacherDatabase database;
    
    public TeacherManagement() {
        this.teachers = new ArrayList<>();
        this.database = new TeacherDatabase("jdbc:derby://localhost:1527/tableDB");
    }
    
   public boolean remove(String id){
       
       boolean status=database.deleteTeacher(id);
       
       
       
      return status;
   }
    public boolean addTeacher(Teacher teacher) {
        teachers.add(teacher);
        return database.addTeacher(teacher);
    }
    
    public ArrayList<Teacher> getAll(){
        
        ArrayList<Teacher>teachers=new ArrayList<>();
        
    teachers= database.getAll();
        
        return teachers;
    }
   
    
    
    public boolean login(String teacherId,String password) {
        
        
        boolean status=false;
        
      Teacher  newteacher=database.authenticateTeacher(teacherId);
      if(newteacher!=null){
      if(newteacher.getPassword().equals(password)){
          status=true;  
         
          
        }
      }
     return status;
    }
    
   
    public boolean canManageLearners(String teacherId) {
        Teacher teacher = database.getTeacher(teacherId);
        if (teacher != null) {
            String role = teacher.getRole();
            return role.equals("admin") || role.equals("principal");
        }
        return false;
    }
}