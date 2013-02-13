package dsr;

public class Interaction extends Vertex{
     short no;
     public Interaction(short no){
    	 this.no=no;
     }
     
     public String toString(){
    	 return "R"+no;
     }
}
