package dsr;


import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JComboBox;

import org.apache.commons.collections15.Factory;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class Util {

	public static int stepX,stepY, xlines, ylines;
	static boolean grid;
	public static Graph<Vertex,Edge> g;
	public enum LineType {lineType, quadType, cubicType};

	public static VisualizationViewer<Vertex,Edge> vv;
	public static ControlPoint ctrl1, ctrl2, ctrl3;
    //public static char modalMouse='p'; 
    public static JComboBox modeBox;
	
	public static Graph<Vertex,Edge> readGraphFromContent(String content){
		Graph<Vertex,Edge> graph=new SparseMultigraph<Vertex,Edge>();
		Factory<Integer> idFactory = 
				new Factory<Integer>() {
			int count;

			public Integer create() {
				return count++;
			}};

			Map<String,Species> sps=new HashMap<String, Species>();

			short in=0;
			short type;//the type of interaction
			StringTokenizer st=new StringTokenizer(content, ".");
			//one interaction
			while (st.hasMoreTokens()) {
				String reaction=st.nextToken();
				Interaction ini=new Interaction(in++);
				if (reaction.contains("<-->"))
					type=0;//double direction
				else 	
					if(reaction.contains("<--"))
						type=1;
					else type=2;
				
				String parts[] = reaction.split("[<]*--[>]*");
				
				for(int i=0;i<2;i++)//left or right part of reactions
				{String species[]=parts[i].split("\\+");
				for(int k=0;k<species.length;k++)
				{String csp=species[k].trim();
					if (!(sps.containsKey(csp)))
					{
				    sps.put(csp, new Species(csp));
					}
				if (i==0)
				{ if ((type==0))//left part has - sign, and the right part + sign
					graph.addEdge(new Edge((short)-1, (int) idFactory.create()), sps.get(csp), ini ,EdgeType.UNDIRECTED );
				if(type==1)
					graph.addEdge(new Edge((short)-1, (int) idFactory.create()),	ini, sps.get(csp), EdgeType.DIRECTED );
				if(type==2)
					graph.addEdge(new Edge((short)-1, (int) idFactory.create()), sps.get(csp), ini, EdgeType.UNDIRECTED );
				}
				else
				{ if (type==0)
					graph.addEdge(new Edge((short)1, (int) idFactory.create()), sps.get(csp), ini ,EdgeType.UNDIRECTED );
				if(type==1)
					graph.addEdge(new Edge((short)1, (int) idFactory.create()), sps.get(csp),ini, EdgeType.UNDIRECTED );
				if(type==2) 
					graph.addEdge(new Edge((short)1, (int) idFactory.create()),	ini, sps.get(csp), EdgeType.DIRECTED );

				}
				}
				} }
			
				return graph;
			}


			}
