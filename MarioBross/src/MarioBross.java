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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarioBross extends Application {
    private static int skor = 0;
    private static int nyawa = 3;
    private Pemain pemain;
    private List<Musuh> rintangan = new ArrayList<>();
    private List<Awan> awanList = new ArrayList<>();
    private List<Block> blockList = new ArrayList<>();
    private Text teksSkor, teksNyawa;
    private Pane root;
    private Random random = new Random();
    private AnimationTimer gameLoop;
    private Timeline musuhTimeline;

    @Override
    public void start(Stage stage) {
        showMainMenu(stage);
    }

    private void showMainMenu(Stage stage) {
        root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        ImageView backgroundMenu = new ImageView(new Image("file:start.png"));
        backgroundMenu.setFitWidth(800);
        backgroundMenu.setFitHeight(600);
        root.getChildren().add(backgroundMenu);

        Button startButton = new Button("Play");
        startButton.setPrefWidth(150);
        startButton.setPrefHeight(50);
        startButton.setLayoutX(315);
        startButton.setLayoutY(245);
        startButton.setStyle("-fx-font: 16px Arial; -fx-background-color: #0000FF; -fx-text-fill: white; -fx-background-radius: 20;");
        startButton.setOnAction(e -> startGame(stage));

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(150);
        exitButton.setPrefHeight(50);
        exitButton.setLayoutX(315);
        exitButton.setLayoutY(325);
        exitButton.setStyle("-fx-font: 16px Arial; -fx-background-color: #ff5555; -fx-text-fill: white; -fx-background-radius: 20;");
        exitButton.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(startButton, exitButton);

        stage.setTitle("Menu Utama");
        stage.setScene(scene);
        stage.show();
    }

    private void startGame(Stage stage) {
        root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        // Background
        ImageView background = new ImageView(new Image("file:backgroundd.jpg"));
        background.setFitWidth(800);
        background.setFitHeight(600);
        root.getChildren().add(background);

        // Blok tanah
        for (int i = 0; i < 11; i++) {
            Block block = new Block("file:blok.jpg", i * 80, 549, 2);
            blockList.add(block);
            root.getChildren().add(block.getGambar());
        }

        // Awan
        for (int i = 0; i < 3; i++) {
            double posisiX = 200 * i + random.nextInt(100);
            double posisiY = 50 + random.nextInt(100);
            double kecepatan = 1 + random.nextDouble();
            Awan awan = new Awan("file:awan.png", posisiX, posisiY, kecepatan);
            awanList.add(awan);
            root.getChildren().add(awan.getGambar());
        }

        // Pemain
        pemain = new Pemain("file:mario.png", 50, 500);
        root.getChildren().add(pemain.getGambar());

        // Teks skor dan nyawa
        teksSkor = new Text(15, 20, "Skor: " + skor);
        teksSkor.setFill(Color.WHITE);
        teksNyawa = new Text(15, 40, "Nyawa: " + nyawa);
        teksNyawa.setFill(Color.WHITE);
        root.getChildren().addAll(teksSkor, teksNyawa);

        // Input pemain
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.D) pemain.setKecepatanX(5);
            if (e.getCode() == KeyCode.A) pemain.setKecepatanX(-5);
            if (e.getCode() == KeyCode.W) pemain.lompat();
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.A) pemain.setKecepatanX(0);
        });

        // Loop permainan
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                pemain.gerak();

                for (Awan awan : awanList) {
                    awan.gerak();
                }
                for (Block block : blockList) {
                    block.gerak();
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

        delayKemunculanMusuh();

        stage.setTitle("Mario Game");
        stage.setScene(scene);
        stage.show();
    }

    private void delayKemunculanMusuh() {
        musuhTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            double tinggiRandom = 50 + random.nextInt(220);
            Musuh musuhBaru = new Musuh("file:pipa.png", 800, tinggiRandom);
            rintangan.add(musuhBaru);
            root.getChildren().add(musuhBaru.getGambar());
        }));
        musuhTimeline.setCycleCount(Timeline.INDEFINITE);
        musuhTimeline.play();
    }

    private void gameOver(Pane root, Stage stage) {
        gameLoop.stop();
        if (musuhTimeline != null) musuhTimeline.stop();

        root.getChildren().clear();

        ImageView gameOverImage = new ImageView(new Image("file:gameover.jpg"));
        gameOverImage.setFitWidth(800);
        gameOverImage.setFitHeight(600);
        root.getChildren().add(gameOverImage);

        Text skorAkhirText = new Text("Skor Akhir: " + skor);
        skorAkhirText.setStyle("-fx-font: 24px Arial; -fx-fill: white;");
        skorAkhirText.setX(300);
        skorAkhirText.setY(250);
        root.getChildren().add(skorAkhirText);

        Button restartButton = new Button("Restart");
        restartButton.setPrefWidth(100);
        restartButton.setPrefHeight(40);
        restartButton.setLayoutX((800 - 220) / 2);
        restartButton.setLayoutY(300);
        restartButton.setStyle("-fx-font: 16px Arial; -fx-background-color: #0000FF; -fx-text-fill: white;");

        restartButton.setOnAction(e -> {
            skor = 0;
            nyawa = 3;
            rintangan.clear();
            awanList.clear();
            startGame(stage);
        });

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(100);
        exitButton.setPrefHeight(40);
        exitButton.setLayoutX((800 - 220) / 2 + 120);
        exitButton.setLayoutY(300);
        exitButton.setStyle("-fx-font: 16px Arial; -fx-background-color: #ff5555; -fx-text-fill: white;");
        exitButton.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(restartButton, exitButton);
    }

    public static void main(String[] args) {
        launch(args);
    }
}