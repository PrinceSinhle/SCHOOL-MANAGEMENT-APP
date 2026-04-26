/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package schoolmanagement.za;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Donald
 */
public interface LeanerManagementInterface <T>{
    
    boolean    addLeaner(T t);
    ArrayList<T> getAll();
    
boolean delete(T t); 
  boolean updateLeaner(String t,T e);  
    
    
}
