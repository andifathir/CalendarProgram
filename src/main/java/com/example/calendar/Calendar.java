package com.example.calendar;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        eventTextField = new TextField();
        eventTextField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_CHARACTERS ? change : null));

        Button saveButton = new Button("Save Event");
        saveButton.setOnAction(e -> saveEvent());

        Button deleteButton = new Button("Delete Event");
        deleteButton.setOnAction(e -> deleteEvent());

        Button saveToFileButton = new Button("Save to File");
        saveToFileButton.setOnAction(e -> saveToFile());

        Button updateButton = new Button("Update Event");
        updateButton.setOnAction(e -> updateEvent());

        Button loadFromFileButton = new Button("Load from File");
        loadFromFileButton.setOnAction(e -> loadFromFile());

        eventListView = new ListView<>();

        fileChooser = new FileChooser();

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(saveButton, deleteButton, saveToFileButton, loadFromFileButton, updateButton);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(datePicker, eventLabel, eventTextField, buttonBox, eventListView);

        Scene scene = new Scene(layout, 600, 600);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void saveEvent() {
        try {
            LocalDate selectedDate = datePicker.getValue();

            if (selectedDate == null) {
                showAlert("Error", "Please select a date.");
                return;
            }

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
        try {
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
        } catch (NullPointerException e) {
            showAlert("Error", "No event selected for update.");
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