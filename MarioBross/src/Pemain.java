import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pemain extends Karakter {
    private ImageView gambar;
    private double kecepatanX = 0, kecepatanY = 0;
    private boolean bisaMelompat = false;
    private final double GRAVITASI = 0.5, TENAGA_LOMPAT = -19;
    private boolean kebal = false;
    private long waktuKebal = 0;
    private static final long DURASI_KEBAL = 2000000000L;

    public Pemain(String imagePath, double x, double y) {
        this.x = x;
        this.y = y;
        this.width = 80;
        this.height = 100;
        try {
            Image img = new Image(imagePath);
            gambar = new ImageView(img);
            gambar.setFitWidth(width);
            gambar.setFitHeight(height);
            gambar.setX(x);
            gambar.setY(y);
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar pemain");
        }
    }

    @Override
    public void gerak() {
        kecepatanY += GRAVITASI;
        x += kecepatanX;
        y += kecepatanY;

        if (x < 0) x = 0;
        if (x > 800 - width) x = 800 - width;

        if (y + height >= 600 - 50) {
            y = 600 - 50 - height;
            kecepatanY = 0;
            bisaMelompat = true;
        }

        gambar.setX(x);
        gambar.setY(y);

        if (kebal && System.nanoTime() - waktuKebal > DURASI_KEBAL) {
            kebal = false;
            gambar.setOpacity(1);
        }
    }

    @Override
    public boolean cekTabrakan(Karakter obj) {
        return gambar.getBoundsInParent().intersects(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
    }

    public ImageView getGambar() {
        return gambar;
    }

    public void lompat() {
        if (bisaMelompat) {
            kecepatanY = TENAGA_LOMPAT;
            bisaMelompat = false;
        }
    }

    public void setKecepatanX(double kecepatanX) {
        this.kecepatanX = kecepatanX;
    }

    public void setKebal() {
        kebal = true;
        waktuKebal = System.nanoTime();
        gambar.setOpacity(0.5);
    }

    public boolean isKebal() {
        return kebal;
    }
}
