package dsr;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import edu.uci.ics.jung.visualization.decorators.EdgeShape.QuadCurve;

public class QuadCurveTransformer extends QuadCurve<Vertex,Edge>
{   VisualizationViewer<Vertex,Edge> v;

public QuadCurveTransformer(VisualizationViewer<Vertex,Edge> v){
	super();
	this.v=v;
}

public Point2D affineTransform(Edge edge, Point2D p){
	Layout<Vertex,Edge> layout = v.getModel().getGraphLayout();

	Pair<Vertex> endpoints=Util.g.getEndpoints(edge);
	Vertex v1 = endpoints.getFirst();
	Vertex v2 = endpoints.getSecond();

	Point2D p1 = layout.transform(v1);
	Point2D p2 = layout.transform(v2);
	p1 = v.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
	p2 = v.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
	float x1 = (float) p1.getX();
	float y1 = (float) p1.getY();
	float x2 = (float) p2.getX();
	float y2 = (float) p2.getY();
	AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);
	float dx = x2-x1;
	float dy = y2-y1;
	float thetaRadians = (float) Math.atan2(dy, dx);
	xform.rotate(thetaRadians);
	float dist = (float) Math.sqrt(dx*dx + dy*dy);
	xform.scale(dist, 1.0);
	Point2D pp=new Point2D.Double();
	return xform.transform(p, pp);
}
public Shape transform(Context<Graph<Vertex,Edge>,Edge> context){
	Layout<Vertex,Edge> layout = v.getModel().getGraphLayout();
    QuadCurve2D old=new QuadCurve2D.Float();
	//CubicCurve2D old=new CubicCurve2D.Float();//=(CubicCurve2D)super.transform(context);
	//	CubicCurve2D old=(CubicCurve2D)super.transform(context);
	Edge edge = (Edge)context.element;
	if(Util.vv.getPickedEdgeState().getPicked().contains(edge))
	{edge.edited=true;edge.isLine=false;edge.lineType=Util.LineType.quadType;
	if (((Util.ctrl3.getEdge()!=null) &&(!(Util.ctrl3.getEdge().equals(edge)))) || (Util.ctrl3.getEdge()==null)) //there isn't any edge that is controled by the points
		
	{
		Point2D currCtrl3 = new Point2D.Double();
		//Point2D currCtrl2 = new Point2D.Double();
		Util.ctrl3.setEdge(edge);//Util.ctrl2.setEdge(edge);//the current edge for editing
		currCtrl3=affineTransform(edge, edge.ctrl3);
		//currCtrl2=affineTransform(edge, edge.ctrl2);

		layout.setLocation(Util.ctrl3,  currCtrl3);
		
	}
	else
	{
		Point2D currCtrl3 = new Point2D.Double();
		//Point2D currCtrl2 = new Point2D.Double();
		currCtrl3=affineTransform(edge, edge.ctrl3);
		//currCtrl2=affineTransform(edge, edge.ctrl2);
		System.out.println("sfsdgsdfg"+Util.modeBox.getSelectedIndex());
		if(((Util.ctrl3.getEdge().equals(edge)) & 
				(!(Util.vv.getPickedVertexState().getPicked().contains(Util.ctrl3))
						) //here the shape of the edge should not be changed
				//!(Util.vv.getPickedVertexState().getPicked().contains(Util.ctrl2))
				))
			//the edge is selected, but it is not changed by the control nodes
			//the control nodes position is changed
		{layout.setLocation(Util.ctrl3,  currCtrl3); System.out.println("ADAsdasdas");
		//layout.setLocation(Util.ctrl2, currCtrl2);}
		}  
	}}




	if (edge.edited)
	{old.setCurve(0.0,0.0, edge.ctrl3.getX(), edge.ctrl3.getY(), 1.0,0.0);
		//old.setCurve(0.0f, 0.0f, edge.ctrl1.getX(),edge.ctrl1.getY(),edge.ctrl2.getX(), edge.ctrl2.getY(), 1.0f,0.0f);
	return old;
	}
	else
		return new Line2D.Float(0.0f, 0.0f, 1.0f, 0.0f);
	//}
	//else 
	//	old.setCurve(0.0f, 0.0f, edge.ctrl1.getX(),edge.ctrl1.getY(),edge.ctrl2.getX(), edge.ctrl2.getY(), 1.0f,0.0f);


	//return new 

}
}