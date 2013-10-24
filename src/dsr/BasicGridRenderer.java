package dsr;

import java.awt.Color;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.algorithms.layout.Layout;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

public class BasicGridRenderer<V,E> extends BasicRenderer<V,E> implements ChangeListener {

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
	
		p1=new Point2D.Double();
		p2=new Point2D.Double();
		int my=Util.stepY*Util.ylines;
		int mx=Util.stepX*Util.xlines;
		int xX,yY;
		 xX=-10*Util.stepX;
		 yY=-10*Util.stepY;
		for(int i=-10;i<Util.xlines; i++){
			p1.setLocation(xX, -mx);
			p2.setLocation(xX,my );
			xX+=Util.stepX;
			p1=rc.getMultiLayerTransformer().transform(p1);//iunie
			p2=rc.getMultiLayerTransformer().transform(p2);
			g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
		}
		for(int i=-10;i<Util.ylines; i++){
			p1.setLocation(-mx,yY);
			p2.setLocation(mx,yY);
			yY+=Util.stepY;
			p1=rc.getMultiLayerTransformer().transform(p1);
			p2=rc.getMultiLayerTransformer().transform(p2);
			g2d.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());

		} 


	}
	@Override
	public void stateChanged(ChangeEvent arg0) {
		System.out.println(arg0.getSource());
		
	}


}
