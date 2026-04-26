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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
public class leanerGUI extends JFrame{
    
    private JPanel mainpanel;
    private JPanel detailspanel;
    private JPanel namepanel;
    private JPanel idpanel;
    private JPanel headingpanel;
    private JPanel contactpanel;
    private JPanel buttonspanel;
    private JPanel headerndetails;
    private JPanel buttonpanel1;
      private JPanel buttonpanel2;
        private JPanel buttonpanel3;
        private JPanel buttonpanel4;
        private JPanel buttonpanel5;
      private JPanel boxpanel;
   
      
    private JTextField name;
    private JTextField id;
    private JTextField contact;
    
    private JLabel namelabel;
     private JLabel idlabel;
    private JLabel headinglabel;
     private JLabel contactlabel;
    private JLabel boxlabel;
     
     
     private JButton button1;
     private JButton button2;
     private JButton button3;
     private JButton button4;
     private JButton button5;
     private JTable table;  
     
     
     private JComboBox grade;
     private JScrollPane scrollpane;
     
   public leanerGUI(){
        setTitle("Leaner Management System");
        setSize(600,500);
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
      setLocationRelativeTo(null);
      
      
      String[] tablenames={"Full name","id","grade","contact"};
      
      DefaultTableModel model=new DefaultTableModel(tablenames,0);
      
   
      table=new JTable(model);  
      
      scrollpane=new JScrollPane(table);
      scrollpane.setPreferredSize(new Dimension(400, 200));
      scrollpane.setBorder(new TitledBorder(new LineBorder(Color.BLACK,1),"Table "));
      
       mainpanel=new JPanel(new BorderLayout());
       detailspanel=new JPanel(new GridLayout(4,1));
       idpanel=new JPanel();
       detailspanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK,1),"Leaner details"));
       
       namepanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
       idpanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
       headingpanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
       headingpanel.setBackground(Color.gray);
       contactpanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
       buttonspanel=new JPanel(new GridLayout(1,5));
       buttonpanel1=new JPanel(new FlowLayout());
       buttonpanel2=new JPanel(new FlowLayout());
       buttonpanel3=new JPanel(new FlowLayout());
        buttonpanel4=new JPanel(new FlowLayout());
         buttonpanel5=new JPanel(new FlowLayout());
       boxpanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerndetails=new JPanel(new BorderLayout());
       
        
        
        
       button1=new JButton("ADD");
       button2=new JButton("REMOVE");
       button3=new JButton("LOAD");
        button4=new JButton("EXIT");
         button5=new JButton("UPDATE");
        
       namelabel=new JLabel("NAME:         ");
       idlabel=new JLabel("ID:                ");
       headinglabel=new JLabel("SABATHA PRIMARY SCHOOL");
      
        contactlabel=new JLabel("CONTACT: ");
        boxlabel=new JLabel("GRADE:       ");
       
      
        
     
        button1.addActionListener(e->{
       
    String details=name.getText().trim();
    String idno=id.getText().trim();
    String contactno=contact.getText().trim();
    int Grade=0;
    boolean status=true;
    
    //validation
    if(details.isEmpty() || idno.isEmpty() || contactno.isEmpty() || grade.getSelectedIndex()==0){
        JOptionPane.showMessageDialog(null, "All fields must be filled");
        return; 
    }
    
 
    if(details.matches(".*\\d.*")){ // Check if contains numbers
        JOptionPane.showMessageDialog(null, "Name cannot contain numbers");
        name.setText("");
        return;
    } else if(!details.matches("[a-zA-Z\\s]+")){ // Only letters and spaces allowed
        JOptionPane.showMessageDialog(null, "Name can only contain letters and spaces");
        name.setText("");
        return; 
    }
    
   
    if(idno.length()!=13){
        JOptionPane.showMessageDialog(null, "ID must be 13 characters");
        id.setText("");
        return; 
    } else if(!idno.matches("\\d+")){ // Check if contains only digits
        JOptionPane.showMessageDialog(null, "ID can only contain numbers");
        id.setText("");
        return; 
    }
    
    
    if(!contactno.matches("\\d+")){ // Check if contains only digits
        JOptionPane.showMessageDialog(null, "Contact can only contain numbers");
        contact.setText("");
        return;
    } else if(contactno.length()!=10){
        JOptionPane.showMessageDialog(null, "Contact must be 10 digits");
        contact.setText("");
        return; 
    } else if(!contactno.startsWith("0")){
        JOptionPane.showMessageDialog(null, "Contact must start with 0");
        contact.setText("");
        return; 
    }
    
  
    int t=grade.getSelectedIndex();
    if(t==1) Grade=1;
    if(t==2) Grade=2;
    if(t==3) Grade=3;
    if(t==4) Grade=4;
    if(t==5) Grade=5;
   if(t==6) Grade=6;
    if(t==7) Grade=7;
    
    // Check if ID already exists in table
    boolean idExists = false;
    for(int j=0; j<model.getRowCount(); j++){
        String existingId = (String)model.getValueAt(j, 1);
        if(existingId.equals(idno)){
            idExists = true;
            break;
        }
    }
    
    if(idExists){
        JOptionPane.showMessageDialog(null, "Learner with this ID already exists!");
    } else {
        Leaner leaner=new Leaner(details,Grade,idno,contactno);
        Object[] rowdate={details,idno,Grade,contactno};
        model.addRow(rowdate);
        
        name.setText("");
        id.setText("");
        contact.setText("");
        grade.setSelectedIndex(0);
        
        leanerManagement management=new leanerManagement();
        boolean database=  management.addLeaner(leaner);
        
        if(database){
            JOptionPane.showMessageDialog(null, "Learner added to database"); 
        }
    }
});
        
        
        //exit button
        button4.addActionListener(e->{
        
        System.exit(0);
        
        });
        
        //appending selected row details into textfields
        table.getSelectionModel().addListSelectionListener(e->{
         int selectedrow=table.getSelectedRow(); 
        
     
        if(selectedrow>=0){  
            
            String nname= (String)model.getValueAt(selectedrow, 0);
            String iid= (String)model.getValueAt(selectedrow, 1);
            int ggrade= (int)model.getValueAt(selectedrow, 2);
            String ccontact= (String)model.getValueAt(selectedrow, 3);
            
           name.setText(nname);
           
           id.setText(iid);
           grade.setSelectedIndex(ggrade);
           contact.setText(ccontact);
        }
        
        
        
        
        });
        
        // load button
               button3.addActionListener(e->{
             model.setRowCount(0);
         leanerManagement management=new leanerManagement();
        ArrayList<Leaner>newlist=management.getAll();
            
            for(int i=0;i<newlist.size();i++){
                
                 Leaner leaner=newlist.get(i);
        
                 String details=leaner.getFullname();
                 String idno=leaner.getId();
                 int Grade=leaner.getGrade();
                 String contactno=leaner.getContact();
         Object[] rowdate={details,idno,Grade,contactno};
        model.addRow(rowdate);
        
                
            }
        
        
        
        
        });
        
        
        //remove button
        button2.addActionListener(e->{
        
            
          String idno=  JOptionPane.showInputDialog(null,"enter id of leaner to remove");
         if(idno == null || idno.trim().isEmpty()){
        return;
    }
    
    for(int i = 0; i < model.getRowCount(); i++){
        String tableId = (String)model.getValueAt(i, 1);
        if(tableId.equals(idno)){
            model.removeRow(i);
            break;
        }
    }
        leanerManagement management=new leanerManagement();
            
            
          boolean status=  management.removeLeaner(idno);
            
        if(status){
             JOptionPane.showMessageDialog(null, "Leaner removed from database"); 
          }else{
             JOptionPane.showMessageDialog(null, "leaner not removed");
            
        }
       
        
        });
        //update button
        button5.addActionListener(e->{
        
    int selectedrow = table.getSelectedRow();
    
    if(selectedrow >= 0){
        // Get values from text fields
        String newName = name.getText().trim();
        String newContact = contact.getText().trim();
        int newGrade = 0;
        
        boolean status = true;
        
   
        if(newName.isEmpty()){
            JOptionPane.showMessageDialog(null, "Name is required");
            status=false;
            return;
        } else if(newName.matches(".*\\d.*")){
            JOptionPane.showMessageDialog(null, "Name cannot contain numbers");
            status=false;
            return;
        } else if(!newName.matches("[a-zA-Z\\s]+")){
            JOptionPane.showMessageDialog(null, "Name can only contain letters and spaces");
            status=false;
            return;
        }
        
    
        if(newContact.isEmpty()){
            JOptionPane.showMessageDialog(null, "Contact number is required");
            status=false;
            return;
        } else if(!newContact.matches("\\d+")){
            JOptionPane.showMessageDialog(null, "Contact can only contain numbers");
            status=false;
            return;
        } else if(newContact.length()!=10){
            JOptionPane.showMessageDialog(null, "Contact must be 10 digits");
            status=false;
            return;
        } else if(!newContact.startsWith("0")){
            JOptionPane.showMessageDialog(null, "Contact must start with 0");
            status=false;
            return;
        }
        
    
        int t = grade.getSelectedIndex();
        if(t == 0){
            JOptionPane.showMessageDialog(null, "Please select a grade");
            status=false;
            return;
        } else {
            if(t == 1) newGrade = 1;
            if(t == 2) newGrade = 2;
            if(t == 3) newGrade = 3;
            if(t == 4) newGrade = 4;
            if(t == 5) newGrade = 5;
            if(t == 6) newGrade = 6;
            if(t == 7) newGrade = 7;
        }
        
        if(status){
            // Get the ORIGINAL ID from the selected row
            String originalId = (String)model.getValueAt(selectedrow, 1);
            
            // Update the table
            model.setValueAt(newName, selectedrow, 0);
            model.setValueAt(newGrade, selectedrow, 2);
            model.setValueAt(newContact, selectedrow, 3);
            
            // Create updated learner object
            Leaner updatedLeaner = new Leaner(newName, newGrade, originalId, newContact);
            
            // Update in database
            leanerManagement management = new leanerManagement();
            boolean success = management.updateLeaner(originalId, updatedLeaner);
            
            if(success){
                JOptionPane.showMessageDialog(null, "Learner updated successfully!");
                
                // Clear fields after update
                name.setText("");
                id.setText("");
                contact.setText("");
                grade.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update learner in database");
            }
        }
        
    } else {
        JOptionPane.showMessageDialog(null, "Please select a row to update first!");
    }
    
});
              
       name=new JTextField(20);
       id=new JTextField(20);
       contact=new JTextField(20);
       
       grade=new JComboBox();
       grade.addItem("");
       grade.addItem("1");
       grade.addItem("2");
       grade.addItem("3");
       grade.addItem("4");
       grade.addItem("5");
        grade.addItem("6");
        grade.addItem("7");
       
       
       
       
       buttonpanel1.add(button1);
        buttonpanel2.add(button2);
         buttonpanel3.add(button3);
       buttonpanel4.add(button4);
        buttonpanel5.add(button5);
       
       buttonspanel.add(buttonpanel1);
       buttonspanel.add(buttonpanel2);
       buttonspanel.add(buttonpanel3);
       buttonspanel.add(buttonpanel4);
        buttonspanel.add(buttonpanel5);
       
       headingpanel.add(headinglabel);
       
       contactpanel.add(contactlabel);
        contactpanel.add(contact);
       
       boxpanel.add(boxlabel);
        boxpanel.add(grade);
       namepanel.add(namelabel);
       namepanel.add(name);
       
    idpanel.add(idlabel);
       idpanel.add(id);
       
       detailspanel.add(namepanel);
       detailspanel.add(idpanel);
       detailspanel.add(contactpanel);
       detailspanel.add(boxpanel);
       headerndetails.add(headingpanel,BorderLayout.NORTH);
       headerndetails.add(detailspanel,BorderLayout.SOUTH);
       
     //  mainpanel.add(detailspanel,BorderLayout.CENTER);
        mainpanel.add(headerndetails,BorderLayout.NORTH);
        mainpanel.add(scrollpane,BorderLayout.CENTER);
         mainpanel.add(buttonspanel,BorderLayout.SOUTH);
       add(mainpanel);
        setVisible(true);
    }
    
    
    
}