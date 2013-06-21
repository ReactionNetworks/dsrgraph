package dsr;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.DefaultParallelEdgeIndexFunction;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EdgeShape.CubicCurve;
import edu.uci.ics.jung.visualization.decorators.EdgeShape.Line;

//public class MultipleEdgeFirstTransformer extends EdgeShape.QuadCurve<Vertex,Edge>
public class MultipleEdgeFirstTransformer extends QuadCurveTransformer

{   VisualizationViewer<Vertex,Edge> v;
    EdgeShape.Line<Vertex,Edge> cct;

    //protected EdgeIndexFunction<Vertex,Edge> parallelEdgeIndexFunction;
    
    //VisualizationViewer<Vertex,Edge> v
public MultipleEdgeFirstTransformer(){
	super();
	cct=new EdgeShape.Line<Vertex,Edge>();
    	//this.v=v;
}




public Shape transform(Context<Graph<Vertex,Edge>,Edge> context){
  
    
  
	Edge edge = (Edge)context.element;
	Graph<Vertex,Edge> graph = context.graph;
    
	
	if (!edge.isMultiple)
		{return cct.transform(context);
		}
	else
		{edge.lineType=Util.LineType.quadType;
		int index=1; 
		if(parallelEdgeIndexFunction != null) 
			index = parallelEdgeIndexFunction.getIndex(graph, edge);
		super.setVV(Util.vv);
		edge.ctrl3.setLocation(edge.ctrl3.getX(), 20f+20f* (index==1 ? 0: -2 ));    
		 return super.transform(context);
		
		}
}
}