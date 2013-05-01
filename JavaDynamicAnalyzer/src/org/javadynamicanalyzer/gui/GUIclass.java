package org.javadynamicanalyzer.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.commons.collections15.Transformer;
import org.javadynamicanalyzer.BasicBlockPath;
import org.javadynamicanalyzer.MethodNode.BasicBlock;
import org.javadynamicanalyzer.graph.Edge;
import org.javadynamicanalyzer.graph.Graph;
import org.javadynamicanalyzer.gui.LensDemo.VerticalLabelUI;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.LensMagnificationGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;
import edu.uci.ics.jung.visualization.transform.HyperbolicTransformer;
import edu.uci.ics.jung.visualization.transform.LayoutLensSupport;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.MagnifyTransformer;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.MagnifyShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;


import org.javadynamicanalyzer.gui.TextAreaOutputStream;

import EDU.oswego.cs.dl.util.concurrent.misc.SwingWorker;

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

	FRLayout<T,Edge<T>> layout;

	/**
	 * provides a Hyperbolic lens for the view
	 */
	LensSupport hyperbolicViewSupport;
	/**
	 * provides a magnification lens for the view
	 */
	LensSupport magnifyViewSupport;

	/**
	 * provides a Hyperbolic lens for the model
	 */
	LensSupport hyperbolicLayoutSupport;
	/**
	 * provides a magnification lens for the model
	 */
	LensSupport magnifyLayoutSupport;

	BasicBlockPath AllActiveNodes;
	
	VertexStrokeHighlight<T> vsh;


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

	Set<BasicBlockPath> prev_bbp;

	@SuppressWarnings("unchecked")

	JTextPane textPane=new JTextPane();

	private void updateTextPane(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Document doc = textPane.getDocument();
				try {
					doc.insertString(doc.getLength(), text, null);
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				}
				textPane.setCaretPosition(doc.getLength() - 1);
			}
		});
	}

	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				updateTextPane(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextPane(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}





	public void getVisual(String name){

		JFrame frame = new JFrame(name);
		
		
		layout = new FRLayout<T, Edge<T>>(graph);
		//		adjustLayout();


		vv =  new VisualizationViewer<T, Edge<T>>(layout, new Dimension(600,600));
		vv.setBackground(Color.white);
		//		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		//		add a listener for ToolTips
		vv.setVertexToolTipTransformer(new ToStringLabeller());
		
		
		redirectSystemStreams();
		
		prev_bbp=new HashSet<BasicBlockPath>();
		final PickedState<T> picked_state = vv.getPickedVertexState();
		vsh = new VertexStrokeHighlight<T>(picked_state);
		frame.add( new JLabel(name), BorderLayout.NORTH );
		System.out.println(graph.toString());

		//		frame.add( new JLabel(" Outout" ), BorderLayout.NORTH );
		//
		//        JTextArea ta = new JTextArea(10,20);
		//        ta.append("Hello world!");
		//        frame.add( new JScrollPane( ta )  );
		//        TextAreaOutputStream taos = new TextAreaOutputStream(ta);
		//        PrintStream ps = new PrintStream( taos );
		//        System.setOut( ps );
		//        System.setErr( ps );
		//
		//
		//        frame.add( ta   );
		//        frame.pack();
		//        frame.setVisible( true );
		//        for( int i = 0 ; i < 100 ; i++ ) {
		//            System.out.println( i );
		//        }
		
		//		vv.getRenderContext().setArrowFillPaintTransformer(new EdgeShape.QuadCurve<T, Edge<T>>());

		//        vv.setBackground(Color.black);

		Container content =  getContentPane();
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		content.add(panel);


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


		ButtonGroup radio = new ButtonGroup();
		JRadioButton lineButton = new JRadioButton("Line");
		lineButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<T,Edge<T>>());
					vv.repaint();
				}
			}
		});

		JRadioButton quadButton = new JRadioButton("QuadCurve");
		quadButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<T,Edge<T>>());
					vv.repaint();
				}
			}
		});

		JRadioButton cubicButton = new JRadioButton("CubicCurve");
		cubicButton.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.CubicCurve<T,Edge<T>>());
					vv.repaint();
				}
			}
		});
		radio.add(lineButton);
		radio.add(quadButton);
		radio.add(cubicButton);

		JPanel edgePanel = new JPanel(new GridLayout(0,1));
		edgePanel.setBorder(BorderFactory.createTitledBorder("EdgeType"));
		edgePanel.add(lineButton);
		edgePanel.add(quadButton);
		edgePanel.add(cubicButton);

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

		JButton reset = new JButton("reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showActiveNode();
				adjustLabel();
				adjustLayout();
				vv.repaint();
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
		controls.add(edgePanel);

		//        addComponentListener(new CA(this));

		JPanel scaleGrid = new JPanel(new GridLayout(1,0));
		scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

		scaleGrid.add(plus);
		scaleGrid.add(minus);
		scaleGrid.add(reset);
		controls.add(scaleGrid);
		controls.add(modeBox);
		content.add(controls, BorderLayout.SOUTH);



		JSlider edgeOffsetSlider = new JSlider(0,50) {
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				d.width /= 2;
				return d;
			}
		};
		edgeOffsetSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider s = (JSlider)e.getSource();
				AbstractEdgeShapeTransformer<T, Edge<T>> aesf = 
						(AbstractEdgeShapeTransformer<T,Edge<T>>)vv.getRenderContext().getEdgeShapeTransformer();
				aesf.setControlOffsetIncrement(s.getValue());
				vv.repaint();
			}

		});
		JPanel labelPanel = new JPanel(new BorderLayout());
		JPanel sliderPanel = new JPanel(new GridLayout(3,1));
		JPanel sliderLabelPanel = new JPanel(new GridLayout(3,1));
		JPanel offsetPanel = new JPanel(new BorderLayout());
		offsetPanel.setBorder(BorderFactory.createTitledBorder("Offset"));

		sliderPanel.add(edgeOffsetSlider);

		sliderLabelPanel.add(new JLabel("Edges", JLabel.RIGHT));
		offsetPanel.add(sliderLabelPanel, BorderLayout.WEST);
		offsetPanel.add(sliderPanel);
		labelPanel.add(offsetPanel);
		controls.add(labelPanel);




		hyperbolicViewSupport = 
				new ViewLensSupport<T,Edge<T>>(vv, new HyperbolicShapeTransformer(vv, 
						vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)), 
						new ModalLensGraphMouse());
		hyperbolicLayoutSupport = 
				new LayoutLensSupport<T,Edge<T>>(vv, new HyperbolicTransformer(vv, 
						vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT)),
						new ModalLensGraphMouse());
		magnifyViewSupport = 
				new ViewLensSupport<T,Edge<T>>(vv, new MagnifyShapeTransformer(vv,
						vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)),
						new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));
		magnifyLayoutSupport = 
				new LayoutLensSupport<T,Edge<T>>(vv, new MagnifyTransformer(vv, 
						vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT)),
						new ModalLensGraphMouse(new LensMagnificationGraphMousePlugin(1.f, 6.f, .2f)));
		hyperbolicLayoutSupport.getLensTransformer().setLensShape(hyperbolicViewSupport.getLensTransformer().getLensShape());
		magnifyViewSupport.getLensTransformer().setLensShape(hyperbolicLayoutSupport.getLensTransformer().getLensShape());
		magnifyLayoutSupport.getLensTransformer().setLensShape(magnifyViewSupport.getLensTransformer().getLensShape());


		ButtonGroup radio1 = new ButtonGroup();
		JRadioButton normal = new JRadioButton("None");
		normal.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(hyperbolicViewSupport != null) {
						hyperbolicViewSupport.deactivate();
					}
					if(hyperbolicLayoutSupport != null) {
						hyperbolicLayoutSupport.deactivate();
					}
					if(magnifyViewSupport != null) {
						magnifyViewSupport.deactivate();
					}
					if(magnifyLayoutSupport != null) {
						magnifyLayoutSupport.deactivate();
					}
				}
			}
		});

		final JRadioButton hyperView = new JRadioButton("Hyperbolic View");
		hyperView.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				hyperbolicViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		final JRadioButton hyperModel = new JRadioButton("Hyperbolic Layout");
		hyperModel.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				hyperbolicLayoutSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		final JRadioButton magnifyView = new JRadioButton("Magnified View");
		magnifyView.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				magnifyViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		final JRadioButton magnifyModel = new JRadioButton("Magnified Layout");
		magnifyModel.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				magnifyLayoutSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		JLabel modeLabel = new JLabel("     Mode Menu >>");
		modeLabel.setUI(new VerticalLabelUI(false));
		radio1.add(normal);
		radio1.add(hyperModel);
		radio1.add(hyperView);
		radio1.add(magnifyModel);
		radio1.add(magnifyView);
		normal.setSelected(true);

		graphMouse.addItemListener(hyperbolicLayoutSupport.getGraphMouse().getModeListener());
		graphMouse.addItemListener(hyperbolicViewSupport.getGraphMouse().getModeListener());
		graphMouse.addItemListener(magnifyLayoutSupport.getGraphMouse().getModeListener());
		graphMouse.addItemListener(magnifyViewSupport.getGraphMouse().getModeListener());

		JPanel hyperControls = new JPanel(new GridLayout(3,2));
		hyperControls.setBorder(BorderFactory.createTitledBorder("Examiner Lens"));

		hyperControls.add(normal);
		hyperControls.add(new JLabel());

		hyperControls.add(hyperModel);
		hyperControls.add(magnifyModel);

		hyperControls.add(hyperView);
		hyperControls.add(magnifyView);

		controls.add(hyperControls);

		AllActiveNodes=new BasicBlockPath();
		for (BasicBlockPath bbp: ((BasicBlock) graph.getVertices().iterator().next()).getMethodNode().getPaths())
			AllActiveNodes.addAll(bbp);

		showActiveNode();
		adjustLabel();

		adjustLayout();



		JScrollPane paneScrollPane = new JScrollPane(textPane);
		paneScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		paneScrollPane.setPreferredSize(new Dimension(250, 155));
		paneScrollPane.setMinimumSize(new Dimension(10, 10));


		controls.add(paneScrollPane);

		// Attach the listener that will print when the vertices selection changes.
		picked_state.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				Object subject = e.getItem();
				if (subject instanceof BasicBlock) {
					System.out.println("asdfsadf");
					showActiveNode();
					BasicBlock vertex = (BasicBlock) subject;
					if (picked_state.isPicked((T) vertex)) {
						for (BasicBlockPath bbp: vertex.getPaths()){
							if(prev_bbp.contains(bbp) && vertex.getPaths().size()!=1) continue;
							changePathColor(bbp,Color.blue, AllActiveNodes, Color.green);
							prev_bbp.add(bbp);
							if(prev_bbp.size()==vertex.getPaths().size()) prev_bbp.clear();
							break;
						}
						//						JOptionPane.showMessageDialog(null, "put stats here", "InfoBox: ", JOptionPane.INFORMATION_MESSAGE);
						//						JOptionPane.showInputDialog(null, "asdfa", "info", JOptionPane.INFORMATION_MESSAGE );
						//						changeVertexColor(vertex, Color.CYAN, AllActiveNodes, Color.green);
						System.out.println("Vertex " + vertex
								+ " is now selected");
					} else {
						//						showActiveNode();
						System.out.println("Vertex " + vertex
								+ " no longer selected");
					}
				}
				System.out.println("lalal");
			}
		});




		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.pack();
		frame.setVisible(true);

	}
	public void getVisual(){ getVisual(toString()); }

	public DijkstraDistance<T,Edge<T>> getDijkstraDistance(){ return new DijkstraDistance<T,Edge<T>>(graph); }

	public Collection<T> getSortedSuccessors(T t){
		Collection<T> vertices = graph.getSuccessors(t);
		List<T> vertlist = new ArrayList<T>();
		for (T v : vertices)
			vertlist.add(v);
		Comparator comparator = new Comparator<BasicBlock>() {
			public int compare(BasicBlock c1, BasicBlock c2) {
				return c1.index() - c2.index(); // use your logic
			}
		};

		Collections.sort(vertlist, comparator);
		return vertlist;
	}

	@SuppressWarnings("unchecked")
	public void adjustLayout(){
		double[] rv=new double[2];
		Collection<T> visited=new HashSet<T>();

		for(T t: graph.getVertices()){
			if(graph.getInEdges(t).size()==0){
				visited.add(t);
				Point2D v=layout.transform(t);
				v.setLocation(300,10);
				rv=adjustLayoutNeib(getSortedSuccessors(t),60,300, visited);
				break;
			}
		}
		double xax=300;
		for(T t: graph.getVertices()){
			if(graph.getOutEdges(t).size()==0){
				Point2D v=layout.transform(t);
				v.setLocation(xax,rv[1]+50);
				xax+=50;
			}
		}
	}

	//	public double adjustLayoutNeib(Collection<T> curLevel, double depth, double width, Collection<T> visited){	
	//		double rv=0;
	//		if (curLevel.isEmpty())	return depth;
	//		double newdepth=depth;
	//		for (T t: curLevel){
	//			if (visited.contains(t)) continue;
	//			visited.add(t);
	//			Point2D v=layout.transform(t);
	//			v.setLocation(width,depth);
	//			rv=adjustLayoutNeib(getSortedSuccessors(t),depth+50, width, visited);
	//			newdepth=(rv>newdepth)? rv: newdepth;
	//			width+=50*graph.getSuccessorCount(t);	
	//		}
	//		return newdepth;
	//	}


	public double[] adjustLayoutNeib(Collection<T> curLevel, double depth, double width, Collection<T> visited){	
		double[] rv= new double[2];
		rv[0]=0;rv[1]=depth;
		if (curLevel.isEmpty())	return rv;
		double newdepth=depth;
		double succount=0;
		for (T t: curLevel){
			if (visited.contains(t)) continue;
			visited.add(t);
			Point2D v=layout.transform(t);
			v.setLocation(width,depth);
			rv=adjustLayoutNeib(getSortedSuccessors(t),depth+50, width, visited);
			newdepth=(rv[1]>newdepth)? rv[1]: newdepth;
			width+=50*(graph.getSuccessorCount(t)+rv[0]);
			succount+=graph.getSuccessorCount(t)-1;
		}
		rv[0]=curLevel.size()==1?0:succount;
		rv[1]=newdepth;
		return rv;
	}



	public void showActiveNode(){

		changePathColor(AllActiveNodes,Color.green);
	}


	private void changePathColor(final BasicBlockPath bbp, final Color actcol) {
		// TODO Auto-generated method stub
		Transformer<BasicBlock, Paint> vertexColor = new Transformer<BasicBlock, Paint>() {
			public Paint transform(BasicBlock bb) {
				if(bbp.contains(bb.index())) {
					return actcol;
				}
				return Color.RED;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer((Transformer<T, Paint>) vertexColor);
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


	public void changePathColor(final BasicBlockPath curpath, final Color pathcol, final BasicBlockPath bbp, final Color actcol ){

		Transformer<BasicBlock, Paint> vertexColor = new Transformer<BasicBlock, Paint>() {
			public Paint transform(BasicBlock bb) {
				if(curpath.contains(bb.index())) 	return pathcol;
				else if(bbp.contains(bb.index())) 	return actcol;
				else 		return Color.RED;
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


	public void changeVertexColor(final BasicBlock bb, final Color curnodecol, final BasicBlockPath bbp, final Color actcol){
		Transformer<BasicBlock, Paint> vertexColor = new Transformer<BasicBlock, Paint>() {
			public Paint transform(BasicBlock b) {
				if(bb.equals(b)) 	return curnodecol;
				else if(bbp.contains(bb.index()))	return actcol;
				else		return Color.RED;
			}
		};

		vv.getRenderContext().setVertexFillPaintTransformer((Transformer<T, Paint>) vertexColor);
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


	private class VertexStrokeHighlight<T> implements
    Transformer<T,Stroke>
    {
        protected boolean highlight = false;
        protected Stroke heavy = new BasicStroke(5);
        protected Stroke medium = new BasicStroke(3);
        protected Stroke light = new BasicStroke(1);
        protected PickedInfo<T> pi;
        
        public VertexStrokeHighlight(PickedInfo<T> pi)
        {
            this.pi = pi;
        }
        
        public void setHighlight(boolean highlight)
        {
            this.highlight = highlight;
        }
        
        public Stroke transform(T v)
        {
            if (highlight)
            {
                if (pi.isPicked(v))
                    return heavy;
                else
                {
                    return light;
                }
            }
            else
                return light; 
        }

    }



	public String toString(){
		if(name!=null)
			return name;
		else
			return super.toString();
	}


	//	class CA extends ComponentAdapter{
	//		GUIclass<T> gui;
	//		public CA(GUIclass<T> g){ gui=g; }
	//		public void componentResized(ComponentEvent e){
	//			gui.vv.setGraphLayout(new FRLayout<T, Edge<T>>(graph));
	//			Point2D lvc=gui.vv.getRenderContext().getMultiLayerTransformer().inverseTransform(gui.vv.getCenter());
	//			Dimension d=e.getComponent().getSize();
	//			final double dx = lvc.getX()-d.getWidth()/2;
	//			final double dy = lvc.getY()-d.getHeight()/2;
	//			gui.vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy+50);
	//
	//
	//
	//
	//		}
	//	}

}
