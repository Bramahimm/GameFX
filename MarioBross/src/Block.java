import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Block {
    private ImageView gambar;
    private double y, kecepatan, x;

    public Block(String imagePath, int x, int y, int kecepatan) {
        this.y = y;
        this.kecepatan = kecepatan;
        this.x = x;

        try {
            Image img = new Image(imagePath);
            gambar = new ImageView(img);
            gambar.setFitWidth(800);
            gambar.setFitHeight(65);
            gambar.setY(y);
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar blok");
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
