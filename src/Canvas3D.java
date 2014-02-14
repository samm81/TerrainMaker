import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

/**
 * 
 * @author Sam Maynard, Kejin Wang
 *
 * abstract class for simplifying usage of JOGL 3D canvases
 */
@SuppressWarnings("serial")
public abstract class Canvas3D extends GLCanvas {

	// used for camera control
	double angle = 0;
	double radius = 7;

	// camera position
	double cameraX = Math.cos(angle) * radius;
	double cameraY = 0;
	double cameraZ = Math.sin(angle) * radius;
	
	double positionX = 0;
	double positionY = 0;
	double positionZ = 0;


	// constructor
	public Canvas3D(GLCapabilities capabilities) {
		super(capabilities);
		addGLEventListener(new GLEventListener() {

			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y,
					int width, int height) {
				doReshape(drawable.getGL(), drawable.getHeight(),
						drawable.getWidth());
			}

			@Override
			public void init(GLAutoDrawable drawable) {
				GL gl = drawable.getGL();
				gl.glClearColor(0f, 0f, 0f, 0f);
				gl.glEnable(GL.GL_DEPTH_TEST);
				gl.glDepthFunc(GL.GL_LEQUAL);

				initialize();
			}

			@Override
			public void displayChanged(GLAutoDrawable drawable,
					boolean modeChanged, boolean deviceChanged) {
			}

			@Override
			public void display(GLAutoDrawable drawable) {
				doDisplay(drawable.getGL(), drawable.getHeight(),
						drawable.getWidth());
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			int mousePreviousX = 0;
			int mousePreviousY = 0;

			@Override
			public void mouseDragged(MouseEvent e) {
				int mouseX = e.getX();
				int mouseY = e.getY();

				int mouseChangeX = mouseX - mousePreviousX;
				int mouseChangeY = mouseY - mousePreviousY;

				double reductionConstant = 50.0;

				double dragThreshold = 10;

				if (!(mouseChangeX > dragThreshold || mouseChangeY > dragThreshold) &&
						!(mouseChangeX < -dragThreshold || mouseChangeX < -dragThreshold)) {
					angle += mouseChangeX / reductionConstant;
					cameraX = Math.cos(angle) * radius;
					cameraY += mouseChangeY / reductionConstant;
					cameraZ = Math.sin(angle) * radius;
				}
				mousePreviousX = mouseX;
				mousePreviousY = mouseY;
			}
		});
		addMouseWheelListener(new MouseAdapter() {

			double reductionConstant = 1d;

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int scroll = -1 * e.getWheelRotation();
				double move = (double)scroll / reductionConstant;
				if(radius - move > 0){
					double ratio = (radius - move) / radius;
					cameraX *= ratio;
					cameraY *= ratio;
					cameraZ *= ratio;
					radius *= ratio;
				}
			}
		});
		addKeyListener(new KeyAdapter() {
			
			double scale = 1;

			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				switch(key){
				case KeyEvent.VK_W:
				case KeyEvent.VK_UP:
					forward();
					break;
				case KeyEvent.VK_A:
				case KeyEvent.VK_LEFT:
					left();
					break;
				case KeyEvent.VK_D:
				case KeyEvent.VK_RIGHT:
					right();
					break;
				case KeyEvent.VK_S:
				case KeyEvent.VK_DOWN:
					backward();
					break;
				default:
					key(e);
					break;
				}
			}

			private void forward() {
				move(-1 * Math.cos(angle) * scale, -1 * Math.sin(angle) * scale);
			}

			private void left() {
				move(-1 * Math.cos(angle - Math.PI / 2) * scale, -1 * Math.sin(angle - Math.PI / 2) * scale);
			}

			private void right() {
				move(-1 * Math.cos(angle + Math.PI / 2) * scale, -1 * Math.sin(angle + Math.PI / 2) * scale);
			}

			private void backward() {
				move(Math.cos(angle) * scale, Math.sin(angle) * scale);
			}
			
			private void move(double xComponent, double zComponent){
				positionX += xComponent;
				positionZ += zComponent;
			}
		});
	}

	// allows the subclass to implement keystrokes
	protected void key(KeyEvent e){}

	// displays to the screen
	protected void doDisplay(GL gl, int height, int width) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		adjustCamera();
		
		GLU glu = new GLU();
		glu.gluLookAt(positionX + cameraX, positionY + cameraY, positionZ + cameraZ, positionX, positionY, positionZ, 0, 1, 0);

		draw(gl, glu);
	}

	// allows the subclass to make camera adjustments
	protected void adjustCamera() {}

	protected void doReshape(GL gl, int height, int width) {
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU glu = new GLU();
		glu.gluPerspective(60, 1, .1, 2000);
		gl.glViewport(0, 0, width, height);
	}

	// allows the subclass to initialize variables
	protected abstract void initialize();

	// actually does the drawing
	protected abstract void draw(GL gl, GLU glu);

}
