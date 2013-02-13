package dsr;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class BasicGridRenderer<V,E> extends BasicRenderer<V,E> {

    VisualizationViewer<V,E> vv;
	public BasicGridRenderer(){
		super();
	}
    public void setViewer(VisualizationViewer<V,E> vv){
    	this.vv=vv;
    }
	public void render(RenderContext<V,E> rc, Layout<V,E> layout){
		super.render(rc, layout);
		if (Util.grid)
			renderGrid(rc, layout);
	}

	public void renderGrid(RenderContext<V,E> rc, Layout<V,E>layout){
		GraphicsDecorator g2d=rc.getGraphicsContext();
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.setStroke(RenderContext.DOTTED);
		Point2D p1,p2;
		//Point2D lvc = rc.getMultiLayerTransformer().inverseTransform(vv.getCenter());
		p1=new Point2D.Double();
		p2=new Point2D.Double();
		int my=Util.stepY*Util.ylines;
		int mx=Util.stepX*Util.xlines;
		for(int i=-10;i<Util.xlines; i++){
			p1.setLocation(i*Util.stepX, -mx);
			p2.setLocation(i*Util.stepX,my );
			p1=rc.getMultiLayerTransformer().transform(p1);
			p2=rc.getMultiLayerTransformer().transform(p2);
			g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
		}
		for(int i=-10;i<Util.ylines; i++){
			p1.setLocation(-mx,i*Util.stepY);
			p2.setLocation(mx,i*Util.stepY);
			p1=rc.getMultiLayerTransformer().transform(p1);
			p2=rc.getMultiLayerTransformer().transform(p2);
			g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());

		} 


	}


}
