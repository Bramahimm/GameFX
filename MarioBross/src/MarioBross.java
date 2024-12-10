/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;

abstract class Karakter {
    protected double x, y, width, height;

    public abstract void gerak();
    public abstract boolean cekTabrakan(Karakter obj);

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}

class Pemain extends Karakter {
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

class Musuh extends Karakter {
    private ImageView gambar;

    public Musuh(String imagePath, double x, double tinggi) {
        this.x = x;
        this.width = 90;
        this.height = tinggi; // Tinggi musuh random
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

class Awan {
    private ImageView gambar;
    private double x, y, kecepatan;

    public Awan(String imagePath, double x, double y, double kecepatan) {
        this.x = x;
        this.y = y;
        this.kecepatan = kecepatan;
        try {
            Image img = new Image(imagePath);
            gambar = new ImageView(img);
            gambar.setFitWidth(100); // Ukuran awan
            gambar.setFitHeight(60);
            gambar.setX(x);
            gambar.setY(y);
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar awan");
        }
    }

    public void gerak() {
        x -= kecepatan; // Awan bergerak ke kiri
        if (x + gambar.getFitWidth() < 0) { // Jika keluar layar, reset posisi
            x = 800;
        }
        gambar.setX(x);
    }

    public ImageView getGambar() {
        return gambar;
    }
}

public class MarioBross extends Application {
    private static int skor = 0;
    private static int nyawa = 3;
    private Pemain pemain;
    private List<Musuh> rintangan = new ArrayList<>();
    private List<Awan> awanList = new ArrayList<>();
    private Text teksSkor, teksNyawa;
    private Pane root;
    private Random random = new Random();
    private AnimationTimer gameLoop;

    @Override
    public void start(Stage stage) {
        // Menampilkan Menu Start
        showMainMenu(stage);
    }

    private void showMainMenu(Stage stage) {
        root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        // Background Menu Start
        ImageView backgroundMenu = new ImageView(new Image("file:start.png"));
        backgroundMenu.setFitWidth(800);
        backgroundMenu.setFitHeight(600);
        root.getChildren().add(backgroundMenu);
    
        // Tombol Mulai Permainan
        Button startButton = new Button("Play");
        startButton.setPrefWidth(150);
        startButton.setPrefHeight(50);
        startButton.setLayoutX(315);
        startButton.setLayoutY(245);
        startButton.setStyle("-fx-font: 16px Arial; -fx-background-color: #55ff55; -fx-text-fill: white; -fx-background-radius: 20;");
        startButton.setOnAction(e -> startGame(stage));

        // Tombol Keluar
        Button exitButton = new Button("Keluar");
        exitButton.setPrefWidth(150);
        exitButton.setPrefHeight(50);
        exitButton.setLayoutX(315);
        exitButton.setLayoutY(325);
        exitButton.setStyle("-fx-font: 16px Arial; -fx-background-color: #ff5555; -fx-text-fill: white; -fx-background-radius: 20;");
        exitButton.setOnAction(e -> System.exit(0));

        // Menambahkan tombol ke dalam menu
        root.getChildren().addAll(startButton, exitButton);

        stage.setTitle("Menu Utama");
        stage.setScene(scene);
        stage.show();
    }

    private void startGame(Stage stage) {
        root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        // Background permainan
        ImageView background = new ImageView(new Image("file:backgroundd.jpg"));
        background.setFitWidth(800);
        background.setFitHeight(600);
        root.getChildren().add(background);

        // Tambahkan awan
        for (int i = 0; i < 3; i++) { // Tambah beberapa awan
            double posisiX = 200 * i + random.nextInt(100);
            double posisiY = 50 + random.nextInt(100);
            double kecepatan = 1 + random.nextDouble(); // Kecepatan acak
            Awan awan = new Awan("file:awan.png", posisiX, posisiY, kecepatan);
            awanList.add(awan);
            root.getChildren().add(awan.getGambar());
        }

        // Tanah
        ImageView tanah = new ImageView(new Image("file:blok.jpg"));
        tanah.setFitWidth(800);
        tanah.setFitHeight(65);
        tanah.setY(549);
        root.getChildren().add(tanah);

        // Pemain
        pemain = new Pemain("file:mario.png", 50, 500);
        root.getChildren().add(pemain.getGambar());

        // Teks skor dan nyawa
        teksSkor = new Text(10, 20, "Skor: " + skor);
        teksSkor.setFill(Color.WHITE);
        teksNyawa = new Text(10, 40, "Nyawa: " + nyawa);
        teksNyawa.setFill(Color.WHITE);
        root.getChildren().addAll(teksSkor, teksNyawa);

        // Input kontrol pemain
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.D) pemain.setKecepatanX(5);
            if (e.getCode() == KeyCode.A) pemain.setKecepatanX(-5);
            if (e.getCode() == KeyCode.W) pemain.lompat();
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.A) pemain.setKecepatanX(0);
        });

        // Game loop
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                pemain.gerak();

                // Gerakkan awan
                for (Awan awan : awanList) {
                    awan.gerak();
                }

                for (Musuh pipa : rintangan) {
                    pipa.gerak();
                    if (!pemain.isKebal() && pemain.cekTabrakan(pipa)) {
                        nyawa--;
                        pemain.setKebal();
                        if (nyawa <= 0) {
                            gameOver(root, stage);
                        }
                    }
                }
                teksSkor.setText("Skor: " + ++skor);
                teksNyawa.setText("Nyawa: " + nyawa);
            }
        };
        gameLoop.start();

        // Delay musuh
        delayKemunculanMusuh();

        stage.setTitle("Mario Game");
        stage.setScene(scene);
        stage.show();
    }

    private Timeline musuhTimeline; // Timeline untuk kemunculan musuh

    private void delayKemunculanMusuh() {
        musuhTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            double tinggiRandom = 50 + random.nextInt(220); // Tinggi antara 50-200
            Musuh musuhBaru = new Musuh("file:pipa.png", 800, tinggiRandom);
            rintangan.add(musuhBaru);
            root.getChildren().add(musuhBaru.getGambar());
        }));
        musuhTimeline.setCycleCount(Timeline.INDEFINITE);
        musuhTimeline.play();
    }

    private void gameOver(Pane root, Stage stage) {
        // Hentikan semua animasi
        gameLoop.stop(); // Hentikan AnimationTimer
        if (musuhTimeline != null) musuhTimeline.stop(); // Hentikan Timeline jika berjalan

        // Bersihkan semua elemen di Pane
        root.getChildren().clear();

        // Tambahkan gambar Game Over
        ImageView gameOverImage = new ImageView(new Image("file:gameover.jpg"));
        gameOverImage.setFitWidth(800); // Sesuaikan ukuran gambar
        gameOverImage.setFitHeight(600);
        root.getChildren().add(gameOverImage);

        // Tambahkan teks skor akhir
        Text skorAkhirText = new Text("Skor Akhir: " + skor);
        skorAkhirText.setStyle("-fx-font: 24px Arial; -fx-fill: white;");
        skorAkhirText.setX(300);
        skorAkhirText.setY(250);
        root.getChildren().add(skorAkhirText);

        // Tambahkan tombol Restart
        Button restartButton = new Button("Restart");
        restartButton.setPrefWidth(100);
        restartButton.setPrefHeight(40);
        restartButton.setLayoutX((800 - 220) / 2);
        restartButton.setLayoutY(300);
        restartButton.setStyle("-fx-font: 16px Arial; -fx-background-color: #ff5555; -fx-text-fill: white;");

        restartButton.setOnAction(e -> {
         // Reset data permainan
        skor = 0;
        nyawa = 3;
        rintangan.clear();
        awanList.clear();  // Bersihkan daftar awan jika ada

        // Mulai ulang permainan (memulai game langsung)
        startGame(stage); // Langsung memulai game
    });


        // Tambahkan tombol Exit
        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(100);
        exitButton.setPrefHeight(40);
        exitButton.setLayoutX((800 - 220) / 2 + 120);
        exitButton.setLayoutY(300);
        exitButton.setStyle("-fx-font: 16px Arial; -fx-background-color: #5555ff; -fx-text-fill: white;");

        exitButton.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(restartButton, exitButton);

        stage.setTitle("Game Over");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }
}