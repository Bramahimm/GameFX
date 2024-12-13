import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Musuh extends Karakter {
    private ImageView gambar;

    public Musuh(String imagePath, double x, double tinggi) {
        this.x = x;
        this.width = 90;
        this.height = tinggi;
        this.y = 600 - 50 - height;

        try {
            Image img = new Image(imagePath);
            gambar = new ImageView(img);
            gambar.setFitWidth(width);
            gambar.setFitHeight(height);
            gambar.setX(x);
            gambar.setY(y);
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar musuh");
        }
    }

    @Override
    public void gerak() {
        x -= 2;
        gambar.setX(x);
    }

    @Override
    public boolean cekTabrakan(Karakter obj) {
        return gambar.getBoundsInParent().intersects(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
    }

    public ImageView getGambar() {
        return gambar;
    }
}