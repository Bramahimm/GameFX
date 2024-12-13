abstract class Karakter {
    protected double x, y, width, height;

    public abstract void gerak();
    public abstract boolean cekTabrakan(Karakter obj);

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
