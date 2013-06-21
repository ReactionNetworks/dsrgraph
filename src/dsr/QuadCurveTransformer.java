package dsr;

import java.awt.Shape;
import java.awt.geom.AffineTransform;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import dsr.Util.LineType;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import edu.uci.ics.jung.visualization.decorators.EdgeShape.QuadCurve;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

public class QuadCurveTransformer extends QuadCurve<Vertex,Edge>
{   VisualizationViewer<Vertex,Edge> v;


public QuadCurveTransformer(){
	super();
	//this.v=v;
}

public void setVV(VisualizationViewer<Vertex,Edge> vv){
	v=vv;
}

public QuadCurveTransformer(VisualizationViewer<Vertex,Edge> v){
	super();
	this.v=v;
}

public Point2D affineTransform(Edge edge, Point2D p){
	//transforms from 0 - 1 to real position
	
	//the problem is that translation is applied i think only in multilayer
	Layout<Vertex,Edge> layout = v.getModel().getGraphLayout();

	Pair<Vertex> endpoints=Util.g.getEndpoints(edge);
	Vertex v1 = endpoints.getFirst();
	Vertex v2 = endpoints.getSecond();

	Point2D p1 = layout.transform(v1);
	Point2D p2 = layout.transform(v2);
	 
	// p1=v.getRenderContext().getMultiLayerTransformer()
	
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
	Graph<Vertex,Edge> graph = context.graph;
   
	QuadCurve2D old=new QuadCurve2D.Float();
	Edge edge = (Edge)context.element;
	
	if(Util.vv.getPickedEdgeState().getPicked().contains(edge))
	{edge.edited=true;edge.isLine=false;edge.lineType=Util.LineType.quadType;
	if (((Util.ctrl3.getEdge()!=null) &&(!(Util.ctrl3.getEdge().equals(edge)))) || (Util.ctrl3.getEdge()==null)) //there isn't any edge that is controled by the points

	{
		Point2D currCtrl3 = new Point2D.Double();
		Util.ctrl3.setEdge(edge);//the current edge for editing
		currCtrl3=affineTransform(edge, edge.ctrl3);
		layout.setLocation(Util.ctrl3,  currCtrl3);

	}
	else
	{
		Point2D currCtrl3 = new Point2D.Double();
		currCtrl3=affineTransform(edge, edge.ctrl3);
		if(((Util.ctrl3.getEdge().equals(edge)) & (!(Util.vv.getPickedVertexState().getPicked().contains(Util.ctrl3))) 
						//here the shape of the edge should not be changed
				))
			//the edge is selected, but it is not changed by the control nodes
			//the control nodes position is changed
		{layout.setLocation(Util.ctrl3,  currCtrl3);
		}  
	}}
	
	if (edge.lineType.equals(Util.LineType.quadType))
	{old.setCurve(0.0,0.0, edge.ctrl3.getX(), edge.ctrl3.getY(), 1.0,0.0);
	return old;
	}
	else
		if(edge.isMultiple && edge.lineType.equals(LineType.quadType))
		{ 
			
			old.setCurve(0.0,0.0, edge.ctrl3.getX(), edge.ctrl3.getY(), 1.0,0.0);
			return old;

		}
		else
			if(edge.lineType.equals(Util.LineType.cubicType))
			{ CubicCurve2D oldc=new CubicCurve2D.Float();
				oldc.setCurve(0.0f, 0.0f, edge.ctrl1.getX(),edge.ctrl1.getY(),edge.ctrl2.getX(), edge.ctrl2.getY(), 1.0f,0.0f);
				return oldc;
			}
			return new Line2D.Float(0.0f, 0.0f, 1.0f, 0.0f);
	

}
}