package org.javadynamicanalyzer.graph;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Iterator;

import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

@SuppressWarnings("serial")
public class Graph<T> extends DirectedSparseMultigraph<T, Edge<T>> implements Iterable<T>{
	//Make a classes to draw to JFrames
	class Layout extends FRLayout2<T, Edge<T>>{
		public Layout(Graph<T> g){ super(g); } 
	}
	class CA extends ComponentAdapter{
		Graph<T> graph;
		public CA(Graph<T> g){ graph=g; }
		public void componentResized(ComponentEvent e){ 
			graph.visual.setGraphLayout(new Layout(graph));
		}
	}
	public class Visualizer extends BasicVisualizationServer<T,Edge<T>>{
		Visualizer(Graph<T> g){
			super(new Layout(g));
			getRenderContext().setVertexLabelTransformer(new ToStringLabeller<T>());
			getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
			addComponentListener(new CA(g));
		}
	}
	
	public Visualizer visual=new Visualizer(this);
	public boolean addEdge(T n1, T n2){ return addEdge(new Edge<T>(n1,n2)); }
	public boolean addEdge(Edge<T> e){ return addEdge(e,e.src,e.dst,EdgeType.DIRECTED); }
	
	@Override
	public Iterator<T> iterator(){ return getVertices().iterator(); }
	public Iterator<T> nodeIterator(){ return iterator(); }
	public Iterator<Edge<T>> edgeIterator(){ return getEdges().iterator(); }
 
}
