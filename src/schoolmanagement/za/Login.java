/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package schoolmanagement.za;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Donald
 */
public class Login extends JFrame{
    
    private JPanel mainpanel;
    private JPanel namepanel;
    private JPanel passwordpanel;
    private JPanel mixpanel;
    private JPanel headingpanel;
    private JPanel buttonpanel1;
    private JPanel buttonpanel2;
    private JPanel buttonspanel;
    
    private JPasswordField password;
    private JTextField name;
    
    private JButton register;
    private JButton enter;
    
    
    
    private JLabel namelabel;
    private JLabel passwordlabel;
    private JLabel headinglabel;
    public Login(){
        setTitle("Authentication");
      setSize(400,300);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
      setLocationRelativeTo(null); 
     
      mainpanel=new JPanel(new BorderLayout());
      namepanel=new JPanel(new FlowLayout());
      buttonpanel1=new JPanel(new FlowLayout());
      buttonpanel2=new JPanel(new FlowLayout());
      
       passwordpanel=new JPanel(new FlowLayout());
      buttonspanel=new JPanel(new GridLayout(1,2));
      mixpanel=new JPanel(new FlowLayout());
      
      
      headingpanel=new JPanel(new FlowLayout());
      
      namelabel=new JLabel("ID:                     ");
      passwordlabel=new JLabel("PASSWORD:  ");
       headinglabel=new JLabel("LOGIN");
       
       register=new JButton("register");
       enter=new JButton("enter");
      name=new JTextField(20);
      password=new JPasswordField(20);
      
      
      enter.addActionListener(e->{
      
        TeacherManagement management=new TeacherManagement();  
          
        String id=name.getText();
        String passwordn=new String(password.getPassword());
        boolean loginstatus=false;
        boolean idfound=false;
        ArrayList<Teacher>teachers=management.getAll();
        
      /** for(int i=0;i<teachers.size();i++){
            
            Teacher newTeacher=teachers.get(i);
            
            if(newTeacher.getTeacherId().equals(id)&&(!passwordn.isEmpty())){
                
              idfound=true;
                break;  
            }
            
        }**/
        
    
      
     if(passwordn.isEmpty()){
    
    
     JOptionPane.showMessageDialog(null, "invalid id or password is empty");
       

     }else {
         
          if(loginstatus=management.login(id, passwordn)){
             
             JOptionPane.showMessageDialog(null, "Login succesfull");
             
          new leanerGUI().setVisible(true);  
          dispose();
         }else{
          JOptionPane.showMessageDialog(null, "Invalid login");
         }    
         
     }
      
      
        
      
      
      });
      
      
      buttonpanel1.add(register);
        buttonpanel2.add(enter);
      
      buttonspanel.add(buttonpanel1);
       buttonspanel.add(buttonpanel2);
      
      headingpanel.add(headinglabel);
      namepanel.add(namelabel);
      namepanel.add(name);
      
      passwordpanel.add(passwordlabel);
       passwordpanel.add(password);
       
       
        mixpanel.add(passwordpanel);
       mixpanel.add(namepanel);
      
      
       mainpanel.add(headingpanel,BorderLayout.NORTH);
       mainpanel.add(mixpanel,BorderLayout.CENTER);
        mainpanel.add(buttonspanel,BorderLayout.SOUTH);
       add(mainpanel);
      setVisible(true);
        
    }
    
    
    
    
    
    
    
    
    
    
    
}
