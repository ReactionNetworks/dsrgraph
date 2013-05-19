package dsr;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.Shape;
import java.awt.GradientPaint;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

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
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.GradientEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer.InsidePositioner;

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
	protected JTextArea latex;

	protected JRadioButton no_gradient;
	//		protected JRadioButton gradient_absolute;
	protected JRadioButton gradient_relative;
	JButton coordB; 
	protected static final int GRADIENT_NONE = 0;
	protected static final int GRADIENT_RELATIVE = 1;
	//		protected static final int GRADIENT_ABSOLUTE = 2;
	protected static int gradient_level = GRADIENT_NONE;

	protected DefaultModalGraphMouse<Vertex,Edge> gm;

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
		jf.setPreferredSize(new Dimension(1000,700));
		jf.pack();
		jf.setVisible(true);

	}  
	public void init(){
		// WindowUtilities.setNativeLookAndFeel();
		Container content = getContentPane();
		content.setBackground(Color.LIGHT_GRAY);
		resize(1000,700);
		content.setPreferredSize(new Dimension(1000,700));
		JPanel jp=new DsrDraw().startFunction(getParameter("content"));
		
		content.add(jp);
		
	}
	//create a grid layout
	public JPanel startFunction(String s){   
		//Util.g = Util.readGraphFromFile(s);
		
		Util.g = Util.readGraphFromContent(s);
		System.out.println(Util.g);
		Layout<Vertex,Edge> frl=new FRLayout<Vertex,Edge>(Util.g);
	
		Util.vv = new VisualizationViewer<Vertex,Edge>(frl);
		BasicGridRenderer<Vertex,Edge> bb=new BasicGridRenderer<Vertex,Edge>();
		
		Util.vv.setRenderer(bb);
		
		Util.stepX=50; Util.stepY=50;
		Util.xlines=30;Util.ylines=30;



	
		vsta=new VertexShapeSizeAspect<Vertex>();
		snapToGridLayout=new SnapToGridStaticLayout(Util.g);//,Util.vv.getGraphLayout());
		nosnapToGridLayout=new NoSnapToGridStaticLayout(Util.g);//,Util.vv.getGraphLayout());
		cubicTransf=new CubicCurveTransformer(Util.vv);
		quadTransf=new QuadCurveTransformer(Util.vv);
		 Util.vv.getRenderer().setVertexRenderer(
                 new GradientVertexRenderer<Vertex,Edge>(
                                 new Color(200,170,170), new Color(250,230,230), 
                                 Color.red, Color.red,
                                 Util.vv.getPickedVertexState(),
                                 false));

		Util.vv.getRenderContext().setVertexDrawPaintTransformer(new ControlPickableVertexPaintTransformer(Util.vv.getPickedVertexState(),Color.RED, Color.WHITE));
		Util.vv.getRenderContext().setVertexShapeTransformer(vsta);
		Util.vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		Util.vv.getRenderContext().setVertexFontTransformer(new ConstantTransformer( new Font("Helvetica", Font.BOLD,12)));
		Util.vv.getRenderContext().getEdgeLabelRenderer().setRotateEdgeLabels(false);
		Util.vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Vertex>());
		 Util.vv.getRenderContext().setLabelOffset(2);
		
	/*	Util.vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Vertex,Paint>(){
			public Paint transform(Vertex v){
				if (v instanceof ControlPoint)
					return Color.green;
				else return new GradientPaint(0,0,Color.LIGHT_GRAY,30, 0,Color.RED); 
			}
		});
*/
		Util.vv.getRenderContext().setEdgeStrokeTransformer(new Transformer<Edge,Stroke>()
				{ public Stroke transform(Edge e){
					return (e.sgn>0)? new BasicStroke(1): RenderContext.DASHED;}
				}
				);
		//Util.vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<Vertex,Edge>());		 
		Util.vv.getRenderContext().setEdgeShapeTransformer(new MultipleEdgeFirstTransformer());		 
		Util.vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Edge>());
		
		 Util.vv.setEdgeToolTipTransformer(new Transformer<Edge,String>() {
			 			public String  transform(Edge edge) {
                             return "E" +edge.id+Util.g.getEndpoints(edge).toString();
			 			}});
		JPanel jp = new JPanel();
		jp.setName("Chemical Reaction Networlk");
		jp.setLayout(new BorderLayout());

		Util.vv.setBackground(Color.white);
		GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(Util.vv);
		JPanel title=new JPanel();
		title.setBackground(Color.GRAY);
		title.setPreferredSize(new Dimension(100, 5));
		title.setForeground(Color.WHITE);
		JLabel title_label=new JLabel("CoNtRoL - Chemical Reaction Network");
		title_label.setForeground(Color.RED);
		title.add(title_label);
		//jp.add(title, BorderLayout.NORTH);
		jp.add(scrollPane);
		
		jp.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		gm = new DefaultModalGraphMouse<Vertex,Edge>();
        
		Util.vv.setGraphMouse(gm);
       
        gm.setMode(Mode.PICKING);
      
		addBottomControls( jp );Util.ctrl1= new ControlPoint((byte) 1);
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
		jp.add(control_panel, BorderLayout.NORTH);
		//control_panel.setBackground(Color.DARK_GRAY);
		control_panel.setLayout(new BoxLayout(control_panel, BoxLayout.LINE_AXIS));   //differnt
		final Box vertex_panel = Box.createHorizontalBox();
		vertex_panel.setBorder(BorderFactory.createTitledBorder("Weight"));
		JPanel shape_panel = new JPanel();
		shape_panel.setLayout(new BoxLayout(shape_panel, BoxLayout.LINE_AXIS));
		shape_panel.setBorder(BorderFactory.createTitledBorder("Edge shape"));
	
		JPanel zoomPanel = new JPanel();
		zoomPanel.setLayout(new BoxLayout(zoomPanel, BoxLayout.LINE_AXIS));
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.LINE_AXIS));
		gridPanel.setBorder(BorderFactory.createTitledBorder("Grid"));
 
		JLabel intro=new JLabel("CoNtRoL", JLabel.CENTER);
		intro.setForeground(new Color(49,101,223));
		intro.setFont(new Font("Bookman", Font.BOLD,18));
		control_panel.add(intro);
		//control_panel.add(new JSeparator(SwingConstants.VERTICAL));
		//control_panel.add(new JSeparator(SwingConstants.VERTICAL));
				
		
		control_panel.add(vertex_panel);
		control_panel.add(shape_panel);
		control_panel.add(zoomPanel);
		control_panel.add(gridPanel);
	

		//abc_names = new JCheckBox("ABC");
		//abc_names.addActionListener(this);
		e_labels = new JCheckBox("show edge weight values");
		e_labels.setSelected(true);
		e_labels.addActionListener(this);
	//	vertex_panel.add(e_labels);

		// set up edge controls

		e_line = new JRadioButton("line");
		e_line.addActionListener(this);
		e_line.setSelected(true);
		e_quad = new JRadioButton("quad");
		e_quad.addActionListener(this);
		e_cubic = new JRadioButton("cubic");
		e_cubic.addActionListener(this);

		ButtonGroup bg_shape = new ButtonGroup();
		bg_shape.add(e_line);
		bg_shape.add(e_quad);
		bg_shape.add(e_cubic);
	
		shape_panel.add(e_line);
		shape_panel.add(e_quad);
		shape_panel.add(e_cubic);
		shape_panel.setOpaque(true);
		

		
	//	zoom_at_mouse = new JCheckBox("<html><center>zoom at (wheel) mouse<p></center></html>");
		//zoom_at_mouse.addActionListener(this);
		//zoom_at_mouse.setSelected(true);

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

		zoomPanel.add(plus);
		zoomPanel.add(minus);
		//zoomPanel.add(zoom_at_mouse);

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
		gridPanel.add(plus_grid);
		gridPanel.add(minus_grid);
		gridPanel.add(set_grid);
		snap_to_grid = new JCheckBox("<html><center>Snap to grid</center></html>");
		snap_to_grid.addActionListener(this);
		snap_to_grid.setSelected(false);
		gridPanel.add(snap_to_grid);

		
		//modePanel.add(Util.modeBox);
		coordB=new JButton("ExportToLatex");
		coordB.addActionListener(this);
	
		control_panel.add(coordB);
		latex = new JTextArea(5, 30);
		JScrollPane scrollPane = new JScrollPane(latex);
		latex.setLineWrap(true);
		latex.setEditable(false);
		 jp.add(scrollPane, BorderLayout.EAST);

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
					else
						if(source==coordB)
							latex.setText(Util.exportToLatex());
							
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
