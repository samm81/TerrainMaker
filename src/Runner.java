import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import com.sun.opengl.util.Animator;

/**
 * 
 * @author Sam Maynard
 * This class is the main runner.  It instantiates the objects
 * and gets the GLCanvas up and running with animation.
 */
public class Runner {

	/**
	 * default constructor.
	 */
	public Runner() {
		Frame f = new Frame();
		f.setBounds(100, 100, 700, 700);
		f.setBackground(Color.WHITE);
		
		/** 3D code **/
		GLCapabilities capabilities = new GLCapabilities();
		GLCanvas canvas = new RectangleTerrainer(capabilities);
		f.add(canvas);
		Animator animator = new Animator(canvas);
		
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		
		f.setVisible(true);
		canvas.requestFocus();
		animator.start();
	}
	
	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Runner mr = new Runner();
	}
}
