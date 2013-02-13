package dsr;

public class ControlPoint extends Vertex{
	 
	byte index;//1,2
	Edge e;
	
	public ControlPoint(byte index){
	    this.index=index;
	    //this.e=edgeId;
	}
	public void setEdge(Edge e){
		this.e=e;
	}
	public Edge getEdge(){
		return e;
	}
    public String toString(){
    	return "";
    }
}
