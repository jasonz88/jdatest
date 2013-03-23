package com.omegastudios.inputbuffer;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;

import javax.swing.JFrame;

public class InputBuffer implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener  {
	//CONSTRUCTS
	public enum Type {KEY_TYPE, KEY_PRESS, KEY_RELEASE, MOUSE_DRAG, MOUSE_MOVE, MOUSE_CLICK, MOUSE_PRESS, MOUSE_RELEASE, MOUSE_ENTER, MOUSE_EXIT, MOUSE_WHEEL};
	public static class KeyInput{
		public Type type;
		public int keycode;
		
		KeyInput(int nkeycode, Type t){
			type=t;
			keycode=nkeycode;
		}
	}
	public static class MouseInput{
		public Type type;
		public Point pos;
		
		MouseInput(Point p, Type t){
			type=t;
			pos=p;
		}
	}
	
	//VARIABLES
	Point mousePos;
	DualArrayList<MouseInput> mouseInputList;
	DualArrayList<KeyInput> keyInputList;
	
	//METHODS
	public InputBuffer(JFrame jf){
		mousePos=new Point(0,0);
		mouseInputList=new DualArrayList<MouseInput>();
		keyInputList=new DualArrayList<KeyInput>();
		
		jf.getContentPane().addMouseMotionListener(this);
		jf.getContentPane().addMouseListener(this);
		jf.getContentPane().addMouseWheelListener(this);
		jf.addKeyListener(this);
	}
	
	public Iterator<KeyInput> getKeyInputIterator()		{ return keyInputList.iterator();	}
	public int getKeyInputSize()						{ return keyInputList.size();		}
	public KeyInput getKeyInput(int i)					{ return keyInputList.get(i);		}
	
	public Iterator<MouseInput> getMouseInputIterator()	{ return mouseInputList.iterator();	}
	public int getMouseInputSize()						{ return mouseInputList.size();		}
	public MouseInput getMouseInput(int i)				{ return mouseInputList.get(i);		}
	
	public Point getMousePos(){ return mousePos; }
	public void update(){
		mouseInputList.swap();
		keyInputList.swap();
	}

	//Mouse Stuff
	@Override
	public void mouseClicked(MouseEvent arg0) 	{ mouseInputList.add(new MouseInput((Point)mousePos.clone(),Type.MOUSE_CLICK)); 	}
	@Override
	public void mouseEntered(MouseEvent arg0) 	{ mouseInputList.add(new MouseInput((Point)mousePos.clone(),Type.MOUSE_ENTER)); 	}
	@Override
	public void mouseExited(MouseEvent arg0)	{ mouseInputList.add(new MouseInput((Point)mousePos.clone(),Type.MOUSE_EXIT)); 		}
	@Override
	public void mousePressed(MouseEvent arg0) 	{ mouseInputList.add(new MouseInput((Point)mousePos.clone(),Type.MOUSE_PRESS)); 	}
	@Override
	public void mouseReleased(MouseEvent arg0) 	{ mouseInputList.add(new MouseInput((Point)mousePos.clone(),Type.MOUSE_RELEASE));	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		mousePos=arg0.getPoint();
		mouseInputList.add(new MouseInput((Point)mousePos.clone(),Type.MOUSE_DRAG));
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		mousePos=arg0.getPoint();
		mouseInputList.add(new MouseInput((Point)mousePos.clone(),Type.MOUSE_MOVE));
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		Point p=new Point(arg0.getWheelRotation(),arg0.getScrollAmount());
		mouseInputList.add(new MouseInput(p,Type.MOUSE_WHEEL));
	}

	//Key Stuff
	@Override
	public void keyPressed(KeyEvent arg0) 	{ keyInputList.add(new KeyInput(arg0.getKeyCode(),Type.KEY_PRESS));	  }
	@Override
	public void keyReleased(KeyEvent arg0) 	{ keyInputList.add(new KeyInput(arg0.getKeyCode(),Type.KEY_RELEASE)); }
	@Override
	public void keyTyped(KeyEvent arg0)  	{ keyInputList.add(new KeyInput(arg0.getKeyCode(),Type.KEY_TYPE));	  }
}
