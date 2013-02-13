package dsr;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;


import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.util.SelfLoopEdgePredicate;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;

import edu.uci.ics.jung.graph.util.Pair;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.GradientEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.renderers.Renderer;


public class DsrDraw extends JApplet implements ActionListener{
	static VertexShapeSizeAspect<Vertex> vsta;
	protected JCheckBox v_color;
	protected JCheckBox e_color;
	protected JCheckBox v_stroke;
	protected JCheckBox e_uarrow_pred;
	protected JCheckBox e_darrow_pred;
	protected JCheckBox v_shape;
	protected JCheckBox v_size;
	protected JCheckBox v_aspect;
	protected JCheckBox v_labels;
	protected JRadioButton e_line;
	protected JRadioButton e_bent;
	protected JRadioButton e_wedge;
	protected JRadioButton e_quad;
	protected JRadioButton e_ortho;
	protected JRadioButton e_cubic;
	protected JCheckBox e_labels;
	protected JCheckBox font;
	protected JCheckBox e_show_d;
	protected JCheckBox e_show_u;
	protected JCheckBox v_small;
	protected JCheckBox zoom_at_mouse;
	protected JCheckBox set_grid;
	protected JCheckBox snap_to_grid;
	protected JCheckBox fill_edges;
	protected JCheckBox abc_names;

	protected JRadioButton no_gradient;
	//		protected JRadioButton gradient_absolute;
	protected JRadioButton gradient_relative;

	protected static final int GRADIENT_NONE = 0;
	protected static final int GRADIENT_RELATIVE = 1;
	//		protected static final int GRADIENT_ABSOLUTE = 2;
	protected static int gradient_level = GRADIENT_NONE;

	protected DefaultModalGraphMouse<Vertex,Edge> gm;
	protected GradientPickedEdgePaintFunction edgeDrawPaint;
	protected GradientPickedEdgePaintFunction edgeFillPaint;
	protected SnapToGridStaticLayout snapToGridLayout;
	protected NoSnapToGridStaticLayout nosnapToGridLayout;
	protected CubicCurveTransformer cubicTransf;
	protected QuadCurveTransformer quadTransf;
	
	boolean startEditing=false;
	public int grid_stepX, grid_stepY;
	boolean cubicType=false;

	public static void main(String[] args) throws IOException 
	{
		JFrame jf = new JFrame();
		JPanel jp=new DsrDraw().startFunction(args[0]);
		jf.getContentPane().add(jp);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		jf.setPreferredSize(new Dimension(1200,1000));
		jf.pack();
		jf.setVisible(true);

	}  
	public void init(){
		// WindowUtilities.setNativeLookAndFeel();
		Container content = getContentPane();
		content.setBackground(Color.LIGHT_GRAY);
		resize(1000,800);
		content.setPreferredSize(new Dimension(1000,1000));
		JPanel jp=new DsrDraw().startFunction(getParameter("content"));
		content.add(jp);
		
	}
	//create a grid layout
	public JPanel startFunction(String s){   
		//Util.g = Util.readGraphFromFile(s);
		Util.g = Util.readGraphFromContent(s);
		Layout<Vertex,Edge> frl=new FRLayout<Vertex,Edge>(Util.g);
	
		Util.vv = new VisualizationViewer<Vertex,Edge>(frl);
		BasicGridRenderer<Vertex,Edge> bb=new BasicGridRenderer<Vertex,Edge>();
		
		Util.vv.setRenderer(bb);
		
		Util.stepX=50; Util.stepY=50;
		Util.xlines=20;Util.ylines=20;



	
		vsta=new VertexShapeSizeAspect<Vertex>();
		snapToGridLayout=new SnapToGridStaticLayout(Util.g);//,Util.vv.getGraphLayout());
		nosnapToGridLayout=new NoSnapToGridStaticLayout(Util.g);//,Util.vv.getGraphLayout());
		cubicTransf=new CubicCurveTransformer(Util.vv);
		quadTransf=new QuadCurveTransformer(Util.vv);
		Util.vv.getRenderContext().setVertexDrawPaintTransformer(new ControlPickableVertexPaintTransformer(Util.vv.getPickedVertexState(),Color.GRAY, Color.RED));
		Util.vv.getRenderContext().setVertexShapeTransformer(vsta);
		Util.vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		Util.vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Vertex>());
		Util.vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Vertex,Paint>(){
			public Paint transform(Vertex v){
				if (v instanceof ControlPoint)
					return Color.green;
				else return Color.WHITE;
			}
		});

		Util.vv.getRenderContext().setEdgeStrokeTransformer(new Transformer<Edge,Stroke>()
				{ public Stroke transform(Edge e){
					return (e.sgn>0)? new BasicStroke(1): RenderContext.DASHED;}
				}
				);
		Util.vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Vertex,Edge>());		 
		//Util.vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
   
		JPanel jp = new JPanel();
		jp.setName("Chimical Reaction Networlk");
		jp.setLayout(new BorderLayout());

		Util.vv.setBackground(Color.white);
		GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(Util.vv);
		JPanel title=new JPanel();
		title.setBackground(Color.GRAY);
		title.setPreferredSize(new Dimension(100, 5));
		title.setForeground(Color.WHITE);
		JLabel title_label=new JLabel("Chemical Reaction Network");
		title_label.setForeground(Color.WHITE);
		title.add(title_label);
	//	jp.add(title, BorderLayout.NORTH);
		jp.add(scrollPane);
		
		jp.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		gm = new DefaultModalGraphMouse<Vertex,Edge>();
        
		Util.vv.setGraphMouse(gm);
       
        gm.setMode(Mode.PICKING);
      
		addBottomControls( jp );

		Util.ctrl1= new ControlPoint((byte) 1);
		Util.ctrl2=new ControlPoint((byte) 2);
		Util.ctrl3=new ControlPoint((byte) 3);
		Util.g.addVertex(Util.ctrl1);
		Util.g.addVertex(Util.ctrl2);
		Util.g.addVertex(Util.ctrl3);
		return jp;
	}

  

	protected void addBottomControls(final JPanel jp) 
	{
		final JPanel control_panel = new JPanel();
		jp.add(control_panel, BorderLayout.EAST);
		control_panel.setLayout(new BorderLayout());
		final Box vertex_panel = Box.createVerticalBox();
		vertex_panel.setBorder(BorderFactory.createTitledBorder("Species"));
		final Box edge_panel = Box.createVerticalBox();
		edge_panel.setBorder(BorderFactory.createTitledBorder("Edges"));
		final Box both_panel = Box.createVerticalBox();

		control_panel.add(vertex_panel, BorderLayout.NORTH);
		control_panel.add(edge_panel, BorderLayout.SOUTH);
		control_panel.add(both_panel, BorderLayout.CENTER);


		abc_names = new JCheckBox("ABC name for species");
		abc_names.addActionListener(this);
		vertex_panel.add(abc_names);
		// set up edge controls

	/*	JPanel gradient_panel = new JPanel(new GridLayout(1, 0));
		gradient_panel.setBorder(BorderFactory.createTitledBorder("Edge paint"));
		no_gradient = new JRadioButton("Solid color");
		no_gradient.addActionListener(this);
		no_gradient.setSelected(true);
		//		gradient_absolute = new JRadioButton("Absolute gradient");
		//		gradient_absolute.addActionListener(this);
		gradient_relative = new JRadioButton("Gradient");
		gradient_relative.addActionListener(this);
		ButtonGroup bg_grad = new ButtonGroup();
		bg_grad.add(no_gradient);
		bg_grad.add(gradient_relative);
		//bg_grad.add(gradient_absolute);
		gradient_panel.add(no_gradient);
		//gradientGrid.add(gradient_absolute);
		gradient_panel.add(gradient_relative);
*/
		JPanel shape_panel = new JPanel(new GridLayout(3,2));
		shape_panel.setBorder(BorderFactory.createTitledBorder("Edge shape"));
		e_line = new JRadioButton("line");
		e_line.addActionListener(this);
		e_line.setSelected(true);
		//        e_bent = new JRadioButton("bent line");
		//        e_bent.addActionListener(this);
	//	e_wedge = new JRadioButton("wedge");
		//e_wedge.addActionListener(this);
		e_quad = new JRadioButton("quad curve");
		e_quad.addActionListener(this);
		e_cubic = new JRadioButton("cubic curve");
		e_cubic.addActionListener(this);
//		e_ortho = new JRadioButton("orthogonal");
//		e_ortho.addActionListener(this);
		ButtonGroup bg_shape = new ButtonGroup();
		bg_shape.add(e_line);
		//        bg.add(e_bent);
//		bg_shape.add(e_wedge);
		bg_shape.add(e_quad);
	//	bg_shape.add(e_ortho);
		bg_shape.add(e_cubic);
		shape_panel.add(e_line);
		//        shape_panel.add(e_bent);
		//shape_panel.add(e_wedge);
		shape_panel.add(e_quad);
		shape_panel.add(e_cubic);
		//shape_panel.add(e_ortho);
		//fill_edges = new JCheckBox("fill edge shapes");
		//fill_edges.setSelected(false);
		//fill_edges.addActionListener(this);
		//shape_panel.add(fill_edges);
		shape_panel.setOpaque(true);
		// e_color = new JCheckBox("highlight edge weights");
		// e_color.addActionListener(this);
		e_labels = new JCheckBox("show edge weight values");
		e_labels.addActionListener(this);


		shape_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		edge_panel.add(shape_panel);
		//gradient_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		//edge_panel.add(gradient_panel);


		e_labels.setAlignmentX(Component.LEFT_ALIGNMENT);
		edge_panel.add(e_labels);

		// set up zoom controls
		zoom_at_mouse = new JCheckBox("<html><center>zoom at mouse<p>(wheel only)</center></html>");
		zoom_at_mouse.addActionListener(this);
		zoom_at_mouse.setSelected(true);

		final ScalingControl scaler = new CrossoverScalingControl();

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(Util.vv, 1.1f, Util.vv.getCenter());

			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(Util.vv, 1/1.1f, Util.vv.getCenter());

			}
		});

		JPanel zoomPanel = new JPanel();
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
		plus.setAlignmentX(Component.CENTER_ALIGNMENT);
		zoomPanel.add(plus);
		minus.setAlignmentX(Component.CENTER_ALIGNMENT);
		zoomPanel.add(minus);
		zoom_at_mouse.setAlignmentX(Component.CENTER_ALIGNMENT);
		zoomPanel.add(zoom_at_mouse);

		//Grid buttons
		set_grid = new JCheckBox("<html><center>Show grid</center></html>");
		set_grid.addActionListener(this);
		set_grid.setSelected(false);


		JButton plus_grid = new JButton("+");
		plus_grid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Util.stepX+=10;Util.stepY+=10; 
				Util.xlines=Util.vv.getWidth()/(Util.stepX+1)+1;
				Util.ylines=Util.vv.getHeight()/(Util.stepY+1)+1;
				Util.vv.repaint();
			}
		});
		JButton minus_grid = new JButton("-");
		minus_grid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Util.stepX>10)
					{Util.stepX-=10;Util.stepY-=10;
			  
				Util.xlines=Util.vv.getWidth()/(Util.stepX+1)+1;
				Util.ylines=Util.vv.getHeight()/(Util.stepY+1)+1;
				Util.vv.repaint();}
			}
		});
		JPanel gridPanel = new JPanel();
		gridPanel.setBorder(BorderFactory.createTitledBorder("Grid"));
		plus_grid.setAlignmentX(Component.CENTER_ALIGNMENT);
		gridPanel.add(plus_grid);
		minus_grid.setAlignmentX(Component.CENTER_ALIGNMENT);
		gridPanel.add(minus_grid);
		set_grid.setAlignmentX(Component.CENTER_ALIGNMENT);
		gridPanel.add(set_grid);
		snap_to_grid = new JCheckBox("<html><center>Snap to grid</center></html>");
		snap_to_grid.addActionListener(this);
		snap_to_grid.setSelected(false);
		snap_to_grid.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		gridPanel.add(snap_to_grid);

		JPanel fontPanel = new JPanel();
		// add font and zoom controls to center panel

		both_panel.add(zoomPanel);
		both_panel.add(gridPanel);
		both_panel.add(fontPanel);

		Util.modeBox = gm.getModeComboBox();
		Util.modeBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (Util.modeBox.getSelectedIndex()==0)
					Util.vv.getPickedEdgeState().clear();
				Util.vv.getPickedVertexState().clear();
			}
		});
		Util.modeBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel modePanel = new JPanel(new BorderLayout()) {
			public Dimension getMaximumSize() {
				return getPreferredSize();
			}
		};
		modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
		modePanel.add(Util.modeBox);
		JPanel comboGrid = new JPanel(new GridLayout(0,1));
		comboGrid.add(modePanel);
		fontPanel.add(comboGrid);



	}

	public void actionPerformed(ActionEvent e)
	{
		AbstractButton source = (AbstractButton)e.getSource();
		/* if (source == e_color)
        {
            ewcs.setWeighted(source.isSelected());
        }
        else if (source == v_stroke) 
        {
            vsh.setHighlight(source.isSelected());
        }
        else if (source == v_labels)
        {
            if (source.isSelected())
                Util.vv.getRenderContext().setVertexLabelTransformer(vs);
            else
                Util.vv.getRenderContext().setVertexLabelTransformer(vs_none);
        }
        else 
        if (source == e_labels)
        {
            if (source.isSelected())
                Util.vv.getRenderContext().setEdgeLabelTransformer(es);
            else
                Util.vv.getRenderContext().setEdgeLabelTransformer(es_none);
        }
        else if (source == e_uarrow_pred)
        {
            show_arrow.showUndirected(source.isSelected());
        }
        else if (source == e_darrow_pred)
        {
            show_arrow.showDirected(source.isSelected());
        }
        else if (source == font)
        {
            vff.setBold(source.isSelected());
            eff.setBold(source.isSelected());
        }
        else if (source == v_shape)
        {
            vssa.useFunnyShapes(source.isSelected());
        }
        else if (source == v_size)
        {
            vssa.setScaling(source.isSelected());
        }
        else*/
		if (source == set_grid)
		{ if (source.isSelected())
			Util.grid=true;
		else
			Util.grid=false;
		}
		else
			if (source ==snap_to_grid)
			{	if (source.isSelected())
				// if (!startEditing)//snap to grid
			{startEditing=true;
			Util.vv.setGraphLayout(new SnapToGridStaticLayout(Util.g));
			}
			else
				Util.vv.setGraphLayout(new NoSnapToGridStaticLayout(Util.g));}
			else
				if  (source == e_labels)
					if (source.isSelected())
					{Util.vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());}
					else
						Util.vv.getRenderContext().setEdgeLabelTransformer(new ConstantTransformer(null));

				else
					if (source == abc_names) 
						if (source.isSelected())
						{Species.name=true;}
						else
						{Species.name=false;}

					else if (source == e_line) 
					{
						if(source.isSelected())
						{
							Util.vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Vertex,Edge>(){
								public Shape transform(Context<Graph<Vertex,Edge>,Edge> context){
									Edge edge = (Edge)context.element;
									if (Util.vv.getPickedEdgeState().isPicked(edge))
									{edge.edited=false; edge.isLine=true;
									edge.lineType=Util.LineType.lineType;
									return super.transform(context);
									}
									else
										if(edge.lineType.equals(Util.LineType.quadType))
												return quadTransf.transform(context);
										else
											return cubicTransf.transform(context);
								}

							});
						}}

					else if (source == e_quad) 
					{
						if(source.isSelected())
						{
							//Util.vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<Vertex,Edge>());
							Util.vv.getRenderContext().setEdgeShapeTransformer(quadTransf);
						}
					}
					else if (source == e_cubic) 
					{
						if(source.isSelected())
						{
							Util.vv.getRenderContext().setEdgeShapeTransformer(cubicTransf);
						}
					}

					else if(source == zoom_at_mouse)
					{
						gm.setZoomAtMouse(source.isSelected());
					} 
					else if (source == no_gradient) {
						if (source.isSelected()) {
							gradient_level = GRADIENT_NONE;
						}
						//		} else if (source == gradient_absolute) {
						//			if (source.isSelected()) {
						//				gradient_level = GRADIENT_ABSOLUTE;
						//			}
					} 
					else if (source == gradient_relative) {
						if (source.isSelected()) {
							gradient_level = GRADIENT_RELATIVE;
						}
					}
					else if (source == fill_edges)
					{
						if(source.isSelected()) {
							Util.vv.getRenderContext().setEdgeFillPaintTransformer( edgeFillPaint );
						} else {
							Util.vv.getRenderContext().setEdgeFillPaintTransformer( new ConstantTransformer(null) );
						}
						//            edgePaint.useFill(source.isSelected());
					}
		Util.vv.repaint();
	}

	private  class VertexShapeSizeAspect<V>  extends AbstractVertexShapeTransformer <V>
	{
		public VertexShapeSizeAspect(){
			setSizeTransformer(new Transformer<V,Integer>() {

				public Integer transform(V v) {
					if (v instanceof ControlPoint){
						Edge e=((ControlPoint) v).getEdge();
						//if ((e!=null)&&(!(e.isLine))&&(Util.vv.getPickedEdgeState().getPicked().contains(e)))
						if ((e!=null)&&(Util.vv.getPickedEdgeState().getPicked().contains(e)))
							if ((e.lineType.equals(Util.LineType.cubicType)&& (((ControlPoint) v).index<3)))
							  return 10;
							else
						    if ((e.lineType.equals(Util.LineType.quadType)&& (((ControlPoint) v).index==3)))
							  return 10;
						    else     return 0;
						else return 0;	
					}
					else return 30;
				}});
		}
		@Override
		public Shape transform(V v) {
			if (v instanceof ControlPoint)
				return factory.getEllipse(v);

			return (v instanceof Species)? factory.getEllipse(v) :factory.getRectangle(v) ;
		}

	}
	public class GradientPickedEdgePaintFunction extends GradientEdgePaintTransformer<Vertex,Edge> 
	{
		private Transformer<Edge,Paint> defaultFunc;
		protected boolean fill_edge = false;
		Predicate<Context<Graph<Vertex,Edge>,Edge>> selfLoop = new SelfLoopEdgePredicate<Vertex,Edge>();
		VisualizationViewer<Vertex,Edge> vv;

		public GradientPickedEdgePaintFunction(Transformer<Edge,Paint> defaultEdgePaintFunction, 
				VisualizationViewer<Vertex,Edge> vv) 
		{ 
			super(Color.YELLOW, Color.BLACK, vv);
			this.defaultFunc = defaultEdgePaintFunction;
			this.vv=vv;
		}

		public void useFill(boolean b)
		{
			fill_edge = b;
		}

		public Paint transform(Edge e) {

			if (gradient_level == GRADIENT_NONE) {
				return defaultFunc.transform(e);
			} else {
				return super.transform(e);
			}
		}

		protected Color getColor2(Edge e)
		{
			return Util.vv.getPickedEdgeState().isPicked(e)? Color.RED : c2;
		}



	}

	class ControlPickableVertexPaintTransformer extends PickableVertexPaintTransformer<Vertex>{
		//change position of control points - applies only to these points
		public ControlPickableVertexPaintTransformer(PickedInfo<Vertex> pi, Paint fill_paint, Paint picked_paint){
			super(pi, fill_paint, picked_paint);
		}

		public Point2D scaledCoordinates(Edge e, Point2D p){
			//get the point p in scaled coordinates relative to the edge e
			Layout<Vertex,Edge> layout = Util.vv.getModel().getGraphLayout();
			Pair<Vertex> endpoints=Util.g.getEndpoints(e);
			Vertex v1 = endpoints.getFirst();
			Vertex v2 = endpoints.getSecond();

			Point2D p1 = layout.transform(v1);
			Point2D p2 = layout.transform(v2);
			p1 = Util.vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
			p2 = Util.vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
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
			Point2D pp = new Point2D.Double();
			try {pp=xform.inverseTransform(p, pp);}
			catch(NoninvertibleTransformException ex)
			{System.err.println(ex);}

			return pp;
		}
		public Paint transform(Vertex v ){
			if ((Util.vv.getPickedVertexState().isPicked(v)) &&   (v instanceof ControlPoint)){
				Edge e=((ControlPoint) v).getEdge();
				Layout<Vertex,Edge> layout = Util.vv.getModel().getGraphLayout();
				if (((ControlPoint) v).index==1)
				{e.ctrl1=scaledCoordinates(e,layout.transform(v));
				}
				else 
					if (((ControlPoint) v).index==2)
				{e.ctrl2=scaledCoordinates(e,layout.transform(v));}
				else
				{e.ctrl3=scaledCoordinates(e,layout.transform(v));}
			}

			return super.transform(v);
		}
	}
 
	
	 
	class SnapToGridStaticLayout extends StaticLayout<Vertex, Edge>{
		public SnapToGridStaticLayout(Graph<Vertex, Edge> g){
			super(g, Util.vv.getGraphLayout());
		}
		public Point2D transform(Vertex v){
			Point2D p=super.transform(v);
			if (!(v instanceof ControlPoint))
				p.setLocation(Util.stepX*Math.round(p.getX() / Util.stepX) , Util.stepY*(Math.round(p.getY() / Util.stepY)));
			//   p=Util.vv.getRenderContext().getMultiLayerTransformer().transform(p);
			return p;
		}
	}
	class NoSnapToGridStaticLayout extends StaticLayout<Vertex, Edge>{
		public NoSnapToGridStaticLayout(Graph<Vertex, Edge> g){
			super(g, Util.vv.getGraphLayout());

		}
		public Point2D transform(Vertex v){
			Point2D p=super.transform(v);
			return p;
		}
	}

}
