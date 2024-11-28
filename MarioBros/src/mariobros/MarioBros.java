import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MarioBros extends Application {

    // Dimensi layar
    private static final int LEBAR = 800;
    private static final int TINGGI = 600;

    // Pemain dan tanah
    private Rectangle pemain;
    private  List<Rectangle> segmenTanah = new ArrayList<>();
    private  List<Rectangle> rintangan = new ArrayList<>();

    // Kecepatan pemain
    private double kecepatanX = 0;
    private double kecepatanY = 0;

    // Gravitasi dan lompat
    private final double GRAVITASI = 0.5;
    private final double TENAGA_LOMPAT = -10;

    // Status lompatan
    private boolean bisaMelompat = false;

    // Kecepatan tanah dan rintangan
    private double KECEPATAN_GULUNG = 2;
    

    // Skor dan nyawa
    private int skor = 0;
    private int nyawa = 3;

    // Komponen tampilan
    private Text teksSkor;
    private  List<ImageView> gambarNyawa = new ArrayList<>();
    private  Image gambarHati = new Image("file:/D:/BramBelajarNgoding/BramBelajarJava/GambarHati.jpg", true);


    

    @Override
    public void start(Stage stageUtama) {
        // Root container
        Pane root = new Pane();
        root.setPrefSize(LEBAR, TINGGI);

        // Scene awal dengan teks Play
        Scene menuScene = createMenuScene(stageUtama);
        stageUtama.setTitle("Simple Mario Game");
        stageUtama.setScene(menuScene);
        stageUtama.show();
    }

    // Membuat scene menu yang menampilkan "Play"
    private Scene createMenuScene(Stage stageUtama) {
        Pane menuRoot = new Pane();
        menuRoot.setPrefSize(LEBAR, TINGGI);

        Text teksPlay = new Text(LEBAR / 2 - 50, TINGGI / 2, "Press Enter to Play");
        teksPlay.setFill(Color.WHITE);
        teksPlay.setStyle("-fx-font-size: 24px;");
        menuRoot.getChildren().add(teksPlay);

        Scene menuScene = new Scene(menuRoot, LEBAR, TINGGI);
        menuScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                // Ketika Enter ditekan, mulai permainan
                startGame(stageUtama);
            }
        });

        return menuScene;
    }

    // Memulai permainan
    private void startGame(Stage stageUtama) {
        Pane root = new Pane();
        root.setPrefSize(LEBAR, TINGGI);

        // Buat tanah (Ground) dalam segmen
        for (int i = 0; i < 10; i++) {
            Rectangle tanah = new Rectangle(i * 100, TINGGI - 50, 100, 50);
            tanah.setFill(Color.GREEN);
            segmenTanah.add(tanah);
            root.getChildren().add(tanah);
        }

        // Pemain (Player)
        pemain = new Rectangle(50, TINGGI - 100, 30, 50);
        pemain.setFill(Color.BLUE);

        // Tambahkan rintangan (Obstacles)
        tambahkanRintangan(root, 400);
        tambahkanRintangan(root, 700);

        // Tambahkan objek ke root
        root.getChildren().add(pemain);

        // Kolom Skor
        teksSkor = new Text(10, 20, "Skor: " + skor);
        teksSkor.setFill(Color.WHITE);
        teksSkor.setStyle("-fx-font-size: 18px;");
        root.getChildren().add(teksSkor);

        // Gambar nyawa di pojok kiri atas
        for (int i = 0; i < nyawa; i++) {
            ImageView nyawaGambar = new ImageView(gambarHati);
            nyawaGambar.setX(10 + i * 40); // Mengatur posisi gambar hati secara horizontal
            nyawaGambar.setY(10); // Mengatur posisi gambar hati secara vertikal
            nyawaGambar.setFitWidth(30); // Menyesuaikan ukuran gambar
            nyawaGambar.setFitHeight(30);
            gambarNyawa.add(nyawaGambar);
            root.getChildren().add(nyawaGambar);
        }

        // Scene dan event handler
        Scene gameScene = new Scene(root);
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.D) kecepatanX = 5; // D untuk kanan
            if (e.getCode() == KeyCode.A) kecepatanX = -5; // A untuk kiri
            if (e.getCode() == KeyCode.W && bisaMelompat) { // W untuk lompat
                kecepatanY = TENAGA_LOMPAT;
                bisaMelompat = false; // Hanya bisa melompat sekali
            }
        });

        gameScene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.A) kecepatanX = 0; // Berhenti saat tombol dilepas
        });

        // Loop animasi
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                perbarui();
            }
        }.start();

        // Ganti scene ke permainan
        stageUtama.setScene(gameScene);
    }

    private void tambahkanRintangan(Pane root, double posisiX) {
        Rectangle rintanganBaru = new Rectangle(posisiX, TINGGI - 100, 30, 50);
        rintanganBaru.setFill(Color.RED);
        rintangan.add(rintanganBaru);
        root.getChildren().add(rintanganBaru);
    }

    private void perbarui() {
        // Update posisi horizontal pemain
        pemain.setX(pemain.getX() + kecepatanX);

        // Gravitasi
        kecepatanY += GRAVITASI;
        pemain.setY(pemain.getY() + kecepatanY);

        // Cek batas layar
        if (pemain.getX() < 0) pemain.setX(0);
        if (pemain.getX() > LEBAR - pemain.getWidth()) pemain.setX(LEBAR - pemain.getWidth());

        // Cek jika pemain menyentuh tanah
        if (pemain.getY() + pemain.getHeight() >= TINGGI - 50) {
            pemain.setY(TINGGI - 50 - pemain.getHeight());
            kecepatanY = 0;
            bisaMelompat = true;
        }

        // Gerakkan tanah (Ground scrolling)
        for (Rectangle tanah : segmenTanah) {
            tanah.setX(tanah.getX() - KECEPATAN_GULUNG);
            if (tanah.getX() + tanah.getWidth() <= 0) {
                tanah.setX(LEBAR);
            }
        }

        // Gerakkan rintangan (Obstacle scrolling)
        for (Rectangle rintanganBaru : rintangan) {
            rintanganBaru.setX(rintanganBaru.getX() - KECEPATAN_GULUNG);
            if (rintanganBaru.getX() + rintanganBaru.getWidth() <= 0) {
                rintanganBaru.setX(LEBAR + Math.random() * 200); // Pindahkan ke posisi baru
                skor++; // Tambah skor jika berhasil melewati rintangan
            }

            // Deteksi tabrakanddd
            if (pemain.getBoundsInParent().intersects(rintanganBaru.getBoundsInParent())) {
                nyawa=nyawa-1; // Kurangi nyawa jika terkena rintangan
                if (nyawa <= 0) {
                    System.out.println("Game Over!");
                    // Setelah game over, tampilkan pesan atau jalankan perintah lainnya
                    Platform.runLater(() -> {
                        System.out.println("Game Over! Skor Akhir: " + skor);
                        Platform.exit(); // Keluar dari permainan
                    });
                    return; // Hentikan update lebih lanjut
                }

                // Hapus gambar nyawa yang hilang
                if (nyawa >= 0 && nyawa < gambarNyawa.size()) {
                    gambarNyawa.get(nyawa).setVisible(false); // Nyawa yang hilang dihilangkan dari layar
                }
            }
        }

        // Update tampilan skor dan nyawa
        teksSkor.setText("Skor: " + skor);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
