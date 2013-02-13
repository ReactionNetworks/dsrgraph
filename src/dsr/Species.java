package dsr;

public class Species extends Vertex{
  short no;
  String n;
  static boolean name=true;//if species have name as A,B,C (name=true) or S1, S2; 
  //all the species follow the same rule
  public Species(short no){
	  this.no=(short)(no);
  }
  public Species(String n){
	  this.n=n;
  }
  
  public String toString(){
	  if (name)
		  //return Character.toString((char)('A'+no));
		  return n;
	  else
		   return "S"+(no+1);
	  
  }
}
