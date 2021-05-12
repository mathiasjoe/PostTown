package ntnu.idatx2001.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.opencsv.exceptions.CsvException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import ntnu.idatx2001.model.PostTown;
import ntnu.idatx2001.model.PostTownRegister;
import ntnu.idatx2001.ui.views.DialogFactory;

/**
 * The type Primary controller.
 */
public class PrimaryController implements Initializable {

    private PostTownRegister postTownRegister;
    private ObservableList<PostTown> observableList;

    @FXML private TableView<PostTown> viewDetails;
    @FXML private TableColumn<PostTown, String> postCodeCol;
    @FXML private TableColumn<PostTown, String> cityCol;
    @FXML private TableColumn<PostTown, String> munCol;
    @FXML private TextField searchField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.postTownRegister = new PostTownRegister();
        //this.fillWithDummies(); -for testing

        postCodeCol.setCellValueFactory(new PropertyValueFactory<>("PostalCode"));
        cityCol.setCellValueFactory(new PropertyValueFactory<>("City"));
        munCol.setCellValueFactory(new PropertyValueFactory<>("Municipality"));

        this.observableList = FXCollections.observableArrayList(this.postTownRegister.getTowns());
        viewDetails.setItems(this.observableList);

    }

    private PostTownRegister fillWithDummies(){

        postTownRegister.addNewPostTown(new PostTown("6005", "Ålesund", "Ålesund"));
        postTownRegister.addNewPostTown(new PostTown("9602", "Hammerfest", "Hammerfest"));
        postTownRegister.addNewPostTown(new PostTown("9690", "Havøysund", "Måsøy"));
        postTownRegister.addNewPostTown(new PostTown("9515", "Alta", "Alta"));

        return postTownRegister;
    }

    private void updateObservableList(){
        this.observableList.setAll(this.postTownRegister.getTowns());
    }

    /**
     * Search through the observable list.
     * Uses a lowerCaseFilter so that it doesnt matter if the input is Upper or Lower case.
     *
     * @param actionEvent the action event
     */
    @FXML public void searchThroughList(KeyEvent actionEvent){
        FilteredList<PostTown> filteredData = new FilteredList<>(observableList, p -> true);
        searchField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredData.setPredicate(postTown -> {
                if(newValue == null || newValue.isEmpty()){
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return (postTown.getPostalCode().toLowerCase().contains(lowerCaseFilter)) ||
                    (postTown.getCity().toLowerCase().contains(lowerCaseFilter));

            });
        });
        SortedList<PostTown> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(viewDetails.comparatorProperty());
        viewDetails.setItems(sortedData);
    }

    /**
     * Import file, opens window and you can choose between text or csv files.
     * update the observable list after the file is chosen.
     * @throws IOException  the io exception
     * @throws CsvException the csv exception
     */
    @FXML public void importFile() throws IOException, CsvException {
        String path;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(" CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile != null) {
            path = selectedFile.getAbsolutePath();
        } else {
            path = null;
        }
        System.out.println(path);
        postTownRegister.readFile(path);
        this.updateObservableList();
    }

    /**
     * Show the about Dialog. Opens a new window with the added information.
     */
    @FXML public void ShowAboutDialog() {

        Alert alert = new DialogFactory().createAlert(Alert.AlertType.CONFIRMATION,
                "Information Dialog - About",
                "Post Town Register",
                "A Fantastic Application by\n"
                        + "(C) Mathias Jørgensen\n"
                        + "v0.1 2021-05-12");

        alert.showAndWait();
    }

    /**
     * Close app the application. Pops up an alert to confirm if you want to exit the application.
     *
     * @param event the event
     */
    @FXML public void closeApp(ActionEvent event){
        Alert alert = new DialogFactory().createAlert(Alert.AlertType.CONFIRMATION,
                "Confirmation",
                "Exit the Application",
                "Are your sure you want to Exit the Application?");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent()){
            if(result.get() == ButtonType.OK){
                Platform.exit();
            }
        }

    }





}