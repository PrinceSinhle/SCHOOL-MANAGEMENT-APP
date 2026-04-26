/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package schoolmanagement.za;

/**
 *
 * @author Donald
 */

import java.sql.*;
import java.util.ArrayList;

public class TeacherDatabase {
    private Connection connection;
    
    public TeacherDatabase(String url) {
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public boolean addTeacher(Teacher teacher) {
        String sql = "INSERT INTO Teacher (teacher_id, full_name, phone_number, " +
                     "qualification, password, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, teacher.getTeacherId());
            ps.setString(2, teacher.getFullName());
         
            ps.setString(3, teacher.getPhoneNumber());
            ps.setString(4, teacher.getQualification());
        
            ps.setString(5, teacher.getPassword());
            ps.setString(6, teacher.getRole());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
    
    public Teacher authenticateTeacher(String teacherId) {
        String sql = "SELECT * FROM Teacher WHERE teacher_id=?";
        
        String trimedid=teacherId.trim();
        Teacher teacher=null;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, trimedid);
          
            
            
            ResultSet rs = ps.executeQuery();
           
            if (rs.next()) {
                String teacherid=rs.getString("teacher_id");
                String name=rs.getString("full_name");
                 String number=rs.getString("phone_number");
                 String qualification=rs.getString("qualification");
                  String password=rs.getString("password");
                   String role=rs.getString("role");
                
                 teacher=new Teacher(teacherid,name,number,qualification,password,role);
                
            }

           //  return teacher;
            
            
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            
        }
       return teacher;
    }
    
 public Teacher getTeacher(String id){
     

     String sql="select teacher_id,full_name,phone_number,qualification,password,role from Teacher"
             + "where teacher_id=?";
      try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,id);
         
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String teacherid=rs.getString("teacher_id");
                String name=rs.getString("full_name");
                 String number=rs.getString("phone_number");
                 String qualification=rs.getString("qualification");
                  String password=rs.getString("password");
                   String role=rs.getString("role");
                
                  Teacher teacher=new Teacher(teacherid,name,number,qualification,password,role);
                   return teacher;
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
       return null;
    }
public ArrayList<Teacher> getAll(){
    
    String sql="select* from Teacher";
    ArrayList<Teacher> teachers=new ArrayList<>();
      try {
            PreparedStatement ps = connection.prepareStatement(sql);
          
         
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String teacherid=rs.getString("teacher_id");
                String name=rs.getString("full_name");
                 String number=rs.getString("phone_number");
                 String qualification=rs.getString("qualification");
                  String password=rs.getString("password");
                   String role=rs.getString("role");
                
                  Teacher teacher=new Teacher(teacherid,name,number,qualification,password,role);
                  teachers.add(teacher);
                  
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        return teachers; 
}

public boolean deleteTeacher (String id){
    
    String sql="delete from Teacher "+
             "where teacher_id=?";
      try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,id);
         
            
            int status= ps.executeUpdate();
        
            
                
               return status>0; 
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
  

    
}





 }

