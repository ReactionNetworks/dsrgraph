package dsr;

import java.awt.geom.Point2D;

public class Edge {
   byte sgn; // edge's sign: 1,-1
   short lbl; //edge's label: 0..∞
   byte orientation; //S to R direction, R-S direction,undirected
   int id;//
   //control points for cubic curves
   Point2D ctrl1=new Point2D.Double(0.33f, 20f);
   Point2D ctrl2=new Point2D.Double(0.66f, -10f);
   //control point for quad curve
   Point2D ctrl3=new Point2D.Double(0.5, 20f);
   
   boolean edited=false;
   boolean isLine=true;
   boolean isMultiple=false;//true if between two vertices there are more edges
   
   Util.LineType lineType=Util.LineType.lineType;
   public Edge()
   
   {
	   
   }
   public Edge(short value, int id){
	   this.sgn=(byte)((value>0) ? 1 : -1);
	   this.lbl=(short) (sgn*value);
	   //this.lbl=value;
	   orientation=1;this.id=id;
	   if (this.lbl!=1)
		   Util.allOne=false;
	   
   }
   public Edge(short value, int id, boolean multiple){
	   this.sgn=(byte)((value>0) ? 1 : -1);
	   this.lbl=(short) (sgn*value);
	   //this.lbl=value;
	   orientation=1;this.id=id;
	   this.isMultiple=multiple;
	   //Point2D ctrl3=new Point2D.Double(-0.5, 40f); //it is not the first edge between two vertices
	     
	   
   }
   public Edge(byte sgn, short lbl, byte orientation, int id){
	   this.lbl=lbl;
	   this.sgn=sgn;
	   this.orientation=orientation;
	   this.id=id;
   }
   public int getId(){
	   return id;
   }
   public String toString(){
	  if (Util.toLatex)
		  if (lbl!=1)
			   if (lbl==100)
				   return "$\\infty$";
			   else
			       return ""+lbl;
		   else
			   return "{}";
			 //   return (Util.allOne)? "":""+lbl ;
		  
	   if (lbl!=1)
		   if (lbl==100)
			   return "<html>&#8734;</html>";
		   else
		       return "<html>"+lbl+"</html>";
	   else
		   return "";
		 //   return (Util.allOne)? "":"<html>"+lbl+"</html>" ;
   }

}
