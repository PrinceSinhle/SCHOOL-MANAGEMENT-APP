/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package schoolmanagement.za;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Donald
 */
public class leanerDatabase implements LeanerManagementInterface<Leaner> {

    
     private Connection connection;

    public leanerDatabase(String url) {
        
        try {
            connection=DriverManager.getConnection(url);
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }
    }
    
    
    
    @Override
    public boolean addLeaner(Leaner t){
        
         String sql="insert into Leaner(name, id,contact,grade) "+
                "values(?,?,?,?)";
        
        try {
            PreparedStatement ps=connection.prepareStatement(sql);
            
           ps.setString(1,t.getFullname());
          ps.setString(2,t.getId());
          ps.setString(3,t.getContact());
          ps.setInt(4, t.getGrade());
          
           int result= ps.executeUpdate();
           
           if(result>0){
               
               return true;
               
           }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
       return true; 
        
    }

    

    @Override
    public boolean delete(Leaner t) {
        
        String sql ="delete from Leaner "+
                "where id=?";
        
        try {
            PreparedStatement ps=connection.prepareStatement(sql);
            ps.setString(1,t.getId());
          int result=  ps.executeUpdate();
            
                
               return result>0; 
            
          
          
        } catch (SQLException ex) {
            
             System.out.print(ex.getMessage());
            return false;
        }
  
    }
    
     @Override
    public ArrayList<Leaner> getAll(){
        
        ArrayList<Leaner>newlist=new ArrayList<>();
       String sql="select*"+"from Leaner";
         try {
            PreparedStatement ps=connection.prepareStatement(sql);
            
        ResultSet rs=ps.executeQuery();
            
               while(rs.next()){
                   
                  String name=rs.getString("name");
                   String id=rs.getString("id");
                   String contact=rs.getString("contact");
                   Integer grade=rs.getInt("grade");
                   
                   Leaner newLeaner=new Leaner(name,grade,id,contact);
                   newlist.add(newLeaner);
                   
               } 
             
            
          
          
        } catch (SQLException ex) {
            
             System.out.print(ex.getMessage());
         
        }
  
        
        
      return newlist;  
    }
    
   
     @Override
    public boolean updateLeaner(String id, Leaner updatedLeaner) {
        String sql = "UPDATE Leaner SET name=?, contact=?, grade=? WHERE id=?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            
            ps.setString(1, updatedLeaner.getFullname());
            ps.setString(2, updatedLeaner.getContact());
            ps.setInt(3, updatedLeaner.getGrade());
            ps.setString(4, id); 
            
            int result = ps.executeUpdate();
            
            return result > 0;
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
    
 
    
    
}