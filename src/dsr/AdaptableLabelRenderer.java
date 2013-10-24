/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */
package dsr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class AdaptableLabelRenderer<V,E> implements Renderer.EdgeLabel<V,E> {
	
	public Component prepareRenderer(RenderContext<V,E> rc, EdgeLabelRenderer graphLabelRenderer, Object value, 
			boolean isSelected, E edge) {
		return rc.getEdgeLabelRenderer().<E>getEdgeLabelRendererComponent(rc.getScreenDevice(), value, 
				rc.getEdgeFontTransformer().transform(edge), isSelected, edge);
	}
    
    public void labelEdge(RenderContext<V,E> rc, Layout<V,E> layout, E e, String label) {
    	if(label == null || label.length() == 0) return;
    	
    	Graph<V,E> graph = layout.getGraph();
        // don't draw the edge if its incident vertices are not drawn
        Pair<V> endpoints = graph.getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        if (!rc.getEdgeIncludePredicate().evaluate(Context.<Graph<V,E>,E>getInstance(graph,e)))
            return;

        if (!rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v1)) || 
            !rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v2)))
            return;

        Point2D p1 = layout.transform(v1);
        Point2D p2 = layout.transform(v2);
        p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
        p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
        float x1 = (float) p1.getX();
        float y1 = (float) p1.getY();
        float x2 = (float) p2.getX();
        float y2 = (float) p2.getY();

        GraphicsDecorator g = rc.getGraphicsContext();
        float distX = x2 - x1;
        float distY = y2 - y1;
        double totalLength = Math.sqrt(distX * distX + distY * distY);

        double closeness = rc.getEdgeLabelClosenessTransformer().transform(Context.<Graph<V,E>,E>getInstance(graph, e)).doubleValue();

        int posX = (int) (x1 + (closeness) * distX);
        int posY = (int) (y1 + (closeness) * distY);
        
        Edge edge=(Edge)e;
        Util.LineType lt=edge.lineType;
        if (lt.equals(Util.LineType.lineType))
        		{	posX=(int) (x1+(closeness)*distX);
        		    posY = (int) (y1 + (closeness) * distY);if(Math.abs(x2-x1)<10){posX+=10;}
        			
        		}
        else
        	{double t=0.5;
        	double xx,yy;
        	if (lt.equals(Util.LineType.quadType)) 
        		{ 	 xx=2*(1-t)*t* edge.ctrl3.getX()+t*t;
        			 yy=2*(1-t)*t* edge.ctrl3.getY()+t*t;
        		}
        	else
        		{ 	 xx=3*(1-t)*(1-t)*t*edge.ctrl1.getX()+3*(1-t)*t*t*edge.ctrl2.getX()+t*t*t;
        			 yy=3*(1-t)*(1-t)*t*edge.ctrl1.getY()+3*(1-t)*t*t*edge.ctrl2.getY()+t*t*t;
        		}
        	Point2D pl=Util.affineTransform(edge, new Point2D.Double(xx,yy));
    		posX=(int)pl.getX();
    		posY=(int)pl.getY();
    	}
        
        int xDisplacement = (int) (rc.getLabelOffset() * (distY / totalLength));
        int yDisplacement = (int) (rc.getLabelOffset() * (-distX / totalLength));
        
        Component component = prepareRenderer(rc, rc.getEdgeLabelRenderer(), label, 
                rc.getPickedEdgeState().isPicked(e), e);
        
        Dimension d = component.getPreferredSize();
       
      
        
        AffineTransform old = g.getTransform();
        AffineTransform xform = new AffineTransform(old);
        xform.translate(posX+xDisplacement, posY+yDisplacement);
       
       
        
        xform.translate(-d.width/2, -(d.height/2) -10);
        g.setTransform(xform);
        g.draw(component, rc.getRendererPane(), 0, 0, d.width, d.height, true);

        g.setTransform(old);
    }

}
