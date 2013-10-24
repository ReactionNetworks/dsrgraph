package dsr;


import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.swing.JComboBox;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public static int stepX, stepY, xlines, ylines;
	static boolean grid;
	public static Graph<Vertex, Edge> g;

	public enum LineType {
		lineType, quadType, cubicType
	};

	static public boolean allOne = true;
	static public boolean toLatex=false;

	public static VisualizationViewer<Vertex, Edge> vv;
	public static ControlPoint ctrl1, ctrl2, ctrl3;
	// public static char modalMouse='p';
	//public static JComboBox modeBox;

	public static Graph<Vertex, Edge> readGraphFromContent(String content) {
		Graph<Vertex, Edge> graph = new SparseMultigraph<Vertex, Edge>();
		Factory<Integer> idFactory = new Factory<Integer>() {
			int count;

			public Integer create() {
				return count++;
			}
		};
	
	
		// the Vertex for the species
		Map<String, Species> sps = new HashMap<String, Species>();

		short in = 0;
		short type;// the type of interaction
		StringTokenizer st = new StringTokenizer(content, ".");
		// one interaction
		while (st.hasMoreTokens()) {
			String reaction = st.nextToken();
			Interaction ini = new Interaction(in++);
			if (reaction.contains("<-->"))
				type = 0;// double direction
			else if (reaction.contains("<--"))
				type = 1;
			else
				type = 2;

			String parts[] = reaction.split("[<]*--[>]*");
			String[][] species = new String[2][];
			for (int i = 0; i < 2; i++)// left or right part of reactions
			{
				species[i] = parts[i].split("\\+");

			}
			List<Map<String, Short>> result = computeMatrix(species, type);
			Map<String, Short> matrix1 = result.get(0);
			Map<String, Short> matrix2 = result.get(1);
			Set<String> allSpecies = new HashSet<String>();
			allSpecies.addAll(matrix2.keySet());
			for (String s : allSpecies) {
				if (!(sps.containsKey(s))) {
					sps.put(s, new Species(s));
				}
				// from reaction to species
				if (matrix1.get(s) != 0) {
					if ((matrix1.get(s) * matrix2.get(s) > 0)
							& (matrix2.get(s) != 2)) // have the same sign, but
														// the second matrix is
														// not 2
						graph.addEdge(new Edge((short) matrix1.get(s),
								(int) idFactory.create()), ini, sps.get(s),
								EdgeType.UNDIRECTED);

					else // different signs and matrix!=0
					if (matrix2.get(s) == 0) // there are both signs
						if (matrix1.get(s) > 0) // ramane o michie negativa
						{
							graph.addEdge(new Edge((short) matrix1.get(s),
									(int) idFactory.create(), true), ini, sps
									.get(s), EdgeType.UNDIRECTED);
							graph.addEdge(new Edge((short) (-100),
									(int) idFactory.create(), true),
									sps.get(s), ini, EdgeType.DIRECTED);

						} else {
							graph.addEdge(new Edge((short) matrix1.get(s),
									(int) idFactory.create(), true), ini, sps
									.get(s), EdgeType.UNDIRECTED);
							graph.addEdge(new Edge((short) (100),
									(int) idFactory.create(), true),
									sps.get(s), ini, EdgeType.DIRECTED);

						}
					else // matrix2<>0 matrix1<>0
					if (matrix2.get(s) == 2)
						// matrix2 is 2 - meaning there is no edge from S to R,
						// but there is one from R to S
						graph.addEdge(new Edge((short) (matrix1.get(s)),
								(int) idFactory.create()), ini, sps.get(s),
								EdgeType.DIRECTED);
					else // m1<>0 m2<>0 m2<>nothing m1.sign<>m2.sign the value
							// of ra is the value of ra????
					{
						graph.addEdge(new Edge((short) (matrix1.get(s)),
								(int) idFactory.create(), true), ini, sps
								.get(s), EdgeType.DIRECTED);
						graph.addEdge(new Edge((short) (matrix2.get(s)),
								(int) idFactory.create(), true), sps.get(s),
								ini, EdgeType.DIRECTED);

					}
				} else
				// in the first matrix there is no value for R-to-S, so the
				// value of S-to-R will be inf
				if (matrix2.get(s) == 0) // both signs
				{
					graph.addEdge(
							new Edge((short) 100, (int) idFactory.create(),
									true), sps.get(s), ini, EdgeType.DIRECTED);
					graph.addEdge(
							new Edge((short) (-100), (int) idFactory.create(),
									true), sps.get(s), ini, EdgeType.DIRECTED);

				} else if (matrix2.get(s) != 0)
					graph.addEdge(new Edge((short) (matrix2.get(s) * 100),
							(int) idFactory.create()), sps.get(s), ini,
							EdgeType.DIRECTED);

				// else //deal separately with infinit values
				{
				}// graph.addEdge(new Edge((short)(matrix2.get(s)*100), (int)
					// idFactory.create()), sps.get(s),ini, EdgeType.DIRECTED );

			}

			// }
			// }
		}
		
		return graph;
	}

	public static List<Map<String, Short>> computeMatrix(String[][] species,
			int type) {
		Map<String, Short> matrix1 = new HashMap<String, Short>(); 
		Map<String, Short> matrix2 = new HashMap<String, Short>(); 
		List<Map<String, Short>> spCard = new ArrayList<Map<String, Short>>();
	
		for (int i = 0; i < 2; i++)// left/right part of reaction
		{
			Map<String, Short> part = new HashMap<String, Short>();
			for (int k = 0; k < species[i].length; k++) //
			{
				String csp = species[i][k].trim();// check if there is only a name or also a number
				short coef = 1;
				if (csp.matches("[0-9]+[a-zA-Z]+.*")) {
					String pp[] = csp.split("[a-zA-Z]");
					csp = csp.substring(pp[0].length());
					coef = Short.parseShort(pp[0]);
				}
				if (!(part.containsKey(csp))) {
					part.put(csp, coef);
				} else
					part.put(csp, (short) (1 + part.get(csp)));
			}
			spCard.add(part);
		}

		Set<String> allSpecies = new HashSet<String>();
		allSpecies.addAll(spCard.get(0).keySet());
		allSpecies.addAll(spCard.get(1).keySet());
		for (String s : allSpecies) {
			if (spCard.get(0).containsKey(s))
				if (spCard.get(1).containsKey(s))
					matrix1.put(s, (short) (spCard.get(1).get(s) - spCard
							.get(0).get(s)));
				else
					matrix1.put(s, (short) (-spCard.get(0).get(s)));
			else
				matrix1.put(s, spCard.get(1).get(s));

		}
		byte v = 0;
		// binary coding: 00 - nothing, 01 - minus, 10 - plus, 11 - both
		for (String s : allSpecies) {
			v = 0;
			if (((type == 0) || (type == 2)) & (spCard.get(0).containsKey(s))) // from
																				// left
																				// to
																				// right
				v += 1;// -
			if (((type == 0) || (type == 1)) & (spCard.get(1).containsKey(s)))
				v += 2;// +
			
			switch (v) {
			case 0:
				matrix2.put(s, (short) 2);
				break;
			case 1:
				matrix2.put(s, (short) -1);
				break; 
			case 2:
				matrix2.put(s, (short) 1);
				break; 
			case 3:
				matrix2.put(s, (short) 0);
				break; 
			}
		}
		
		List<Map<String, Short>> result = new ArrayList<Map<String, Short>>();
		result.add(matrix1);
		result.add(matrix2);
		return result;

	}

	static public Point2D affineTransform(Edge edge, Point2D p) {
		
		Layout<Vertex, Edge> layout = Util.vv.getModel().getGraphLayout();

		Pair<Vertex> endpoints = Util.g.getEndpoints(edge);
		Vertex v1 = endpoints.getFirst();
		Vertex v2 = endpoints.getSecond();

		Point2D p1 = layout.transform(v1);
		Point2D p2 = layout.transform(v2);
		p1 = Util.vv.getRenderContext().getMultiLayerTransformer()
				.transform(Layer.LAYOUT, p1);
		p2 = Util.vv.getRenderContext().getMultiLayerTransformer()
				.transform(Layer.LAYOUT, p2);
		float x1 = (float) p1.getX();
		float y1 = (float) p1.getY();
		float x2 = (float) p2.getX();
		float y2 = (float) p2.getY();
		AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);
		float dx = x2 - x1;
		float dy = y2 - y1;
		float thetaRadians = (float) Math.atan2(dy, dx);
		xform.rotate(thetaRadians);
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		xform.scale(dist, 1.0);
		Point2D pp = new Point2D.Double();
		return xform.transform(p, pp);
	}

	static public String exportToLatex() {
		String nodes = "\\def\\nodes{";
		// vv.getWidth();
		int scale = 20;
		Species.name = true;
		Interaction.name = true;
		Util.toLatex=true;
		Map<Vertex, Integer> indexes = new HashMap<Vertex, Integer>();
		int id = 1;
		for (Vertex vertex : Util.g.getVertices()) {
			if (vertex instanceof Species) {
				nodes += vertex.toString()
						+ "/{("
						+ Math.round(vv.getGraphLayout().transform(vertex).getX()/ scale)
						+ ",-"
						+ Math.round(vv.getGraphLayout().transform(vertex).getY()/ scale)
						+ ")}/species,";
				indexes.put(vertex, id++);
			} else if (vertex instanceof Interaction) {
				nodes += vertex.toString()
						+ "/{("
						+ Math.round(vv.getGraphLayout().transform(vertex).getX()/ scale)
						+ ",-"
						+ Math.round(vv.getGraphLayout().transform(vertex).getY()/ scale)
						+ ")}/reaction,";
				indexes.put(vertex, id++);
			}
		}
		nodes = nodes.subSequence(0, nodes.length() - 1) + "}";
		
		String edges = "\\def\\edges{";
		Species.name = false;
		Interaction.name = false;
		for (Edge edge : Util.g.getEdges()) {
			String arcType = "";
			arcType = (g.getEdgeType(edge).equals(EdgeType.DIRECTED)) ? "arcD": "arcU";
			arcType += (edge.sgn > 0) ? "S" : "D";
			Pair<Vertex> pv = Util.g.getEndpoints(edge);
			edges += indexes.get(pv.getFirst()) + "/"
					+ indexes.get(pv.getSecond()) + "/" + arcType + "/";
			double p1x, p1y, p2x, p2y;
			p1x = Util.vv.getGraphLayout().transform(pv.getFirst()).getX();
			p1y = Util.vv.getGraphLayout().transform(pv.getFirst()).getY();
			p2x = Util.vv.getGraphLayout().transform(pv.getFirst()).getX();
			p2y = Util.vv.getGraphLayout().transform(pv.getFirst()).getY();

			if (edge.lineType.equals(Util.LineType.lineType)) {
				edges += "1/"
						+ edge.toString()
						+ "/{("
						+ Math.round((Math.min(p1x, p2x) + Math.abs(p1x - p2x) / 2) / scale)
						+ ","
						+ Math.round(-(Math.min(p1y, p2y) + Math.abs(p1y - p2y) / 2) / scale)
						+ ")}/{("
						+ Math.round((Math.min(p1x, p2x) + Math.abs(p1x - p2x) / 2) / scale)
						+ ","
						+ Math.round(-(Math.min(p1y, p2y) + Math.abs(p1y - p2y) / 2) / scale) + ")},";
			} else {
				Point2D cp1, cp2;
				if (edge.lineType.equals(Util.LineType.cubicType)) {
					cp1 = Util.affineTransform(edge, edge.ctrl1);
					cp2 = Util.affineTransform(edge, edge.ctrl2);
				} else {
					
					
					double cp1x, cp2x, cp1y, cp2y;
					cp1x = 0.66 * edge.ctrl3.getX();
					cp1y = 0.66 * edge.ctrl3.getY();
					cp2x = 0.66 * edge.ctrl3.getX() + 0.33;
					cp2y = 0.66 * edge.ctrl3.getY() + 0.33;
					cp1 = Util.affineTransform(edge, new Point2D.Double(cp1x, cp1y));
					cp2 = Util.affineTransform(edge, new Point2D.Double(cp2x, cp2y));
				}
				edges += "3/"
						+ edge.toString()+
						"/{(" + Math.round(cp1.getX() / scale) + ",-"
						+ Math.round(cp1.getY() / scale) + ")}/{("
						+ Math.round(cp2.getX() / scale) + ",-"
						+ Math.round(cp2.getY() / scale) + ")},";
			}

		}
		edges = edges.subSequence(0, edges.length() - 1) + "}";
		
		Species.name = true;
		Interaction.name = true;
		toLatex=false;
		return getFirstPart()+nodes+"\n"+edges +"\n\n"+getLastPart();
	}
	
	private static String getFirstPart(){
		return  "\\documentclass[12pt]{article} \n"+
				"\\usepackage{amssymb, amsmath} \n"+
				"\\usepackage{tikz} \n"+
				"\\usetikzlibrary{matrix,arrows} \n"+
	               "\n"+
	               "\\begin{document} \n";
	}
	private static String getLastPart(){
		return "\\begin{tikzpicture} \n"+
		"[scale=.5,  \n"+
		"font=\\small, \n"+
		"reaction/.style={rectangle, minimum size=5mm, inner sep=2pt,  draw=red!50!black!50, solid, top color=red!20!black!10, \n"+
		"bottom color=red!50!black!20,font=\\small}, \n"+
		"species/.style={rectangle,draw=white!5,fill=white!9,thick,inner sep=0pt,minimum size=3mm, font=\\small}, \n"+
		"arcDS/.style={shorten <=2pt,shorten >=2pt, >=stealth',semithick, ->}, 		\n"+
		"arcDD/.style={shorten <=2pt,shorten >=2pt, >=stealth',semithick, ->, dashed},    \n"+
		"arcUS/.style={shorten <=2pt,shorten >=2pt, >=stealth',semithick}, 			 \n"+
		"arcUD/.style={shorten <=2pt,shorten >=2pt, >=stealth',semithick, dashed} 	 \n"+
		"] \n"+
		"\n"+  
		"\\newcounter{j} \n"+
		"\\setcounter{j}{1} \n"+
		"\\foreach \\name/\\coords/\\type in \\nodes \n"+
		"{ \n"+
		"    \\node[\\type] (\\arabic{j})  at \\coords {\\name}; \n"+
		"    \\addtocounter{j}{1} \n"+
		"} \n"+
		 "\n"+
		"\\foreach \\firstNode/\\secondNode/\\type/\\linetype/\\labl/\\firstControl/\\secondControl in \\edges \n"+
		"{ \n"+
		"\\ifnum\\linetype=1 \n"+
		"  \\draw[\\type] (\\firstNode) -- (\\secondNode) \n"+
		"\\else \n"+
		"  \\draw[\\type] (\\firstNode) ..controls \\firstControl and \\secondControl .. (\\secondNode) \n"+
		"\\fi \n"+
		"node[auto, pos=0.5] {\\scriptsize\\labl};\n"+
		"}\n"+
         "\n"+
		"\\end{tikzpicture} \n"+
       "\n"+
		"\\end{document} \n";
	}
}
