package dsr;

public class Interaction extends Vertex{
     short no;
     static boolean name=true;
     public Interaction(short no){
    	 this.no=no;
     }
     
     public String toString(){
    	 if (name)
    		  return "R"+(no+1);
    	 else 
    		 return ""+(no+1);
     }
}
