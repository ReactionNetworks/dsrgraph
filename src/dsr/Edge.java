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
   Point2D ctrl3=new Point2D.Double(0.5, 10f);
   
   boolean edited=false;
   boolean isLine=true;
   
   Util.LineType lineType=Util.LineType.lineType;
   public Edge()
   
   {
	   
   }
   public Edge(short value, int id){
	   this.sgn=(byte)((value>0) ? 1 : -1);
	   this.lbl=(short) (sgn*value);
	   orientation=1;this.id=id;
	   
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
	   if (lbl!=1)
		    return ""+lbl;
	   else
		    return "";
   }

}
