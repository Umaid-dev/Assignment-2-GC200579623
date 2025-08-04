package com.example.assignment2gc200579623;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class HelloApplication extends Application {

    private static final String NASA_API_KEY = "xvVkBuTYch37GdspqSqGmaTAe0X7a7xsWqCeHJOz"; // Used my own api key
    private static final String APOD_URL = "https://api.nasa.gov/planetary/apod?api_key=" + NASA_API_KEY;
    private static final String MARS_ROVER_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&api_key=" + NASA_API_KEY;
    private static final String NEO_URL = "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + "&end_date=" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + "&api_key=" + NASA_API_KEY;

    private Stage primaryStage;
    private BorderPane mainLayout;
    private VBox contentArea;
    private Label statusLabel;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        setupUI(); // Set up all main layouts and controls
        primaryStage.setTitle("NASA Space Explorer - Assignment 2");

        // Try to load icon, but don't fail if it doesn't exist
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/assignment2gc200579623/space-icon.png")));
        } catch (Exception e) {
            System.out.println("Icon not found, using default");
        }

        primaryStage.show();
    }

    private void setupUI() {
        mainLayout = new BorderPane();

        // Create header
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Create navigation
        VBox navigation = createNavigation();
        mainLayout.setLeft(navigation);

        // Create content area
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        contentArea.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");

        mainLayout.setCenter(scrollPane);

        // Create status bar
        statusLabel = new Label("Ready to explore space! 🚀");
        statusLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 12px;");
        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #1a1a2e;");
        mainLayout.setBottom(statusBar);

        // Apply main styling
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #0f0f23, #16213e);");

        Scene scene = new Scene(mainLayout, 1200, 800);

        // Try to load CSS, but don't fail if it doesn't exist
        try {
            scene.getStylesheets().add(getClass().getResource("/com/example/assignment2gc200579623/style.css"
            ).toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found, using default styling");
        }

        primaryStage.setScene(scene);

        // Load initial content
        loadAPOD();
    }

    private VBox createHeader() {
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #0f0f23, #16213e, #0f0f23);");

        Label title = new Label("🚀 NASA Space Explorer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(5, Color.BLACK));

        Label subtitle = new Label("Discover the Universe Through NASA's Eyes");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.LIGHTGRAY);

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createNavigation() {
        VBox nav = new VBox(10);
        nav.setPadding(new Insets(20));
        nav.setStyle("-fx-background-color: #16213e; -fx-border-color: #2e3440; -fx-border-width: 0 1 0 0;");
        nav.setPrefWidth(200);

        Label navTitle = new Label("🌌 Explore");
        navTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        navTitle.setTextFill(Color.WHITE);

        Button apodBtn = createNavButton("📸 Picture of the Day", this::loadAPOD);
        Button marsBtn = createNavButton("🔴 Mars Rover Photos", this::loadMarsPhotos);
        Button neoBtn = createNavButton("☄️ Near Earth Objects", this::loadNearEarthObjects);
        Button aboutBtn = createNavButton("ℹ️ About", this::showAbout);

        nav.getChildren().addAll(navTitle, new Separator(), apodBtn, marsBtn, neoBtn, new Separator(), aboutBtn);
        return nav;
    }

    private Button createNavButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefWidth(160);
        btn.setStyle(
                "-fx-background-color: #2e3440; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #5e81ac; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-cursor: hand;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #2e3440; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10px; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-cursor: hand;"
        ));

        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void loadAPOD() {
        clearContent();
        updateStatus("Loading Astronomy Picture of the Day...");

        Task<JSONObject> task = new Task<JSONObject>() {
            @Override
            protected JSONObject call() throws Exception {
                return makeAPICall(APOD_URL);
            }
        };

        task.setOnSucceeded(e -> {
            JSONObject apod = task.getValue();
            displayAPOD(apod);
            updateStatus("Astronomy Picture of the Day loaded successfully! ✨");
        });

        task.setOnFailed(e -> {
            updateStatus("Failed to load APOD. Please check your internet connection.");
            showError("Failed to load Astronomy Picture of the Day");
        });

        new Thread(task).start();
    }

    private void loadMarsPhotos() {
        clearContent();
        updateStatus("Loading Mars Rover photos...");

        Task<JSONObject> task = new Task<JSONObject>() {
            @Override
            protected JSONObject call() throws Exception {
                return makeAPICall(MARS_ROVER_URL);
            }
        };

        task.setOnSucceeded(e -> {
            JSONObject marsData = task.getValue();
            displayMarsPhotos(marsData);
            updateStatus("Mars Rover photos loaded successfully! ");
        });

        task.setOnFailed(e -> {
            updateStatus("Failed to load Mars photos. Please check your internet connection.");
            showError("Failed to load Mars Rover photos");
        });

        new Thread(task).start();
    }

    private void loadNearEarthObjects() {
        clearContent();
        updateStatus("Loading Near Earth Objects data...");

        Task<JSONObject> task = new Task<JSONObject>() {
            @Override
            protected JSONObject call() throws Exception {
                return makeAPICall(NEO_URL);
            }
        };

        task.setOnSucceeded(e -> {
            JSONObject neoData = task.getValue();
            displayNearEarthObjects(neoData);
            updateStatus("Near Earth Objects data loaded successfully! ☄️");
        });

        task.setOnFailed(e -> {
            updateStatus("Failed to load NEO data. Please check your internet connection.");
            showError("Failed to load Near Earth Objects data");
        });

        new Thread(task).start();
    }

    private void displayAPOD(JSONObject apod) {
        VBox apodBox = new VBox(15);
        apodBox.setAlignment(Pos.CENTER);
        apodBox.setPadding(new Insets(20));
        apodBox.setStyle("-fx-background-color: rgba(46, 52, 64, 0.8); -fx-background-radius: 15px;");

        Label title = new Label(apod.getString("title"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        title.setWrapText(true);

        Label date = new Label("📅 Date: " + apod.getString("date"));
        date.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        date.setTextFill(Color.LIGHTGRAY);

        if (apod.getString("media_type").equals("image")) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(600);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            // Load image asynchronously one by one
            CompletableFuture.supplyAsync(() -> {
                try {
                    return new Image(apod.getString("url"), true);
                } catch (Exception e) {
                    return null;
                }
            }).thenAccept(image -> {
                if (image != null) {
                    Platform.runLater(() -> imageView.setImage(image));
                }
            });

            apodBox.getChildren().add(imageView);
        }

        Label explanation = new Label(apod.getString("explanation"));
        explanation.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        explanation.setTextFill(Color.WHITE);
        explanation.setWrapText(true);
        explanation.setMaxWidth(700);

        apodBox.getChildren().addAll(title, date, explanation);
        contentArea.getChildren().add(apodBox);
    }
    // Example of a comment for image loading in Mars photos
    private void displayMarsPhotos(JSONObject marsData) {
        JSONArray photos = marsData.getJSONArray("photos");

        Label title = new Label("Mars Rover Photos - Curiosity");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        contentArea.getChildren().add(title);

        GridPane photoGrid = new GridPane();
        photoGrid.setHgap(15);
        photoGrid.setVgap(15);
        photoGrid.setPadding(new Insets(20));
        photoGrid.setAlignment(Pos.CENTER);

        int maxPhotos = Math.min(6, photos.length());
        int col = 0, row = 0;

        // Add debug information
        System.out.println("Total photos available: " + photos.length());

        for (int i = 0; i < maxPhotos; i++) {
            JSONObject photo = photos.getJSONObject(i);

            VBox photoBox = new VBox(10);
            photoBox.setAlignment(Pos.CENTER);
            photoBox.setPadding(new Insets(10));
            photoBox.setStyle("-fx-background-color: rgba(46, 52, 64, 0.8); -fx-background-radius: 10px;");

            ImageView imageView = new ImageView();
            imageView.setFitWidth(180);
            imageView.setFitHeight(180);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            // Add placeholder while loading
            imageView.setStyle("-fx-background-color: #3b4252; -fx-border-color: #5e81ac; -fx-border-width: 2;");

            // Get image URL and convert HTTP to HTTPS if needed
            String imageUrl = photo.getString("img_src");
            if (imageUrl.startsWith("http://")) {
                imageUrl = imageUrl.replace("http://", "https://");
            }

            System.out.println("Loading image: " + imageUrl);

            // Load image with better error handling
            final String finalImageUrl = imageUrl;
            Task<Image> imageTask = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    try {
                        // Add connection timeout and user agent
                        Image image = new Image(finalImageUrl, 180, 180, true, true, true);

                        // Check if image loaded successfully
                        if (image.isError()) {
                            System.err.println("Image loading error for: " + finalImageUrl);
                            System.err.println("Error: " + image.getException());
                            return null;
                        }
                        return image;
                    } catch (Exception e) {
                        System.err.println("Exception loading image: " + finalImageUrl);
                        e.printStackTrace();
                        return null;
                    }
                }
            };

            imageTask.setOnSucceeded(e -> {
                Image image = imageTask.getValue();
                if (image != null && !image.isError()) {
                    Platform.runLater(() -> {
                        imageView.setImage(image);
                        System.out.println("Successfully loaded image: " + finalImageUrl);
                    });
                } else {
                    Platform.runLater(() -> {
                        // Show error placeholder
                        Label errorLabel = new Label("\nImage\nFailed");
                        errorLabel.setTextFill(Color.RED);
                        errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                        errorLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

                        VBox errorBox = new VBox(errorLabel);
                        errorBox.setAlignment(Pos.CENTER);
                        errorBox.setPrefSize(180, 180);
                        errorBox.setStyle("-fx-background-color: #bf616a; -fx-border-color: #d08770; -fx-border-width: 2;");

                        // Replace imageView with error box in parent
                        photoBox.getChildren().set(0, errorBox);
                        System.err.println("Showing error placeholder for: " + finalImageUrl);
                    });
                }
            });

            imageTask.setOnFailed(e -> {
                Platform.runLater(() -> {
                    Label errorLabel = new Label("\nNetwork\nError");
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                    errorLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

                    VBox errorBox = new VBox(errorLabel);
                    errorBox.setAlignment(Pos.CENTER);
                    errorBox.setPrefSize(180, 180);
                    errorBox.setStyle("-fx-background-color: #bf616a; -fx-border-color: #d08770; -fx-border-width: 2;");

                    photoBox.getChildren().set(0, errorBox);
                    System.err.println("Network error loading: " + finalImageUrl);
                    if (imageTask.getException() != null) {
                        imageTask.getException().printStackTrace();
                    }
                });
            });

            // Start the image loading task
            new Thread(imageTask).start();

            // Add camera and sol information
            Label photoInfo = new Label("Sol: " + photo.getInt("sol"));
            photoInfo.setTextFill(Color.LIGHTGRAY);
            photoInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

            // Add camera info if available
            if (photo.has("camera")) {
                JSONObject camera = photo.getJSONObject("camera");
                Label cameraInfo = new Label("Camera: " + camera.getString("name"));
                cameraInfo.setTextFill(Color.LIGHTBLUE);
                cameraInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
                photoBox.getChildren().addAll(imageView, photoInfo, cameraInfo);
            } else {
                photoBox.getChildren().addAll(imageView, photoInfo);
            }

            photoGrid.add(photoBox, col, row);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }

        contentArea.getChildren().add(photoGrid);

        // Add loading status
        if (photos.length() == 0) {
            Label noPhotos = new Label("No photos available for Sol 1000");
            noPhotos.setTextFill(Color.ORANGE);
            noPhotos.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            contentArea.getChildren().add(noPhotos);
        }
    }

    private void displayNearEarthObjects(JSONObject neoData) {
        Label title = new Label("☄️ Near Earth Objects - Today");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        contentArea.getChildren().add(title);

        JSONObject nearEarthObjects = neoData.getJSONObject("near_earth_objects");
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        if (nearEarthObjects.has(today)) {
            JSONArray asteroids = nearEarthObjects.getJSONArray(today);

            VBox asteroidsBox = new VBox(10);
            asteroidsBox.setPadding(new Insets(20));

            for (int i = 0; i < Math.min(5, asteroids.length()); i++) {
                JSONObject asteroid = asteroids.getJSONObject(i);

                VBox asteroidBox = new VBox(5);
                asteroidBox.setPadding(new Insets(15));
                asteroidBox.setStyle("-fx-background-color: rgba(46, 52, 64, 0.8); -fx-background-radius: 10px;");

                Label name = new Label(asteroid.getString("name"));
                name.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                name.setTextFill(Color.WHITE);

                JSONObject estimatedDiameter = asteroid.getJSONObject("estimated_diameter").getJSONObject("kilometers");
                Label diameter = new Label(String.format("📏 Diameter: %.2f - %.2f km",
                        estimatedDiameter.getDouble("estimated_diameter_min"),
                        estimatedDiameter.getDouble("estimated_diameter_max")));
                diameter.setTextFill(Color.LIGHTGRAY);

                boolean isPotentiallyHazardous = asteroid.getBoolean("is_potentially_hazardous_asteroid");
                Label hazard = new Label("⚠️ Potentially Hazardous: " + (isPotentiallyHazardous ? "Yes" : "No"));
                hazard.setTextFill(isPotentiallyHazardous ? Color.ORANGE : Color.LIGHTGREEN);

                asteroidBox.getChildren().addAll(name, diameter, hazard);
                asteroidsBox.getChildren().add(asteroidBox);
            }

            contentArea.getChildren().add(asteroidsBox);
        } else {
            Label noData = new Label("No Near Earth Objects data available for today.");
            noData.setTextFill(Color.LIGHTGRAY);
            contentArea.getChildren().add(noData);
        }
    }

    private void showAbout() {
        clearContent();

        VBox aboutBox = new VBox(20);
        aboutBox.setAlignment(Pos.CENTER);
        aboutBox.setPadding(new Insets(40));
        aboutBox.setStyle("-fx-background-color: rgba(46, 52, 64, 0.8); -fx-background-radius: 15px;");

        Label title = new Label("🚀 About NASA Space Explorer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        Label description = new Label(
                "This application showcases the wonders of space exploration through NASA's public APIs.\n\n" +
                        "Features:\n" +
                        "📸 Astronomy Picture of the Day\n" +
                        "🔴 Mars Rover Photos from Curiosity\n" +
                        "☄️ Near Earth Objects tracking\n\n" +
                        "Data provided by NASA's Open Data Portal\n" +
                        "Built with JavaFX for Assignment 2\n" +
                        "Student: GC200579623"
        );
        description.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        description.setTextFill(Color.LIGHTGRAY);
        description.setWrapText(true);
        description.setMaxWidth(600);
        description.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        aboutBox.getChildren().addAll(title, description);
        contentArea.getChildren().add(aboutBox);

        updateStatus("About information displayed ℹ️");
    }

    private JSONObject makeAPICall(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "JavaFX-NASA-Explorer");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP error code: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return new JSONObject(response.toString());
    }

    private void clearContent() {
        contentArea.getChildren().clear();
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            VBox errorBox = new VBox(10);
            errorBox.setAlignment(Pos.CENTER);
            errorBox.setPadding(new Insets(20));
            errorBox.setStyle("-fx-background-color: rgba(191, 97, 106, 0.8); -fx-background-radius: 10px;");

            Label errorLabel = new Label("❌ Error: " + message);
            errorLabel.setTextFill(Color.WHITE);
            errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            Label suggestion = new Label("Please check your internet connection and try again.");
            suggestion.setTextFill(Color.LIGHTGRAY);

            errorBox.getChildren().addAll(errorLabel, suggestion);
            contentArea.getChildren().add(errorBox);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}