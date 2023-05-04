package com.anmvg.cerenia;

import com.anmvg.cerenia.models.Trip;
import com.anmvg.cerenia.models.User;
import com.anmvg.cerenia.services.AuthService;
import com.anmvg.cerenia.services.DataService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class DashboardController {

    @FXML
    private ScrollPane mainPane;

    @FXML
    private Button refreshButton;

    @FXML
    private TextField destinationInput;

    @FXML
    private DatePicker startDateInput;

    @FXML
    private DatePicker endDateInput;

    @FXML
    private Spinner<Integer> peopleSpinner;

    @FXML
    private Button cancelSearchButton;

    @FXML
    private SplitMenuButton loginButton;


    // Dashboard initialization
    public void initialize() throws FileNotFoundException {

        // REFRESH BUTTON
        refreshButton.setGraphic(new FontIcon("fa-refresh:8:WHITE"));
        refreshButton.setOnAction(event -> {
            try {
                DataService.getInstance().fetchAll();
                this.resetSearchAndDisplay();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        // DESTINATION SEARCH INPUT
        destinationInput.setOnKeyReleased(event -> {
            if (destinationInput.getText().length() >= 2) {
                try {
                    this.searchTrips();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (destinationInput.getText().length() == 0) {
                DataService dataService = DataService.getInstance();
                try {
                    this.displayTrips(dataService.getTripList());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // START DATE SEARCH INPUT
        startDateInput.setOnAction(event -> {
            try {
                this.searchTrips();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        // END DATE SEARCH INPUT
        endDateInput.setOnAction(event -> {
            try {
                this.searchTrips();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        // PEOPLE NUMBER SEARCH INPUT
        peopleSpinner.valueProperty().addListener(event -> {
            try {
                this.searchTrips();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        // CANCEL SEARCH BUTTON
        cancelSearchButton.setGraphic(new FontIcon("fa-close:8:WHITE"));
        cancelSearchButton.setOnAction(event -> {
            try {
                this.resetSearchAndDisplay();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        // LOGIN BUTTON & ITEMS
        loginButton.setGraphic(new FontIcon("fa-user:8:WHITE"));

        MenuItem loginItem = new MenuItem("Connexion");
        loginItem.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(loader.load(), 1280, 720);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                stage.setScene(scene);
            } catch (IOException io) {
                io.printStackTrace();
            }
        });

        MenuItem logoutItem = new MenuItem("Déconnexion");
        logoutItem.setOnAction(event -> {
            AuthService.getInstance().logout();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(loader.load(), 1280, 720);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                stage.setScene(scene);
            } catch (IOException io) {
                io.printStackTrace();
            }
        });

        MenuItem cartItem = new MenuItem("Mon panier");
        MenuItem historyItem = new MenuItem("Historique");

        MenuItem requestsItem = new MenuItem("Demandes");
        MenuItem planningItem = new MenuItem("Planning");

        User currentUser = AuthService.getInstance().getUser();
        if (currentUser != null) {
            loginButton.setText(currentUser.getFirstName());
            if (!currentUser.isHost()) {
                loginButton.getItems().addAll(cartItem, historyItem);
            } else {
                loginButton.getItems().addAll(requestsItem, planningItem);
            }
            loginButton.getItems().add(logoutItem);
        } else {
            loginButton.getItems().add(loginItem);
        }

        // DISPLAY THE LIST OF TRIPS
        DataService dataService = DataService.getInstance();
        this.displayTrips(dataService.getTripList());

        // SPINNER 1-100 CONFIGURATION
        SpinnerValueFactory<Integer> peopleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        peopleSpinner.setValueFactory(peopleValueFactory);
    }


    // Display a provided list of trips
    public void displayTrips(List<Trip> tripList) throws FileNotFoundException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // ROOT OF THE LIST
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.prefWidthProperty().bind(mainPane.widthProperty().subtract(10));
        root.setHgap(20);
        root.setVgap(40);

        // TRIP ITERATION
        for (int i = 0; i < tripList.size(); i++) {
            Trip trip = tripList.get(i);

            // TRIP IMAGE
            InputStream stream = new FileInputStream("./data/images/" + trip.getId() + ".jpg");
            Image image = new Image(stream);
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            //Setting the image view parameters
            imageView.setX(10);
            imageView.setY(10);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            root.add(imageView, 0, i, 1, 1);

            // TRIP NAME & LOCATION
            VBox tripGeneralInfo = new VBox();
            Label tripName = new Label(trip.getName());
            tripName.getStyleClass().add("h3");
            Label tripLocationText = new Label(" " + trip.getCity() + ", " + trip.getCountry());
            tripLocationText.getStyleClass().add("h5");
            HBox tripLocation = new HBox(new FontIcon("fas-map-marker-alt"), tripLocationText);
            tripGeneralInfo.getChildren().addAll(tripName, tripLocation);
            root.add(tripGeneralInfo, 1, i, 1, 1);
            GridPane.setHgrow(tripGeneralInfo, Priority.ALWAYS);

            // TRIP START & END
            Label tripPeriod = new Label(
                    dateFormat.format(trip.getStartDate()) +
                            " - " +
                            dateFormat.format(trip.getEndDate())
            );
            root.add(tripPeriod, 2, i, 1, 1);

            // TRIP MAX PEOPLE
            Label tripMaxPeople = new Label(trip.getMaxPeople() + " Personne(s)");
            root.add(tripMaxPeople, 3, i, 1, 1);


            // TRIP PRICE
            VBox tripPrice = new VBox();
            tripPrice.setAlignment(Pos.CENTER_RIGHT);
            Label pricePrefix = new Label("à partir de");
            Label price = new Label(trip.getPrice() + " €");
            price.getStyleClass().setAll("h4", "text-info");
            Label priceSuffix = new Label("par personne");
            tripPrice.getChildren().addAll(pricePrefix, price, priceSuffix);
            root.add(tripPrice, 4, i, 1, 1);

            // INFO BUTTON
            Button infoButton = new Button("En savoir +");
            infoButton.getStyleClass().setAll("btn", "btn-success");
            root.add(infoButton, 5, i, 1, 1);
        }

        mainPane.setContent(root);
        mainPane.setPannable(true);
        mainPane.getStyleClass().add("panel-primary");
    }


    // Search for trips and display them
    private void searchTrips() throws FileNotFoundException {
        Date start = null;
        if (startDateInput.getValue() != null) {
            start = java.util.Date.from(startDateInput.getValue()
                    .atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
        }

        Date end = null;
        if (endDateInput.getValue() != null) {
            end = java.util.Date.from(endDateInput.getValue()
                    .atTime(23, 59, 59)
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
        }

        DataService dataService = DataService.getInstance();
        this.displayTrips(dataService.searchTripList(
                destinationInput.getText(),
                start,
                end,
                peopleSpinner.getValue()
        ));
    }


    // Reset the search parammeter and display all trips
    private void resetSearchAndDisplay() throws FileNotFoundException {
        destinationInput.clear();
        startDateInput.setValue(null);
        endDateInput.setValue(null);
        peopleSpinner.getValueFactory().setValue(1);
        destinationInput.clear();
        DataService dataService = DataService.getInstance();
        this.displayTrips(dataService.getTripList());
    }

}
