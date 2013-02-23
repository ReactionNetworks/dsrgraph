package dsr;


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
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

			//the first matrix
			List<Map<String, List<Integer>>> m1=new ArrayList<Map<String,List<Integer>>>();
			//the second matrix

			//the Vertex for the species
			Map<String,Species> sps=new  HashMap<String,Species>();




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
				String[][] species=new String[2][]; 
				for(int i=0;i<2;i++)//left or right part of reactions
				{
					species[i]=parts[i].split("\\+");

				}
				List<Map<String,Short>> result= computeMatrix(species, type);
				Map<String,Short> matrix1=result.get(0);
				Map<String,Short> matrix2=result.get(1);
				Set<String> allSpecies=new HashSet<String>();
				allSpecies.addAll(matrix2.keySet());
				for(String s:allSpecies){
					if (!(sps.containsKey(s) )) 
					{
						sps.put(s, new Species(s));
					}
					//from reaction to species
				if(matrix1.get(s)!=0)
					{if((matrix1.get(s)*matrix2.get(s)>0) & (matrix2.get(s)!=2)) //have the same sign, but the second matrix is not 2	
						graph.addEdge(new Edge((short)matrix1.get(s), (int) idFactory.create()), ini, sps.get(s), EdgeType.UNDIRECTED );
					
					 else  //different signs and matrix!=0
						if(matrix2.get(s)==0) //there are both signs
							if (matrix1.get(s)>0) // ramane o michie negativa
							{graph.addEdge(new Edge((short)matrix1.get(s), (int) idFactory.create(),true), ini, sps.get(s), EdgeType.UNDIRECTED );
							graph.addEdge(new Edge((short)(-100), (int) idFactory.create(), true),  sps.get(s),ini, EdgeType.DIRECTED );

							}
							else 
							{graph.addEdge(new Edge((short)matrix1.get(s), (int) idFactory.create(),true), ini, sps.get(s), EdgeType.UNDIRECTED );
							graph.addEdge(new Edge((short)(100), (int) idFactory.create(), true),  sps.get(s),ini, EdgeType.DIRECTED );

							}
						else //matrix2<>0 matrix1<>0
							if (matrix2.get(s)==2)
							//matrix2 is 2 - meaning there is no edge from S to R, but there is one from R to S
								graph.addEdge(new Edge((short)(matrix1.get(s)), (int) idFactory.create()),  ini, sps.get(s), EdgeType.DIRECTED );
							else //m1<>0 m2<>0 m2<>nothing m1.sign<>m2.sign the value of ra is the value of ra????
							{   
								graph.addEdge(new Edge((short)(matrix1.get(s)), (int) idFactory.create(),true),  ini, sps.get(s), EdgeType.DIRECTED );
								graph.addEdge(new Edge((short)(matrix1.get(s)), (int) idFactory.create(),true),  sps.get(s), ini, EdgeType.DIRECTED );
								
							}
					}
				else
						//in the first matrix there is no value for R-to-S, so the value of S-to-R will be inf
						if (matrix2.get(s)==0) //both signs
							{graph.addEdge(new Edge((short)100, (int) idFactory.create(),true),  sps.get(s),ini, EdgeType.DIRECTED );
							graph.addEdge(new Edge((short)(-100), (int) idFactory.create(), true),  sps.get(s),ini, EdgeType.DIRECTED );
							
							}
						else
							if(matrix2.get(s)!=0)
				             graph.addEdge(new Edge((short)(matrix2.get(s)*100), (int) idFactory.create()), sps.get(s),ini, EdgeType.DIRECTED );

						//else //deal separately with infinit values
							{}//graph.addEdge(new Edge((short)(matrix2.get(s)*100), (int) idFactory.create()), sps.get(s),ini, EdgeType.DIRECTED );

				}
				
				//}
				//}
			}
System.out.println(graph);
			return graph;
	}


	public static List<Map<String,Short>> computeMatrix(String[][] species, int type){
		Map<String,Short> matrix1=new HashMap<String, Short>(); //from reaction to species
		Map<String,Short> matrix2=new HashMap<String, Short>(); //from reaction to species
		List<Map<String,Short>> spCard=new ArrayList<Map<String, Short>>();
	//	System.out.println(species[0][0]);
		for (int i=0;i<2;i++)//left/right part of reaction	
		{Map<String,Short> part=new HashMap<String,Short>();
		for(int k=0;k<species[i].length;k++) //
		{String csp=species[i][k].trim();//check if there is only a species name or also a parameter
		short coef=1;
		if (csp.matches("[0-9]+[a-zA-Z]+"))
		{String pp[]=csp.split("[a-zA-Z]");
		csp=csp.substring(pp[0].length());
		coef=Short.parseShort(pp[0]);
		}
		if (!(part.containsKey(csp)))
		{
			part.put(csp, coef);
		}
		else
			part.put(csp, (short)(1+part.get(csp)));
		}
		spCard.add(part);
		}

		Set<String> allSpecies=new HashSet<String>();
		allSpecies.addAll(spCard.get(0).keySet());
		allSpecies.addAll(spCard.get(1).keySet());
		for(String s:allSpecies)
		{if(spCard.get(0).containsKey(s))
			if(spCard.get(1).containsKey(s))
				matrix1.put(s,(short)(spCard.get(1).get(s)-spCard.get(0).get(s)));
			else
				matrix1.put(s,(short)(-spCard.get(0).get(s)));
		else
			matrix1.put(s,spCard.get(1).get(s));

		}
		byte v=0;
		//binary coding: 00 - nothing, 01 - minus, 10 - plus, 11 - both
		for(String s:allSpecies)
		{v=0;
		if (((type==0)||(type==2)) & (spCard.get(0).containsKey(s))) //from left to right
			v+=1;//-
		if (((type==0)||(type==1)) &(spCard.get(1).containsKey(s)))
			v+=2;//+
		System.out.println(v);
		switch (v){
		case 0: matrix2.put(s,(short) 2);break;//nothing
		case 1: matrix2.put(s,(short) -1);break; //nothing
		case 2: matrix2.put(s, (short)1);break; //nothing
		case 3: matrix2.put(s, (short)0);break; //nothing
				}
		}		
		System.out.println(matrix1);
		System.out.println(matrix2);
		List<Map<String,Short>> result=new ArrayList<Map<String,Short>>();
		result.add(matrix1);
		result.add(matrix2);
		return result;

	}
}
