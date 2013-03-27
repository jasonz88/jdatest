package org.javadynamicanalyzer.graph;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

@SuppressWarnings("serial")
public class Graph<T> extends DirectedSparseMultigraph<T, Edge<T>> implements Iterable<T> {
	//Make a classes to draw to JFrames
	//class Layout extends FRLayout2<T, Edge<T>>{
	class Layout extends KKLayout<T, Edge<T>>{
		public Layout(Graph<T> g){ super(g); } 
	}
	class CA extends ComponentAdapter{
		Graph<T> graph;
		public CA(Graph<T> g){ graph=g; }
		public void componentResized(ComponentEvent e){
			graph.visual.setGraphLayout(new Layout(graph));
		    Point2D lvc=graph.visual.getRenderContext().getMultiLayerTransformer().inverseTransform(graph.visual.getCenter());
		    Dimension d=e.getComponent().getSize();
			final double dx = lvc.getX()-d.getWidth()/2;
			final double dy = lvc.getY()-d.getHeight()/2;
			graph.visual.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy+50);
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
	String name=null;
	
	public boolean addEdge(T n1, T n2){ return addEdge(new Edge<T>(n1,n2)); }
	public boolean addEdge(Edge<T> e){ return addEdge(e,e.src,e.dst,EdgeType.DIRECTED); }
	public boolean addNode(T t){ return super.addVertex(t); }
	public void addGraph(Graph<T> g){
		for(Edge<T> e : g.getEdges())
			addEdge(e);
	}
	
	public void setName(String name){ this.name=name; }
	public void getVisual(String name){
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(visual); 
        frame.pack();
        frame.setVisible(true); 
	}
	public void getVisual(){ getVisual(toString()); }
	
	public Collection<T> getNodes(){ return super.getVertices(); }
	
	@Override
	public Iterator<T> iterator(){ return nodeIterator(); }
	public Iterator<T> nodeIterator(){ return getVertices().iterator(); }
	public Iterator<Edge<T>> edgeIterator(){ return getEdges().iterator(); }
 
	public DijkstraDistance<T,Edge<T>> getDijkstraDistance(){ return new DijkstraDistance<T,Edge<T>>(this); }
	
	public String toString(){
		if(name!=null)
			return name;
		else
			return super.toString();
	}
}
