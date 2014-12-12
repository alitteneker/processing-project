package TestSketch.Math;

public interface MonoOperator {
    public float getPixelValue(float[][] input, int x, int y, int loca, int width, int height);
    public float getPixelValueLength(float[][] pixels, int i, int j, int i2, int width, int height);
}
