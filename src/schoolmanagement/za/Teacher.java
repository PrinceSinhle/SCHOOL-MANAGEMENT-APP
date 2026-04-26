/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package schoolmanagement.za;

/**
 *
 * @author Donald
 */
    
   

public class Teacher {
    private String teacherId;
    private String fullName;
    private String phoneNumber;
    private String qualification;
    private String password; 
    private String role; 
    
    public Teacher() {
    }
    
    public Teacher(String teacherId, String fullName, String phoneNumber, 
                   String qualification, String password, String role) {
        this.teacherId = teacherId;
        this.fullName = fullName;
      
        this.phoneNumber = phoneNumber;
        this.qualification = qualification;
        this.password = password;
        this.role = role;
    }
    
    // Getters and Setters
    public String getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
   
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    
  
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "Teacher{" + "teacherId=" + teacherId + ", fullName=" + fullName + 
               "phoneNumber=" + phoneNumber + 
               ", qualification=" + qualification +
               ", role=" + role + '}';
    }

    
    
    
    
    
}
