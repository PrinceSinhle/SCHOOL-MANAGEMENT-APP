/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package schoolmanagement.za;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Donald
 */
public class TeacherGui extends JFrame {
    
    private JPanel mainpanel;
    private JPanel namepanel;
    private JPanel idpanel;
    private JPanel passwordpanel;
    private JPanel phonenumberpanel;
    private JPanel rolepanel;
    private JPanel detailspanel;
    private JPanel comboboxpanel;
    private JPanel qualificationPanel;
    private JPanel detailsAndqualificationPanel;
    private JPanel addButtonPanel;
    private JPanel updateButtonPanel;
    private JPanel deleteButtonPanel;
    private JPanel resetButtonPanel;
    private JPanel loadButtonPanel;
    private JPanel buttonspanel;
    
    
    
    private JTextField name;
     private JTextField id; 
     private JTextField phonenumber;
     private JPasswordField password;
 
     
     private JLabel namelabel;
     private JLabel idlabel;
     private JLabel phonenumberlabel;
     private JLabel passwordlabel;
     private JLabel rolelabel;
     
   
     
     private JComboBox roleSelection;
     
     private JRadioButton option1;
     private JRadioButton option2;
     private JRadioButton option3;
     private JRadioButton option4;
     
     
     private JButton updatebutton;
     private JButton addbutton;
     private JButton deletebutton;
     private JButton    resetbutton;
     private JButton loadbutton;
     
     private ButtonGroup buttongroup;
     
     private JTable table;
     private JScrollPane scrollpane;
   public TeacherGui(){
       
        setTitle("Teacher Management System");
        setSize(600,500);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
      setLocationRelativeTo(null);
      
      String[] tablecolumns={"full name","teacherID","phone number","password","role","qualification"};
      
      DefaultTableModel model=new DefaultTableModel(tablecolumns,0);
      table=new JTable(model);
      scrollpane=new JScrollPane(table);
      scrollpane.setPreferredSize(new Dimension(400, 200));
      scrollpane.setBorder(new TitledBorder(new LineBorder(Color.BLACK,1),"Table "));
      
      mainpanel=new JPanel(new BorderLayout());
        namepanel =new JPanel(new FlowLayout(FlowLayout.LEFT));
      idpanel =new JPanel(new FlowLayout(FlowLayout.LEFT));
      passwordpanel =new JPanel(new FlowLayout(FlowLayout.LEFT));
      phonenumberpanel =new JPanel(new FlowLayout(FlowLayout.LEFT));
      detailsAndqualificationPanel =new JPanel(new BorderLayout());
       detailspanel=new JPanel(new GridLayout(5,1));
       detailspanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK,1),"Teacher details"));
       rolepanel =new JPanel(new FlowLayout(FlowLayout.LEFT));
       
       addButtonPanel =new JPanel(new FlowLayout());
       updateButtonPanel=new JPanel(new FlowLayout());
       resetButtonPanel=new JPanel(new FlowLayout());
      deleteButtonPanel =new JPanel(new FlowLayout());
        loadButtonPanel =new JPanel(new FlowLayout());
      
      
       qualificationPanel=new JPanel(new GridLayout(1,4));
       buttonspanel=new JPanel(new GridLayout(1,4));
       
       
       
       qualificationPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK,1),"choose qualification"));
       
      name =new JTextField(20);
      id =new JTextField(20);
      phonenumber =new JTextField(20);
       password=new JPasswordField(20);
      
      updatebutton =new JButton("update");
      resetbutton =new JButton("reset");
      deletebutton =new JButton("delete");
       addbutton=new JButton("add");
       loadbutton=new JButton("load");
       
     namelabel =new JLabel("Full Name:           ");
      idlabel =new JLabel("ID:                          ");
      phonenumberlabel =new JLabel("Phone Number:  ");
      passwordlabel =new JLabel("Password:          ");
       rolelabel=new JLabel("Role:                     ");
      
      roleSelection=new JComboBox();
      roleSelection.addItem("");
      roleSelection.addItem("admin");
       roleSelection.addItem("principal");
      roleSelection.addItem("teacher");
      
      
      
      
      
      
      
      
      
      
      
      
      addbutton.addActionListener(e -> {
    // Get input values
    String fullname = name.getText().trim();
    String identity = id.getText().trim();
    String cellnumber = phonenumber.getText().trim();
    String getpassword = new String(password.getPassword());
    int roleIndex = roleSelection.getSelectedIndex();

    
    
     
    // Validate full name (should not be empty)
    if (fullname.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Full name cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        name.requestFocus();
        return;
    }
    
 
    if (!identity.matches("\\d{13}")) {
        JOptionPane.showMessageDialog(null, "ID must be exactly 13 digits and contain only numbers!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        id.requestFocus();
        return;
    }
    
    if (!cellnumber.matches("0\\d{9}")) {
        JOptionPane.showMessageDialog(null, "Cell number must:\n• Start with 0\n• Be exactly 10 digits\n• Contain only numbers", "Validation Error", JOptionPane.ERROR_MESSAGE);
        phonenumber.requestFocus();
        return;
    }
    
    // Validate password - at least 10 characters with at least one special character or number
    if (getpassword.length() < 10) {
        JOptionPane.showMessageDialog(null, "Password must be at least 10 characters long!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        password.requestFocus();
        return;
    }
    
    // Check for at least one special character or number
    boolean hasSpecialCharOrNumber = getpassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?\\d].*");
    if (!hasSpecialCharOrNumber) {
        JOptionPane.showMessageDialog(null, "Password must contain at least one special character or number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        password.requestFocus();
        return;
    }
 
    // Get radio button selection
    String radioOption = "";
    if (option1.isSelected()) {
        radioOption = "PHD";
    } else if (option2.isSelected()) {
        radioOption = "M.Ed";
    } else if (option3.isSelected()) {
        radioOption = "B.Ed";
    } else if (option4.isSelected()) {
        radioOption = "L.Ed";
    } else {
        JOptionPane.showMessageDialog(null, "Please select a qualification!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
   
    String roleOption = "";
       
    if (roleIndex == 0) {
        JOptionPane.showMessageDialog(null, "Please select a role!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        roleSelection.requestFocus();
        return;
    } else if (roleIndex == 1) {
        roleOption = "admin";
    } else if (roleIndex == 2) {
        roleOption = "Principal";
    } else if (roleIndex == 3) {
        roleOption = "teacher";
    } else {
        roleOption = roleSelection.getSelectedItem().toString();
    }
    
   
    try {
        Teacher teacher = new Teacher(identity, fullname, cellnumber, radioOption, getpassword, roleOption);
        TeacherManagement manage = new TeacherManagement();
        
        Object[] rowData={fullname,identity,cellnumber,getpassword,roleOption,radioOption};
        model.addRow(rowData);
        boolean status = manage.addTeacher(teacher);
     
        if (status) {
            JOptionPane.showMessageDialog(null, "Teacher added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        
            name.setText("");
            id.setText("");
            phonenumber.setText("");
            password.setText("");
            roleSelection.setSelectedIndex(0);
           
            
            buttongroup.clearSelection();
        } else {
            JOptionPane.showMessageDialog(null, "Teacher not added. Please check if ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
    }
});
      
      loadbutton.addActionListener(e->{
       model.setRowCount(0);
          TeacherManagement manage=new TeacherManagement();
          
     ArrayList<Teacher>teachers=manage.getAll();
     
     for(int i=0;i<teachers.size();i++){
         
         Teacher teacher=teachers.get(i);
         
         String teacherid=teacher.getTeacherId();
         String teachername=teacher.getFullName();
         String cellno=teacher.getPhoneNumber();
         String password=teacher.getPassword();
         String qualification=teacher.getQualification();
         String role=teacher.getRole();
         
        Object [] rowData={teachername,teacherid,cellno,password,role,qualification};
         model.addRow(rowData);
     }
          
          
      
      
      
      });
      
      deletebutton.addActionListener(e->{
              
             
          
          TeacherManagement manage=new TeacherManagement();
            String removeId=  JOptionPane.showInputDialog(null, "enter id of Teacher to remove");
          
            boolean status=manage.remove(removeId);
            if(status){
                
              
                JOptionPane.showMessageDialog(null, "Teacher removed");
            }else{ JOptionPane.showMessageDialog(null, "Teacher  not removed");}
              
              
              });
      
      resetbutton.addActionListener(e->{
      name.setText("");
            id.setText("");
            phonenumber.setText("");
            password.setText("");
            roleSelection.setSelectedIndex(0);
           
            
            buttongroup.clearSelection();

            model.setRowCount(0);
      
      
      
      
      });
      
      
      
      option1=new JRadioButton("PHD");
      option2=new JRadioButton("M.Ed");
      option3=new JRadioButton("B.Ed");
      option4=new JRadioButton("L.Ed");
      
      buttongroup=new ButtonGroup();
      buttongroup.add(option1);
      buttongroup.add(option2);
      buttongroup.add(option3);
      buttongroup.add(option4);
      
      addButtonPanel.add(addbutton);
      updateButtonPanel.add(updatebutton);
      resetButtonPanel.add(resetbutton);
      deleteButtonPanel.add(deletebutton);
      loadButtonPanel.add(loadbutton);
   
      buttonspanel.add(addButtonPanel);
       buttonspanel.add(deleteButtonPanel);
        buttonspanel.add(resetButtonPanel);
        buttonspanel.add(updateButtonPanel);
       buttonspanel.add(loadButtonPanel);
       
      
      qualificationPanel.add(option1);
      qualificationPanel.add(option2);
      qualificationPanel.add(option3);
      qualificationPanel.add(option4);
      
      
      rolepanel.add(rolelabel);
      rolepanel.add(roleSelection);
      
      namepanel.add(namelabel);
       namepanel.add(name);
      
      idpanel.add(idlabel);
      idpanel.add(id);
      
      phonenumberpanel.add(phonenumberlabel);
      phonenumberpanel.add(phonenumber);
      
      passwordpanel.add(passwordlabel);
      passwordpanel.add(password);
      
      detailspanel.add(namepanel);
      detailspanel.add(idpanel);
      detailspanel.add(phonenumberpanel);
      detailspanel.add(passwordpanel);
      detailspanel.add(rolepanel);
      
      detailsAndqualificationPanel.add(detailspanel,BorderLayout.NORTH);
      detailsAndqualificationPanel.add(qualificationPanel,BorderLayout.SOUTH);
      
    
       
       mainpanel.add(detailsAndqualificationPanel,BorderLayout.NORTH);
       mainpanel.add(scrollpane,BorderLayout.CENTER);
         mainpanel.add(buttonspanel,BorderLayout.SOUTH);
      add(mainpanel);
      
     setVisible(true);
       
   }
    
    
    
    
    
    
    
    
}
