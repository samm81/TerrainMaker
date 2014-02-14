import java.awt.Color;
import java.util.Random;

/**
 * 
 * @author Sam Maynard, Kejin Wang
 *
 * Class that generates terrains
 */
public class TerrainMaker {

	double[] endPoints = { .5, .5, .5, .5 };

	Random r = new Random();

	// returns a random value, scaled by roughness
	private double roughValue(double roughness) {
		return (r.nextDouble() * 2 - 1) * roughness;
	}

	// generates a basic test terrain
	public double[][] generateBasicTerrain(double[][] terrain, double roughness){
		int length = terrain.length;
		int width = terrain[0].length;
		double height = endPoints[0];
		for(int i=0;i<length;i++){
			for(int j=0;j<width;j++){
				height += (r.nextDouble() - .5)/(100/roughness);
				terrain[i][j] = height;
			}
		}
		return terrain;
	}

	// generates terrain based on the diamond square algorithm
	public double[][] generateDiamondSquareTerrain(double[][] terrain, double roughness){
		int length = terrain.length - 1;
		int width = terrain[0].length - 1;

		terrain[0][0] = endPoints[0];
		terrain[length][0] = endPoints[1];
		terrain[0][width] = endPoints[2];
		terrain[width][length] = endPoints[3];

		return diamondSquare(terrain, roughness);
	}

	// used by the generateDiamondSquareTerrain method
	// recursively generates
	private double[][] diamondSquare(double[][]terrain, double roughness){
		int size = terrain.length;
		int end = size - 1;
		int center = (size - 1) / 2;

		double average = terrain[0][0] + terrain[0][end] + terrain[end][0] + terrain[end][end];
		average /= 4d;
		average += roughValue(roughness);
		if(terrain[center][center] == 0)
			terrain[center][center] = average;

		average = (terrain[0][0] + terrain[0][end]) / 2d + roughValue(roughness);
		if(terrain[0][center] == 0)
			terrain[0][center] = average;

		average = (terrain[0][0] + terrain[end][0]) / 2d + roughValue(roughness);
		if(terrain[center][0] == 0)
			terrain[center][0] = average;

		average = (terrain[0][end] + terrain[end][end]) / 2d + roughValue(roughness);
		if(terrain[center][end] == 0)
			terrain[center][end] = average;

		average = (terrain[end][0] + terrain[end][end]) / 2d + roughValue(roughness);
		if(terrain[end][center] == 0)
			terrain[end][center] = average;

		if(size <=3)
			return terrain;

		for(int a=0;a<=1;a++){
			for(int b=0;b<=1;b++){

				double[][] miniTerrain = new double[center + 1][center + 1];
				for(int i=0;i<miniTerrain.length;i++){
					for(int j=0;j<miniTerrain[0].length;j++){
						miniTerrain[i][j] = terrain[(a*center) + i][(b*center) + j];
					}
				}
				miniTerrain = diamondSquare(miniTerrain, roughness/2);
				for(int i=0;i<miniTerrain.length;i++){
					for(int j=0;j<miniTerrain[0].length;j++){
						terrain[(a*center) + i][(b*center) + j] = miniTerrain[i][j];
					}
				}
			}
		}

		return terrain;
	}

	// generates basic testing colors
	public float[][][] generateTestColors(double terrain[][], float[][][] colors) {
		int length = terrain.length;
		int width = terrain[0].length;
		float[] color1 = Color.RED.getColorComponents(null);
		float[] color2 = Color.GREEN.getColorComponents(null);
		float[] color3 = Color.BLUE.getColorComponents(null);
		float[] color4 = Color.ORANGE.getColorComponents(null);
		for(int i=0;i<length;i++){
			for(int j=0;j<width;j++){
				if(i % 2 == 0 && j % 2 == 0)
					colors[i][j] = color1;
				if(i % 2 == 0 && j % 2 == 1)
					colors[i][j] = color2;
				if(i % 2 == 1 && j % 2 == 0)
					colors[i][j] = color3;
				if(i % 2 == 1 && j % 2 == 1)
					colors[i][j] = color4;
			}
		}
		return colors;
	}

	// generates random colors
	public float[][][] generateRandomColors(double terrain[][], float[][][] colors) {
		int length = terrain.length;
		int width = terrain[0].length;
		for(int i=0;i<length;i++){
			for(int j=0;j<width;j++){
				colors[i][j] = new float[] { r.nextFloat(), r.nextFloat(), r.nextFloat() };
			}
		}
		return colors;
	}

	// generates a gradient of colors
	public float[][][] generateBasicColors(double terrain[][], float[][][] colors) {
		int length = terrain.length;
		int width = terrain[0].length;
		float[] color = new float[] { r.nextFloat(), r.nextFloat(), r.nextFloat() };
		for(int i=0;i<length;i++){
			for(int j=0;j<width;j++){
				for(int k=0;k<color.length;k++){
					float col = color[k];
					col += (r.nextFloat() - .5f)/50f;
					if(col > 1) col = 1;
					if(col < 0) col = 0;
					color[k] = col;
				}
				colors[i][j] = new float[] { color[0], color[1], color[2] };
			}
		}
		return colors;
	}

	// generates colors based on their height
	public float[][][] generateAltitudeColor(double terrain[][], float[][][] colors) {
		int length = terrain.length;
		int width = terrain[0].length;
		
		double maxHeight = Double.MIN_NORMAL;
		for(int i=0;i<terrain.length;i++){
			for(int j=0;j<terrain[0].length;j++){
				double height = terrain[i][j];
				if(height > maxHeight)
					maxHeight = height;
			}
		}
		
		float[] color = new float[3];
		for(int i=0;i<length;i++){
			for(int j=0;j<width;j++){
				double height = terrain[i][j] / maxHeight;
				if(height > .7)
					color = Color.WHITE.getColorComponents(null);
				else if(height > -.3)
					color = Color.GREEN.darker().getColorComponents(null);
				else
					color = Color.BLUE.getColorComponents(null);
				for(int k=0;k<color.length;k++){
					float col = color[k];
					col += (r.nextFloat() - .5f)/12f;
					if(col > 1) col = 1;
					if(col < 0) col = 0;
					color[k] = col;
				}
				colors[i][j] = new float[] { color[0], color[1], color[2] };
			}
		}
		return colors;
	}


}