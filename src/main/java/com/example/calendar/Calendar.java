package com.example.calendar;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Calendar extends Application {

   private Label name;
    private DatePicker datePicker;
    private TextField eventTextField;
    private ListView<String> eventListView;

    private FileChooser fileChooser;

    private static final int MAX_CHARACTERS = 100;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        showWelcomeScreen(primaryStage);
    }
    private void showWelcomeScreen(Stage primaryStage){
        primaryStage.setTitle("Welcome");

        Label welcome = new Label("Welcome to Event Calendar");
        welcome.setFont(Font.font("Helvetica", FontWeight.BOLD, 15f));
        welcome.setTextFill(Color.DARKBLUE);

        Label usernameLabel = new Label("Enter your name:");
        usernameLabel.setFont(Font.font("Helvetica", FontWeight.BOLD,15f));
        TextField usernameField = new TextField();
        //usernameLabel.setTextFill(Color.DARKGREEN);

        Button loginButton = new Button("Continue");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();

            if (isString(username)) {
                showCalendar(primaryStage, username);
            } else {
                showAlert("Login Failed", "Please enter a username");
            }
        });
        VBox loginLayout = new VBox(10);
        loginLayout.getChildren().addAll(welcome, usernameLabel, usernameField, loginButton);
        loginLayout.setPadding(new Insets(10));

        Scene loginScene = new Scene(loginLayout, 300, 200);
        primaryStage.setScene(loginScene);

        primaryStage.show();

    }
    private boolean isString(String text){
        return text.matches("[a-zA-Z\\s]+");
    }
    private void showCalendar(Stage primaryStage, String username){
        primaryStage.setTitle("Event Calendar");

        this.name = new Label("Hello, " + username);
        this.name.setFont(Font.font("Helvetica", FontWeight.BOLD, 20f));
        this.name.setTextFill(Color.DARKBLUE);

        datePicker = new DatePicker();
        datePicker.setPromptText("Select a date");
        datePicker.setValue(LocalDate.now());

        StringConverter<LocalDate> converter = new StringConverter<>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return LocalDate.parse(string, dateFormatter);
                } catch (DateTimeParseException e) {
                    showAlert("Error", "Please enter a valid date (dd/MM/yyyy).");
                    return null;
                }
            }
        };

        datePicker.setConverter(converter);

        Label eventLabel = new Label("Event:");
        eventLabel.setFont(Font.font("Helvetica",FontWeight.BOLD,15f));
        eventTextField = new TextField();
        eventTextField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_CHARACTERS ? change : null));

        Button saveButton = new Button("Save Event");
        saveButton.setOnAction(e -> saveEvent());
        saveButton.setStyle("-fx-background-color: #374581; -fx-text-fill: white;");

        Button deleteButton = new Button("Delete Event");
        deleteButton.setOnAction(e -> deleteEvent());
        deleteButton.setStyle("-fx-background-color: #690216; -fx-text-fill: white;");

        Button saveToFileButton = new Button("Save to File");
        saveToFileButton.setOnAction(e -> saveToFile(username));
        saveToFileButton.setStyle("-fx-background-color: #374581; -fx-text-fill: white;");


        Button updateButton = new Button("Update Event");
        updateButton.setOnAction(e -> updateEvent());
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button loadFromFileButton = new Button("Load from File");
        loadFromFileButton.setOnAction(e -> loadFromFile());
        loadFromFileButton.setStyle("-fx-background-color: #374581; -fx-text-fill: white;");

        Button backToHomeButton = new Button("Back to Home");
        backToHomeButton.setOnAction(actionEvent -> showWelcomeScreen(primaryStage));
        backToHomeButton.setStyle("-fx-background-color: #374581; -fx-text-fill: white;");

        eventListView = new ListView<>();

        fileChooser = new FileChooser();

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(saveButton, deleteButton, saveToFileButton, loadFromFileButton, updateButton);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(name, datePicker, eventLabel, eventTextField, buttonBox, eventListView, backToHomeButton);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void saveEvent() {
        try {
            LocalDate selectedDate = datePicker.getValue();

            String event = eventTextField.getText();

            if (event.isEmpty()) {
                showAlert("Warning", "Please enter an event.");
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = selectedDate.format(dateFormatter);

            String eventData = formattedDate + ": " + event;
            eventListView.getItems().add(eventData);

            showAlert("Event Saved", "Event has been saved successfully.");
        } catch (NullPointerException e) {
            showAlert("Error", "Please select a date.");
        }
    }


    private void updateEvent() {

            int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String updatedEvent = eventTextField.getText();

                if (updatedEvent.isEmpty()) {
                    showAlert("Warning", "Please enter an updated event.");
                    return;
                }

                String date = datePicker.getValue().toString();
                String eventData = date + ": " + updatedEvent;

                eventListView.getItems().set(selectedIndex, eventData);
                showAlert("Event Updated", "Selected event has been updated.");
            } else {
                showAlert("Warning", "Please select an event to update.");
            }
    }


    private void deleteEvent() {
        try {
            int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                eventListView.getItems().remove(selectedIndex);
                showAlert("Event Deleted", "Selected event has been deleted.");
            } else {
                showAlert("Warning", "Please select an event to delete.");
            }
        } catch (IndexOutOfBoundsException e) {
            showAlert("Error", "No event selected for deletion.");
        }
    }

    private void saveToFile(String username) {
        List<String> events = new ArrayList<>(eventListView.getItems());

        if (events.isEmpty()) {
            showAlert("Warning", "There are no events to save.");
            return;
        }

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println(username);

                for (String event : events) {
                    writer.println(event);
                }
                showAlert("File Saved", "Events and username have been saved to the file.");
            } catch (IOException e) {
                showAlert("Error", "An error occurred while saving the file.");
            }
        }
    }

    private void loadFromFile() {
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String loadedUsername = reader.readLine();

                name.setText("Hello, " + loadedUsername);

                eventListView.getItems().clear();
                String line;
                boolean fileIsEmpty = true;

                while ((line = reader.readLine()) != null) {
                    eventListView.getItems().add(line);
                    fileIsEmpty = false;
                }

                if (fileIsEmpty) {
                    showAlert("Warning", "The file is empty.");
                } else {
                    showAlert("File Loaded", "Events and username have been loaded from the file.");
                }
            } catch (IOException e) {
                showAlert("Error", "An error occurred while loading the file.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}