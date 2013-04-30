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
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;


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

		//        vv.setBackground(Color.black);

		Container content =  getContentPane();
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		content.add(panel);


		final PickedState<BasicBlock> pickedState = (PickedState<BasicBlock>) vv.getPickedVertexState();
		//		final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse(){

			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
				//				
				//


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
		showActiveNode();
		adjustLabel();


		// Attach the listener that will print when the vertices selection changes.
		pickedState.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				showActiveNode();
				Object subject = e.getItem();
				if (subject instanceof BasicBlock) {
					System.out.println("asdfsadf");

					BasicBlock vertex = (BasicBlock) subject;
					if (pickedState.isPicked(vertex)) {
//						for (BasicBlock bb: vertex.get{
//
//							changeVertexSizeColor(bbp,Color.blue);
//							break;
//						}
						System.out.println("Vertex " + vertex
								+ " is now selected");
					} else {
						System.out.println("Vertex " + vertex
								+ " no longer selected");
					}
				}
				System.out.println("lalal");
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
				depth=adjustLayoutNeib(graph.getSuccessors(t),60, 300, visited);
				break;
			}
		}
		double xax=300;
		for(T t: graph.getVertices()){
			if(graph.getOutEdges(t).size()==0){
				Point2D v=layout.transform(t);
				v.setLocation(xax,depth+50);
				xax+=50;
			}
		}
	}

	public double adjustLayoutNeib(Collection<T> curLevel, double depth, double width, Collection<T> visited){
		if (curLevel.isEmpty()) return depth;
		double newdepth=0;
		double rv=0;
		for (T t: curLevel){
			if (visited.contains(t)) continue;
			visited.add(t);
			Point2D v=layout.transform(t);
			v.setLocation(width, depth);
			rv=adjustLayoutNeib(graph.getSuccessors(t),depth+50, width, visited);
			newdepth=rv>newdepth? rv: newdepth;
			width+=50;
		}
		return newdepth;
	}


	public void showActiveNode(){
		BasicBlockPath col=new BasicBlockPath();
		for (BasicBlockPath bbp: ((BasicBlock) graph.getVertices().iterator().next()).getMethodNode().getPaths())
			col.addAll(bbp);
		changeVertexSizeColor(col,Color.green);
	}


	public void adjustLabel(){
		//		for(T t: graph.getVertices()){
		//			if (t instanceof BasicBlock){
		//				final BasicBlock vertex=(BasicBlock) t;
		//				if(vertex.getMethodNode().getExternalLinks(vertex).isEmpty()){
		//					Transformer<BasicBlock, Paint> vertexColor = new Transformer<BasicBlock, Paint>() {
		//						public Paint transform(BasicBlock i) {
		//							if(vertex.equals(i)) {
		//								System.out.println("I am here!");
		//								return Color.GREEN;
		//							}
		//							return Color.RED;
		//						}
		//					};
		//					vv.getRenderContext().setVertexFillPaintTransformer((Transformer<T, Paint>) vertexColor);
		changeVertexLabel();
		//				}
		//			}
		//
		//		}
	}

	public void changeVertexSizeColor(final BasicBlockPath bbp, final Color actcol){
		Transformer<BasicBlock, Paint> vertexColor = new Transformer<BasicBlock, Paint>() {
			public Paint transform(BasicBlock bb) {
				if(bbp.contains(bb.index())) {
					System.out.println("CALLED");
					return actcol;
				}
				return Color.RED;
			}
		};
		//		Transformer<BasicBlock, Shape> vertexSize = new Transformer<BasicBlock, Shape>(){
		//			public Shape transform(BasicBlock i){
		//				Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
		//				// in this case, the vertex is twice as large
		//				if(vertex.equals(i)) {
		//					System.out.println("I am here!");
		//					return AffineTransform.getScaleInstance(2, 2).createTransformedShape(circle);
		//				}
		//				else return circle;
		//			}
		//		};


		vv.getRenderContext().setVertexFillPaintTransformer((Transformer<T, Paint>) vertexColor);
		//		vv.getRenderContext().setVertexShapeTransformer((Transformer<T, Shape>) vertexSize);
	}


	@SuppressWarnings("unchecked")
	public void changeVertexLabel(){
		// this class will provide both label drawing and vertex shapes
		VertexLabelAsShapeRenderer<T, Edge<T>> vlasr = new VertexLabelAsShapeRenderer<T, Edge<T>>(vv.getRenderContext());

		// customize the render context
		vv.getRenderContext().setVertexLabelTransformer((Transformer<T, String>) new Transformer<BasicBlock, String>(){
			@Override
			public String transform(BasicBlock bb) {
				// TODO Auto-generated method stub
				if(bb.getExternalLinks().isEmpty()) return "<html><center>"+bb.index()+"<p>";
				return bb.toString();
			}});
		vv.getRenderContext().setVertexShapeTransformer((Transformer<T, Shape>) new Transformer<BasicBlock, Shape>(){

			@Override
			public Shape transform(BasicBlock bb) {
				// TODO Auto-generated method stub
				Ellipse2D circle = new Ellipse2D.Double(-10, -10, 20, 20);
				if(bb.getExternalLinks().isEmpty()){

					return circle;
				}
				return AffineTransform.getScaleInstance(2, 2).createTransformedShape(circle);
			}
		});
		/*
        		(Transformer<T, String>) // this chains together Transformers so that the html tags
        		// are prepended to the toString method output
        		new ChainedTransformer<BasicBlock,String>(new Transformer[]{
        		new ToStringLabeller<String>(),
        		new Transformer<BasicBlock,String>() {
					public String transform(BasicBlock i) {
						if(vertex.equals(i)) {
						return "<html><center>"+i+"<p>";
						}
						else return i.toString();
					}}}));
		 */
		//				vv.getRenderContext().setVertexShapeTransformer(vlasr);
		//		vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.red));
		//		vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(Color.yellow));
		//		vv.getRenderContext().setEdgeStrokeTransformer(new ConstantTransformer(new BasicStroke(2.5f)));
		//
		//		// customize the renderer
		//		vv.getRenderer().setVertexRenderer(new GradientVertexRenderer<T,Edge<T>>(Color.gray, Color.white, true));
		vv.getRenderer().setVertexLabelRenderer(vlasr);
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
