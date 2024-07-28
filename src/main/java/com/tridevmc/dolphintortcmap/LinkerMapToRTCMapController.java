package com.tridevmc.dolphintortcmap;

import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LinkerMapToRTCMapController {

    private Map<String, LinkerMapEntry> entries = new HashMap<>();

    private File lastDirectory = null;

    @FXML
    public TextField searchField;
    @FXML
    private TableView<LinkerMapEntry> dataTable;

    @FXML
    private TableColumn<LinkerMapEntry, String> addressColumn;
    @FXML
    private TableColumn<LinkerMapEntry, String> sizeColumn;
    @FXML
    private TableColumn<LinkerMapEntry, String> vmaColumn;
    @FXML
    private TableColumn<LinkerMapEntry, String> nameColumn;

    @FXML
    public void initialize() {
        // Check if FXML elements were injected correctly
        if (dataTable == null || addressColumn == null) {
            throw new IllegalStateException("FXML elements not injected properly.");
        }

        // Correctly access fields of the record in the PropertyValueFactory
        addressColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().address()));
        sizeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().size()));
        vmaColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().vma()));
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().name()));

        nameColumn.prefWidthProperty().bind(
                dataTable.widthProperty()
                        .subtract(addressColumn.widthProperty())
                        .subtract(sizeColumn.widthProperty())
                        .subtract(vmaColumn.widthProperty())
                        .subtract(2) // Border
        );

        dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                dataTable.getItems().setAll(entries.values());
            } else {
                var terms = newValue.split("/");
                List<LinkerMapEntry> filteredEntries = entries.values().stream()
                        .filter(entry -> {
                            for (String term : terms) {
                                if (!entry.name().toLowerCase().contains(term.toLowerCase())) {
                                    return false;
                                }
                            }
                            return true;
                        })
                        .collect(Collectors.toList());
                dataTable.getItems().setAll(filteredEntries);
            }
        });
    }

    public void importMapFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        if (lastDirectory != null) {
            fileChooser.setInitialDirectory(lastDirectory);
        } else {
            // Set the initial directory to the current working directory.
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.setTitle("Open Dolphin .map File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Dolphin Map Files", "*.map"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        File selectedFile = fileChooser.showOpenDialog(dataTable.getScene().getWindow());
        if (selectedFile != null) {
            lastDirectory = selectedFile.getParentFile();
        }
        if (selectedFile != null) {
            loadFile(selectedFile);
        }
    }

    public void loadFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<LinkerMapEntry> entries = parseMapFile(lines);
            this.entries = entries.stream().collect(
                    Collectors.toMap(LinkerMapEntry::address, entry -> entry));

            if (this.searchField.getText().isEmpty()) {
                dataTable.getItems().setAll(entries);
            } else {
                List<LinkerMapEntry> filteredEntries = entries.stream()
                        .filter(entry -> entry.name().toLowerCase().contains(this.searchField.getText().toLowerCase()))
                        .collect(Collectors.toList());
                dataTable.getItems().setAll(filteredEntries);
            }
        } catch (IOException e) {
            showAlert("Error reading file", e.getMessage());
        }
    }

    private List<LinkerMapEntry> parseMapFile(List<String> lines) {
        List<LinkerMapEntry> entries = new ArrayList<>();
        // 80005fcc 00000058 80005fcc 0 dbgMenuFightFightPokemonSelect4  dbgMenuFight.o
        // Address, Size, VMA, 0, Name+
        Pattern pattern = Pattern.compile("([0-9a-fA-F]+) ([0-9a-fA-F]+) ([0-9a-fA-F]+) 0 (.+)$");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String address = matcher.group(1);
                String size = matcher.group(2);
                String vma = matcher.group(3);
                String name = matcher.group(4);
                entries.add(new LinkerMapEntry(address, size, vma, name));
            }
        }

        // Sort by name and then by address if names are equal.
        return entries;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void exportSelectedRanges(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save RTC VMD");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("RTC VMD", "*.vmd"));

        if (lastDirectory != null) {
            fileChooser.setInitialDirectory(lastDirectory);
        } else {
            // Set the initial directory to the current working directory.
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        }

        File directory = fileChooser.showSaveDialog(dataTable.getScene().getWindow());
        if (directory != null) {
            try {
                var builder = new VMD.Builder();
                // Set the name of the VMD to the name of the file.
                builder.vmdName(directory.getName());
                // Set the addRanges to the selected ranges, making sure to subtract the base address of 80000000.
                builder.addRanges(dataTable.getSelectionModel().getSelectedItems().stream()
                        .map(LinkerMapEntry::addressRange)
                        .toArray(int[][]::new));
                VMD vmd = builder.build();
                // Write the VMD to the file, after touching the file to ensure it exists.
                Files.createFile(directory.toPath());
                Files.writeString(directory.toPath(), new Gson().toJson(vmd));

                showSuccessAlert("Export Successful", "Exported VMD to " + directory.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Export Failed", e.getMessage());
            }
        }
    }
}

