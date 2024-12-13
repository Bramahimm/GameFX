import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Awan {
    private ImageView gambar;
    private double x, y, kecepatan;

    public Awan(String imagePath, double x, double y, double kecepatan) {
        this.x = x;
        this.y = y;
        this.kecepatan = kecepatan;
        try {
            Image img = new Image(imagePath);
            gambar = new ImageView(img);
            gambar.setFitWidth(100);
            gambar.setFitHeight(60);
            gambar.setX(x);
            gambar.setY(y);
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar awan");
        }
    }

    public void gerak() {
        x -= kecepatan;
        if (x + gambar.getFitWidth() < 0) {
            x = 800;
        }
        gambar.setX(x);
    }

    public ImageView getGambar() {
        return gambar;
    }
}