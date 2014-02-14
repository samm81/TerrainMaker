import java.awt.event.KeyEvent;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;

/**
 * 
 * @author Sam Maynard, Kejin Wang
 * 
 * Class to display the generated terrain
 *
 */
@SuppressWarnings("serial")
public class RectangleTerrainer extends Canvas3D {

	int size = 513;
	double scale = 1;
	double roughness = 20;

	double maxHeight;
	
	double[][] terrain = new double[size][size];
	float[][][] colors = new float[size][size][3];

	DoubleBuffer verticies = BufferUtil.newDoubleBuffer(size * size * 4 * 3);
	FloatBuffer vertexColors = BufferUtil.newFloatBuffer(size * size * 4 * 3);

	boolean reInit = true;
	
	double humanHeight = 7;
	
	double edgeSize = 10;

	// constructer
	public RectangleTerrainer(GLCapabilities capabilities) {
		super(capabilities);
	}

	@Override
	protected void key(KeyEvent e) {
		reInit = true;
	}
	
	@Override
	protected void adjustCamera() {
		int x = (int) ((this.positionX + 64) * 4);
		int y = (int) ((this.positionZ + 64) * 4);
		
		double height = 0;
		try{
			height = terrain[x][y];
		}catch(ArrayIndexOutOfBoundsException e){
			
		}
		
		this.positionY = height + humanHeight;
	}

	// actually does the drawing
	protected void draw(GL gl, GLU glu) {
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glEnable(GL.GL_LIGHTING); //enables lighting
		gl.glShadeModel(GL.GL_SMOOTH); //set shading on objects drawn as smooth
		
		gl.glEnable(GL.GL_LIGHT0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] {0, 10, 0, 1f}, 0); //sets position of light close to the top of the sphere
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[] {0.4f,0.4f,0.3f}, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[] {0.1f,0.1f,0f}, 0);

		gl.glLightf(GL.GL_LIGHT0, GL.GL_QUADRATIC_ATTENUATION, .005f);
		gl.glLightf(GL.GL_LIGHT0, GL.GL_LINEAR_ATTENUATION, 0.1f); 
		gl.glLightf(GL.GL_LIGHT0, GL.GL_CONSTANT_ATTENUATION, 0.05f);
		
		gl.glPushMatrix();
		gl.glTranslated(0, maxHeight + 10, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, new float[] {0.7f,0.7f,0.7f}, 0);
		gl.glColor3f(1,1,0);
		GLUT glut = new GLUT();
		glut.glutSolidSphere(1, 100, 100);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, new float[] {0f,0f,0f}, 0);
		gl.glPopMatrix();
		
		
		if(getEdge() != 0){
			switch(getEdge()){
			case 1: generateEdge1(); break;
			case 2: generateEdge2(); break;
			case 3: generateEdge3(); break;
			case 4: generateEdge4(); break;
			}
		}

		if(reInit){
			resetTerrain();
			initialize();
		}

		gl.glScaled(.25, scale, .25);
		gl.glTranslated(
				-(terrain.length * .5d),
				0,
				-(terrain[0].length * .5d));

		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_COLOR_ARRAY);

		verticies.rewind();
		vertexColors.rewind();

		gl.glVertexPointer(3, GL.GL_DOUBLE, 0, verticies);
		gl.glColorPointer(3, GL.GL_FLOAT, 0, vertexColors);

		gl.glDrawArrays(GL.GL_QUADS, 0, size * size * 4);

		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		
		gl.glEnable(GL.GL_FOG);
		gl.glFogi(GL.GL_FOG_MODE, GL.GL_EXP2);
		gl.glFogfv(GL.GL_FOG_COLOR, new float[] { 0.7f, 0.7f, 0.7f, 1.0f }, 0);
		gl.glFogf(GL.GL_FOG_DENSITY, 0.02f);
		gl.glHint(GL.GL_FOG_HINT, GL.GL_NICEST);
	}

	// returns the edge that the person is close to, 0 if not near an edge
	private int getEdge() {
		double bound = (size-1)/8d;
		double x = this.positionX;
		double y = this.positionZ;
		if(y > -bound && y < -bound + edgeSize)
			return 1;
		if(x > -bound && x < -bound + edgeSize)
			return 2;
		if(y < bound && y > bound - edgeSize)
			return 3;
		if(x < bound && x > bound - edgeSize)
			return 4;
		
		return 0;
	}
	
	/*
	 * the next 4 methods shift the user depending on the edge their on, and generate
	 * new terrain
	 */
	
	private void generateEdge1() {
		/*
		for(int i=0;i<terrain.length;i++){
			for(int j=0;j<terrain[0].length;j++){
				if(j >= terrain[0].length - edgeSize){
					terrain[i][terrain[0].length - j - 1] = terrain[i][j];
				}
			}
		}
		for(int i=0;i<terrain.length;i++){
			for(int j=0;j<terrain[0].length;j++){
				if(j > edgeSize){
					terrain[i][j] = 0;
				}
			}
		}*/
		
		this.positionZ += (size-1)/4d - 17;
		
		reInit = true;
		//initialize();
	}
	
	private void generateEdge2() {
		this.positionX += (size-1)/4d - 17;
		
		reInit = true;
	}
	
	private void generateEdge3() {
		this.positionZ -= (size-1)/4d - 17;
		
		reInit = true;
	}
	
	private void generateEdge4() {
		this.positionX -= (size-1)/4d - 17;
		
		reInit = true;
	}
	
	@SuppressWarnings("unused")
	private void printTerrain() {
		for(int i=0;i<terrain.length;i++){
			for(int j=0;j<terrain[0].length;j++){
				System.out.print(((int)(terrain[i][j]*1000))/1000f + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	@Override
	protected void initialize() {
		TerrainMaker tm = new TerrainMaker();
		tm.generateDiamondSquareTerrain(terrain, roughness);
		tm.generateAltitudeColor(terrain, colors);

		for(int i=0;i<terrain.length-1;i++){
			for(int j=0;j<terrain[0].length-1;j++){
				int h = 0, l = 0;
				for(int k=0;k<4;k++){
					switch(k){
					case 0: h = 0; l = 0; break;
					case 1: h = 0; l = 1; break;
					case 2: h = 1; l = 1; break;
					case 3: h = 1; l = 0; break;
					}
					verticies.put(i + l);
					verticies.put(terrain[i + l][j + h]);
					verticies.put(j + h);

					vertexColors.put(colors[i + l][j + h][0]);
					vertexColors.put(colors[i + l][j + h][1]);
					vertexColors.put(colors[i + l][j + h][2]);
				}
			}
		}

		maxHeight = Double.MIN_NORMAL;
		for(int i=0;i<terrain.length;i++){
			for(int j=0;j<terrain[0].length;j++){
				double height = terrain[i][j];
				if(height > maxHeight)
					maxHeight = height;
			}
		}

		this.radius = 1.5;
		this.positionY = maxHeight + .05;
		
		reInit = false;
	}
	
	// resets the terrain to all 0s
	private void resetTerrain() {
		for(int i=0;i<terrain.length;i++){
			for(int j=0;j<terrain[0].length;j++){
				terrain[i][j] = 0;
			}
		}
	}

}
