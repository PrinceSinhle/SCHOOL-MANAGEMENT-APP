package schoolmanagement.za;

import java.util.ArrayList;

public class leanerManagement {
    
   private  ArrayList<Leaner>leaners;
  private leanerDatabase database;
    public leanerManagement() {
        this.leaners = new ArrayList<>();
        this.database=new leanerDatabase("jdbc:derby://localhost:1527/tableDB");
    }

    public ArrayList<Leaner> getLeaners() {
        return leaners;
    }

    public void setLeaners(ArrayList<Leaner> leaners) {
        this.leaners = leaners;
    }

public boolean addLeaner(Leaner leaner){
    
    leaners.add(leaner);
    
   boolean status= database.addLeaner(leaner);
    
    return status;
}
  public boolean removeLeaner(String id){
    
    boolean status=false;
 
    ArrayList<Leaner>newlist=database.getAll();
 
    
    
      for(int i=0;i<newlist.size();i++){
          
          Leaner newleaner=newlist.get(i);
          
          if(newleaner.getId().equalsIgnoreCase(id)){
          
          newlist.remove(i);
         status= database.delete(newleaner);
         break;
          }
          
      }
    
  /* if (!status) {
        Leaner tempLearner = new Leaner();
        tempLearner.setId(id);
        status = database.delete(tempLearner);
    }*/
    
    return status;
}  
   public ArrayList<Leaner> getAll(){
       
      ArrayList<Leaner>newlist=database.getAll();
       
       return newlist;
   }
   

   public boolean updateLeaner(String id, Leaner updatedLeaner){
       
       boolean status = false;
       
       ArrayList<Leaner> newlist = database.getAll();
       
       for(int i = 0; i < newlist.size(); i++){
           
           Leaner currentLeaner = newlist.get(i);
           
           if(currentLeaner.getId().equalsIgnoreCase(id)){
               
             
               newlist.set(i, updatedLeaner);
               
         
               status = database.updateLeaner(id, updatedLeaner);
               break;
               
           }
           
       }
       
       return status;
   }
   
}