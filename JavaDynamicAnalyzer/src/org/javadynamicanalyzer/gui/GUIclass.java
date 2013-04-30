package org.javadynamicanalyzer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.javadynamicanalyzer.BasicBlockPath;
import org.javadynamicanalyzer.MethodNode.BasicBlock;
import org.javadynamicanalyzer.graph.Edge;
import org.javadynamicanalyzer.graph.Graph;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;


@SuppressWarnings("serial")
public class GUIclass<T> extends JApplet implements Iterable<T> {
	DirectedSparseMultigraph<T, Edge<T>> graph=new DirectedSparseMultigraph<T, Edge<T>>();
	String name=null;
	public boolean addEdge(T n1, T n2){return addEdge(new Edge<T>(n1,n2)); 
	}
	public boolean addEdge(Edge<T> e){ return graph.addEdge(e,e.src,e.dst,EdgeType.DIRECTED); }
	public boolean addNode(T t){ return graph.addVertex(t); }
	public void addGraph(Graph<T> g){
		for(Edge<T> e : g.getEdges())
			addEdge(e);
	}

	public void setName(String name){ this.name=name; }
	public String getName() { return name; }
	@Override
	public Iterator<T> iterator(){ return nodeIterator(); }
	public Iterator<T> nodeIterator(){ return graph.getVertices().iterator(); }
	public Iterator<Edge<T>> edgeIterator(){ return graph.getEdges().iterator(); }



	/**
	 * the visual component and renderer for the graph
	 */
	VisualizationViewer<T, Edge<T>> vv;

	VisualizationServer.Paintable rings;

	String root;

	KKLayout<T,Edge<T>> layout;
	@SuppressWarnings("unchecked")
	//	
	//	Mouse listeners:
	//
	//Picking Mode:
	//
	//MouseButtonOne press on a Vertex or Edge to select it
	//MouseButtonOne+Shift press on a Vertex or Edge to add or toggle selection
	//MouseButtonOne+drag on a Vertex to move all selected vertices
	//MouseButtonOne+drag to select Vertices in a rectangle
	//MouseButtonOne+Shift+drag to add to selection with Vertices in a rectangle
	//TransformingMode:
	//
	//MouseButtonOne+drag to translate the display
	//MouseButtonOne+Shift+drag to rotate the display
	//MouseButtonOne+ctrl(or Command)+drag to shear the display
	//Both Modes:
	//
	//MouseWheel to scale. When scale < 1, view is scaled. When scale > 1, layout is scaled.

	static final String instructions = 
		"<html>"+
		"<b><h2><center>Mouse listeners:</center></h2></b>"+
		"<p>Picking Mode::"+
		"<ul>"+
		"<li>MouseButtonOne press on a Vertex or Edge to select it"+
		"<li>MouseButtonOne+Shift press on a Vertex or Edge to add or toggle selection"+
		"<li>MouseButtonOne+drag on a Vertex to move all selected vertices"+
		"<li>MouseButtonOne+drag to select Vertices in a rectangle"+
		"<li>MouseButtonOne+Shift+drag to add to selection with Vertices in a rectangle"+
		"</ul>"+
		"<p>TransformingMode:"+
		"<ul>"+
		"<li>MouseButtonOne+drag to translate the display"+
		"<li>MouseButtonOne+Shift+drag to rotate the display"+
		"<li>MouseButtonOne+ctrl(or Command)+drag to shear the display"+
		"</ul>"+
		"<p>Both Modes"+
		"<ul>"+
		"<li>MouseWheel to scale. When scale < 1, view is scaled. When scale > 1, layout is scaled."+
		"</ul>";

	JDialog helpDialog;


	@SuppressWarnings("unchecked")


	public void getVisual(String name){

		layout = new KKLayout<T, Edge<T>>(graph);


		vv =  new VisualizationViewer<T, Edge<T>>(layout, new Dimension(600,600));
		vv.setBackground(Color.white);
		//		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		//		add a listener for ToolTips
		vv.setVertexToolTipTransformer(new ToStringLabeller());
		//		vv.getRenderContext().setArrowFillPaintTransformer(new EdgeShape.QuadCurve<T, Edge<T>>());

		Container content =  getContentPane();
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		content.add(panel);

		//		final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse(){

			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseClicked(arg0);

			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseEntered(arg0);
			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseExited(arg0);
			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mousePressed(arg0);

			}

			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseReleased(arg0);
			}

			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseDragged(arg0);
			}

			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseMoved(arg0);
			}

			public void mouseWheelMoved(MouseWheelEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseWheelMoved(arg0);
			}

		};

		vv.setGraphMouse(graphMouse);


		JComboBox modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

		final ScalingControl scaler = new CrossoverScalingControl();

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1/1.1f, vv.getCenter());
			}
		});


		helpDialog = new JDialog();
		helpDialog.getContentPane().add(new JLabel(instructions));

		JButton help = new JButton("Help");
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				helpDialog.pack();
				helpDialog.setVisible(true);
			}
		});

		JPanel controls = new JPanel();
		JPanel helpControls = new JPanel();
		helpControls.setBorder(BorderFactory.createTitledBorder("Help"));
		helpControls.add(help);
		controls.add(helpControls);
		content.add(panel);
		content.add(controls, BorderLayout.NORTH);


		//        addComponentListener(new CA(this));

		JPanel scaleGrid = new JPanel(new GridLayout(1,0));
		scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

		scaleGrid.add(plus);
		scaleGrid.add(minus);
		controls.add(scaleGrid);
		controls.add(modeBox);
		content.add(controls, BorderLayout.SOUTH);

		adjustLayout();
		final PickedState<BasicBlock> pickedState = (PickedState<BasicBlock>) vv.getPickedVertexState();

		// Attach the listener that will print when the vertices selection changes.
		pickedState.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {

				Object subject = e.getItem();

				if (subject instanceof BasicBlock) {
					BasicBlock vertex = (BasicBlock) subject;
					if (pickedState.isPicked(vertex)) {
						for (BasicBlockPath bbp: vertex.getMethodNode().getPaths()){
							for (final Integer bb : bbp){
								Transformer<Integer, Paint> vertexColor = new Transformer<Integer, Paint>() {
									public Paint transform(Integer i) {
										if(bb.equals(i)) {
											System.out.println("I am here!");
											return Color.GREEN;
										}
										return Color.RED;
									}
								};
								Transformer<Integer, Shape> vertexSize = new Transformer<Integer, Shape>(){
									public Shape transform(Integer i){
										Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
										// in this case, the vertex is twice as large
										if(bb.equals(i)) {
											System.out.println("I am here!");
											return AffineTransform.getScaleInstance(2, 2).createTransformedShape(circle);
										}
										else return circle;
									}
								};


								vv.getRenderContext().setVertexFillPaintTransformer((Transformer<T, Paint>) vertexColor);
								vv.getRenderContext().setVertexShapeTransformer((Transformer<T, Shape>) vertexSize);
							}
						}
						System.out.println("Vertex " + vertex
								+ " is now selected");
					} else {
						System.out.println("Vertex " + vertex
								+ " no longer selected");
					}
				}
			}
		});







		JFrame frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);

	}
	public void getVisual(){ getVisual(toString()); }

	public DijkstraDistance<T,Edge<T>> getDijkstraDistance(){ return new DijkstraDistance<T,Edge<T>>(graph); }

	public void adjustLayout(){
		double depth=0;
		Collection<T> visited=new HashSet<T>();
		for(T t: graph.getVertices()){
			if(graph.getInEdges(t).size()==0){
				visited.add(t);
				Point2D v=layout.transform(t);
				v.setLocation(300,10);
				depth=adjustLayoutNeib(graph.getSuccessors(t),60,visited);
				break;
			}
		}
		for(T t: graph.getVertices()){
			if(graph.getOutEdges(t).size()==0){
				Point2D v=layout.transform(t);
				v.setLocation(300,depth+50);
				break;
			}
		}
	}

	public double adjustLayoutNeib(Collection<T> curLevel, double depth, Collection<T> visited){
		if (curLevel.isEmpty()) return depth;
		double newdepth=0;
		double rv=0;
		double xax=0;
		for (T t: curLevel){
			if (visited.contains(t)) continue;
			visited.add(t);
			Point2D v=layout.transform(t);
			v.setLocation(300+xax, depth);
			rv=adjustLayoutNeib(graph.getSuccessors(t),depth+50, visited);
			newdepth=rv>newdepth? rv: newdepth;
			xax+=50;
		}
		return newdepth;
	}

	@SuppressWarnings("unchecked")
	public void Highlight(VisualizationViewer<T, Edge<T>> vv, final Integer bb){


	}



	public String toString(){
		if(name!=null)
			return name;
		else
			return super.toString();
	}

	class CA extends ComponentAdapter{
		GUIclass<T> gui;
		public CA(GUIclass<T> g){ gui=g; }
		public void componentResized(ComponentEvent e){
			gui.vv.setGraphLayout(new KKLayout<T, Edge<T>>(graph));
			Point2D lvc=gui.vv.getRenderContext().getMultiLayerTransformer().inverseTransform(gui.vv.getCenter());
			Dimension d=e.getComponent().getSize();
			final double dx = lvc.getX()-d.getWidth()/2;
			final double dy = lvc.getY()-d.getHeight()/2;
			gui.vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy+50);




		}
	}
}
