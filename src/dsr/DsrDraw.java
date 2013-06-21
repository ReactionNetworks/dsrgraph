package dsr;
import javax.jnlp.*;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;

import java.awt.Stroke;
import java.awt.Shape;

import java.awt.datatransfer.StringSelection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.geom.AffineTransform;

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import java.io.ByteArrayInputStream;
import java.io.IOException;


import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import javax.swing.JTextArea;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;

import edu.uci.ics.jung.algorithms.layout.Layout;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;

import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;

import edu.uci.ics.jung.graph.util.Pair;

import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

import edu.uci.ics.jung.visualization.control.ScalingControl;

import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;

import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import edu.uci.ics.jung.visualization.util.DefaultChangeEventSupport;

public class DsrDraw extends JApplet implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	JScrollPane scrollPane;
	protected JRadioButton no_gradient;

	protected JRadioButton gradient_relative;
	JButton coordB,hideLatex; 
	protected static final int GRADIENT_NONE = 0;
	protected static final int GRADIENT_RELATIVE = 1;

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
		jf.setPreferredSize(new Dimension(1200,700));
		jf.pack();
		jf.setVisible(true);

	}  
	public void init(){
		/*try
		{
		  //Tell the UIManager to use the platform look and feel
		  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
		  //Do nothing
		}
*/
		Container content = getContentPane();
		content.setBackground(Color.LIGHT_GRAY);
		resize(1000,700);
		content.setPreferredSize(new Dimension(1000,700));
		
		JPanel jp=new DsrDraw().startFunction(getParameter("content"));
		
		content.add(jp);
		
	}

	public JPanel startFunction(String s){   
		//Util.g = Util.readGraphFromFile(s);
		Util.stepX=50; Util.stepY=50;
		Util.xlines=30;Util.ylines=30;
		Util.g = Util.readGraphFromContent(s);
	
		Layout<Vertex,Edge> frl=new FRLayout<Vertex,Edge>(Util.g);
	
		Util.vv = new VisualizationViewer<Vertex,Edge>(frl);
		BasicGridRenderer<Vertex,Edge> bb=new BasicGridRenderer<Vertex,Edge>();
	
		Util.vv.setRenderer(bb);
		
		



	
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
		 Util.vv.getRenderer().setEdgeLabelRenderer(new AdaptableLabelRenderer());
	
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
		jp.setName("Chemical Reaction Network");
		jp.setLayout(new BorderLayout());

		Util.vv.setBackground(Color.white);
		GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(Util.vv);
		JPanel title=new JPanel();
		title.setBackground(Color.GRAY);
		title.setPreferredSize(new Dimension(100, 5));
		title.setForeground(Color.WHITE);
		JLabel title_label=new JLabel("CoNtRol - Chemical Reaction Network");
		title_label.setForeground(Color.RED);
		title.add(title_label);

		jp.add(scrollPane);
		
		jp.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));

      
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
		//final Box vertex_panel = Box.createHorizontalBox();
		//vertex_panel.setBorder(BorderFactory.createTitledBorder("Weight"));
		JPanel shape_panel = new JPanel();
		shape_panel.setLayout(new BoxLayout(shape_panel, BoxLayout.LINE_AXIS));
		shape_panel.setBorder(BorderFactory.createTitledBorder("Edge shape"));
	
		JPanel zoomPanel = new JPanel();
		zoomPanel.setLayout(new BoxLayout(zoomPanel, BoxLayout.LINE_AXIS));
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.LINE_AXIS));
		gridPanel.setBorder(BorderFactory.createTitledBorder("Grid"));
 
		JLabel intro=new JLabel("CoNtRol", JLabel.CENTER);
		intro.setForeground(new Color(49,101,223));
		intro.setFont(new Font("Bookman", Font.BOLD,18));
		control_panel.add(intro);
		//control_panel.add(new JSeparator(SwingConstants.VERTICAL));
		//control_panel.add(new JSeparator(SwingConstants.VERTICAL));
				
		
	
		control_panel.add(shape_panel);
		control_panel.add(zoomPanel);
		control_panel.add(gridPanel);
	

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


        final DefaultModalGraphMouse<Integer,Number> graphMouse = new DefaultModalGraphMouse<Integer,Number>();
        Util.vv.setGraphMouse(graphMouse);
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        
        //TODO: verifica ce e cu girdul care se deplaseaza si el
        //p.lock(true) - poate e o solutie
        //TODO: transofrmarile din 1-0 in LAYOUT, view 
        //care e legatura cu render-ul
        //cand fac transformarea de translatre , ulterior punctele nu se mai afiseaza bine
        //desi se porneste de la 0 1 corect, tranformarea in coordonate ecran nu merge
        
        JPanel modePanel = new JPanel(new GridLayout(1,1));
   //     modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
       // modePanel.add(graphMouse.getModeComboBox());
        control_panel.add(modePanel);
		//modePanel.add(Util.modeBox);
		coordB=new JButton("ExportToLatex");
		coordB.addActionListener(this);
	    hideLatex=new JButton("HideLatex");
	    hideLatex.addActionListener(this);
		control_panel.add(coordB);
		control_panel.add(hideLatex);
		latex = new JTextArea(5, 30);
		scrollPane = new JScrollPane(latex);
		latex.setLineWrap(true);
		
		latex.setEditable(true);
		 jp.add(scrollPane, BorderLayout.EAST);

	}

	public void actionPerformed(ActionEvent e)
	{
		AbstractButton source = (AbstractButton)e.getSource();
		
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
				Util.vv.getRenderContext().setEdgeLabelTransformer(
						new ConstantTransformer(null));

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
						
					} 
					else if (source == gradient_relative) {
						if (source.isSelected()) {
							gradient_level = GRADIENT_RELATIVE;
						}
					}
					else
						if(source==coordB)
							{String lt=Util.exportToLatex();
							latex.setText(lt);
							final ClipboardService cs;
							final FileSaveService fos;
					    try{
							cs = (ClipboardService)ServiceManager.lookup("javax.jnlp.ClipboardService");
							if(cs!=null)
						    	 cs.setContents(new StringSelection(lt));
							FileContents fc ;
							fos = (FileSaveService)ServiceManager.lookup("javax.jnlp.FileSaveService");
							if(fos!=null)
								fc = fos.saveFileDialog(null, null,
				                        new ByteArrayInputStream(lt.getBytes()), null);
					    }
					    catch (UnavailableServiceException ee){
					    	ee.printStackTrace();
					    }
					    catch(IOException ioe){
					    	ioe.printStackTrace(System.out);
					    }
						    
						     
						    
							}
		
					else
                       if(source==hideLatex)
                    	   if(hideLatex.getText().equals("HideLatex"))
                    			   {hideLatex.setText("ShowLatex");
                    	            scrollPane.setVisible(false);
                    	            }
                    	   else
                    	   {hideLatex.setText("HideLatex");
           	                scrollPane.setVisible(true);
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
	

	class ControlPickableVertexPaintTransformer extends PickableVertexPaintTransformer<Vertex>{
		//change position of control points - applies only to these points
		//according to control point position, the edge's shape is changed
		public ControlPickableVertexPaintTransformer(PickedInfo<Vertex> pi, Paint fill_paint, Paint picked_paint){
			super(pi, fill_paint, picked_paint);
		}

		public Point2D scaledCoordinates(Edge e, Point2D p){
			//get the point p in scaled coordinates relative to the edge e
			Layout<Vertex,Edge> layout = Util.vv.getModel().getGraphLayout();
			
			Pair<Vertex> endpoints=Util.g.getEndpoints(e);
			if(endpoints.equals(null))
				return null;
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
			   if  (Util.vv.getPickedEdgeState().isPicked(e))//control point +its edges are selected
			   {
				Layout<Vertex,Edge> layout = Util.vv.getModel().getGraphLayout();
				Point2D pp=scaledCoordinates(e,layout.transform(v));
				if(pp.equals(null))
					return super.transform(v);
				if (((ControlPoint) v).index==1)
				e.ctrl1=pp;
				else 
				if (((ControlPoint) v).index==2)
				  e.ctrl2=pp;
				else
				  e.ctrl3=pp;
			   }
			}

			return super.transform(v);
		}
	}
 
	
	 
	class SnapToGridStaticLayout extends ObservableCachingLayout<Vertex, Edge>  {
		//most probable this should be added to some pluggable transformer, based on events
		//compute it only if chagefired on that node
		public SnapToGridStaticLayout(Graph<Vertex, Edge> g){
			super(new StaticLayout<Vertex,Edge>(g, Util.vv.getGraphLayout()));
		//	addChangeListener(this);
		}
		/*public void stateChanged(ChangeEvent e)
		{ if (e.getSource() instanceof Vertex)
		{	Vertex v=(Vertex)e.getSource();
		    if (!(v instanceof ControlPoint))
			 {Point2D p=super.transform((Vertex)e.getSource());
		      p=Util.vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p);
			  p.setLocation(Util.stepX*Math.round(p.getX() / Util.stepX) , Util.stepY*(Math.round(p.getY() / Util.stepY)));
		      p=Util.vv.getRenderContext().getMultiLayerTransformer().transform(p);
		      setLocation(v,p);
			}
		
		}
		}*/
		public Point2D transform(Vertex v){
		
			Point2D p=super.transform(v);
			if (!(v instanceof ControlPoint))
				{p=Util.vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p);
				p.setLocation(Util.stepX*Math.round(p.getX() / Util.stepX) , Util.stepY*(Math.round(p.getY() / Util.stepY)));
			   p=Util.vv.getRenderContext().getMultiLayerTransformer().transform(p);
			   //from layout to screen
				}
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
