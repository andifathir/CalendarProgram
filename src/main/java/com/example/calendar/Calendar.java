package com.example.calendar;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Calendar extends Application {

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
        primaryStage.setTitle("Event Calendar");

        // DatePicker
        datePicker = new DatePicker();
        datePicker.setPromptText("Select a date");

        // Event TextField
        Label eventLabel = new Label("Event:");
        eventTextField = new TextField();
        eventTextField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= MAX_CHARACTERS ? change : null));


        // Save Button
        Button saveButton = new Button("Save Event");
        saveButton.setOnAction(e -> saveEvent());

        // Delete Button
        Button deleteButton = new Button("Delete Event");
        deleteButton.setOnAction(e -> deleteEvent());

        // Save to File Button
        Button saveToFileButton = new Button("Save to File");
        saveToFileButton.setOnAction(e -> saveToFile());

        // Load from File Button
        Button loadFromFileButton = new Button("Load from File");
        loadFromFileButton.setOnAction(e -> loadFromFile());

        // Event ListView
        eventListView = new ListView<>();

        // FileChooser
        fileChooser = new FileChooser();

        // Layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(datePicker, eventLabel, eventTextField, saveButton, deleteButton, saveToFileButton, loadFromFileButton, eventListView);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void saveEvent() {
        try {
            String date = datePicker.getValue().toString();
            String event = eventTextField.getText();

            if (date.isEmpty() || event.isEmpty()) {
                showAlert("Warning", "Please select a date and enter an event.");
                return;
            }

            String eventData = date + ": " + event;
            eventListView.getItems().add(eventData);

            showAlert("Event Saved", "Event has been saved successfully.");
        } catch (NullPointerException e) {
            showAlert("Error", "Please select a date.");
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

    private void saveToFile() {
        try {
            List<String> events = new ArrayList<>(eventListView.getItems());

            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    for (String event : events) {
                        writer.println(event);
                    }
                    showAlert("File Saved", "Events have been saved to the file.");
                } catch (IOException e) {
                    showAlert("Error", "An error occurred while saving the file.");
                }
            }
        } catch (NullPointerException e) {
            showAlert("Error", "No events to save.");
        }
    }

    private void loadFromFile() {
        try {
            File file = fileChooser.showOpenDialog(null);

            if (file != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    eventListView.getItems().clear();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        eventListView.getItems().add(line);
                    }
                    showAlert("File Loaded", "Events have been loaded from the file.");
                } catch (IOException e) {
                    showAlert("Error", "An error occurred while loading the file.");
                }
            }
        } catch (NullPointerException e) {
            showAlert("Error", "No file selected to load events from.");
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