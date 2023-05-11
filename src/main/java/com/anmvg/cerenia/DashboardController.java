package com.anmvg.cerenia;

import com.anmvg.cerenia.models.Trip;
import com.anmvg.cerenia.models.User;
import com.anmvg.cerenia.services.AuthService;
import com.anmvg.cerenia.services.DataService;
import com.anmvg.cerenia.services.ParameterService;
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
import java.util.ArrayList;
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

    @FXML
    private Button previousPageButton;

    @FXML
    private Label currentPageLabel;

    @FXML
    private Label totalPageLabel;

    @FXML
    private Button nextPageButton;

    @FXML
    private TextField commentText;

    private User currentUser;

    private int pageNumber = 0;
    private int totalPageNumber = 0;
    private final int ITEMS_PER_PAGE = 10;

    private List<Trip> lastTripList = new ArrayList<>();


    // Dashboard initialization
    public void initialize() {

        // REFRESH BUTTON
        refreshButton.setGraphic(new FontIcon("fa-refresh:8:WHITE"));
        refreshButton.setOnAction(event -> {
            DataService.getInstance().fetchAll();
            this.resetSearchAndDisplay();
        });

        // DESTINATION SEARCH INPUT
        destinationInput.setOnKeyReleased(event -> {
            if (destinationInput.getText().length() >= 2) {
                this.searchTrips();
            } else if (destinationInput.getText().length() == 0) {
                List<Trip> trips = DataService.getInstance().getTripList();
                displayTotalPageNumber(trips.size());
                this.displayTrips(trips);
            }
        });

        // START DATE SEARCH INPUT
        startDateInput.setOnAction(event -> this.searchTrips());

        // END DATE SEARCH INPUT
        endDateInput.setOnAction(event -> this.searchTrips());

        // PEOPLE NUMBER SEARCH INPUT
        peopleSpinner.valueProperty().addListener(event -> this.searchTrips());

        // CANCEL SEARCH BUTTON
        cancelSearchButton.setGraphic(new FontIcon("fa-close:8:WHITE"));
        cancelSearchButton.setOnAction(event -> this.resetSearchAndDisplay());

        // LOGIN BUTTON & ITEMS
        loginButton.setGraphic(new FontIcon("fa-user:8:WHITE"));

        MenuItem loginItem = new MenuItem("Connexion");
        loginItem.setOnAction(event -> this.navigateTo("login-view.fxml"));

        MenuItem logoutItem = new MenuItem("Déconnexion");
        logoutItem.setOnAction(event -> {
            AuthService.getInstance().logout();
            this.navigateTo("dashboard-view.fxml");
        });

        MenuItem cartItem = new MenuItem("Mon panier");
        cartItem.setOnAction(event -> {
            this.navigateTo("cart-view.fxml");
        });

        MenuItem recapItem = new MenuItem("Récapitulatif");
        recapItem.setOnAction(event -> {
            this.navigateTo("recap-view.fxml");
        });

        MenuItem myTripsItem = new MenuItem("Mes séjours");
        myTripsItem.setOnAction(event -> {
            this.navigateTo("my-trips-view.fxml");
        });

        MenuItem requestsItem = new MenuItem("Demandes");
        requestsItem.setOnAction(event -> {
            this.navigateTo("requests-view.fxml");
        });

        MenuItem planningItem = new MenuItem("Planning");
        planningItem.setOnAction(event -> {
            this.navigateTo("planning-view.fxml");
        });

        currentUser = AuthService.getInstance().getUser();
        if (currentUser != null) {
            loginButton.setText(currentUser.getFirstName());
            if (!currentUser.isHost()) {
                loginButton.getItems().addAll(cartItem, recapItem);
            } else {
                loginButton.getItems().addAll(myTripsItem, requestsItem, planningItem);
            }
            loginButton.getItems().add(logoutItem);
        } else {
            loginButton.getItems().add(loginItem);
        }

        // DISPLAY THE LIST OF TRIPS
        List<Trip> trips = DataService.getInstance().getTripList();
        displayTotalPageNumber(trips.size());
        this.displayTrips(trips);

        // SPINNER 1-100 CONFIGURATION
        SpinnerValueFactory<Integer> peopleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        peopleSpinner.setValueFactory(peopleValueFactory);

        previousPageButton.setGraphic(new FontIcon("fas-chevron-left"));
        previousPageButton.setOnAction(event -> {
            if (pageNumber > 0) {
                pageNumber--;
                currentPageLabel.setText(Integer.toString(pageNumber + 1));
                mainPane.setVvalue(0);
                this.displayTrips(lastTripList);

                if (pageNumber == 0) {
                    previousPageButton.setDisable(true);
                }

                if (pageNumber < (totalPageNumber - 1)) {
                    nextPageButton.setDisable(false);
                }
            }
        });

        if (totalPageNumber > 1) {
            nextPageButton.setDisable(false);
        }

        nextPageButton.setGraphic(new FontIcon("fas-chevron-right"));
        nextPageButton.setOnAction(event -> {
            if (pageNumber > -1) {
                pageNumber++;
                currentPageLabel.setText(Integer.toString(pageNumber + 1));
                mainPane.setVvalue(0);
                this.displayTrips(lastTripList);

                if (pageNumber == totalPageNumber - 1) {
                    nextPageButton.setDisable(true);
                }

                if (pageNumber > 0) {
                    previousPageButton.setDisable(false);
                }
            }
        });

        currentPageLabel.setText(Integer.toString(pageNumber + 1));
    }


    // Display a provided list of trips
    public void displayTrips(List<Trip> globalTripList) {
        lastTripList = globalTripList;

        int startIndex = pageNumber * (ITEMS_PER_PAGE);
        int endIndex = (Math.min((startIndex + ITEMS_PER_PAGE), globalTripList.size()));

        List<Trip> tripList = globalTripList.subList(startIndex, endIndex);

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
            InputStream stream;
            try {
                stream = new FileInputStream("./data/images/" + trip.getId() + ".jpg");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
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
            Button infoButton = new Button();
            infoButton.getStyleClass().setAll("btn", "btn-success");
            if (currentUser == null) {
                infoButton.setText("Connexion");
                infoButton.setOnAction(event -> navigateTo("login-view.fxml"));
            } else {
                infoButton.setText("En savoir +");
                infoButton.setOnAction(event -> {
                    ParameterService.getInstance().setIdParam(trip.getId());
                    ParameterService.getInstance().setLastVisited("dashboard-view.fxml");
                    navigateTo("trip-infos-view.fxml");
                });
            }
            root.add(infoButton, 5, i, 1, 1);


        }

        mainPane.setContent(root);
        mainPane.setPannable(true);
        mainPane.getStyleClass().add("panel-primary");
    }

    // Search for trips and display them
    private void searchTrips() {
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

        List<Trip> trips = DataService.getInstance().searchTripList(
                destinationInput.getText(),
                start,
                end,
                peopleSpinner.getValue()
        );
        displayTotalPageNumber(trips.size());
        this.resetPagination();
        this.displayTrips(trips);
    }


    // Reset the search parameter and display all trips
    private void resetSearchAndDisplay() {
        destinationInput.clear();
        startDateInput.setValue(null);
        endDateInput.setValue(null);
        peopleSpinner.getValueFactory().setValue(1);
        destinationInput.clear();

        List<Trip> trips = DataService.getInstance().getTripList();
        displayTotalPageNumber(trips.size());
        this.resetPagination();
        this.displayTrips(trips);
    }


    // Utility function that computes and displays the total number of pages
    private void displayTotalPageNumber(int nbItems) {
        totalPageNumber = (int) Math.ceil(nbItems / (double) ITEMS_PER_PAGE);
        totalPageLabel.setText(Integer.toString(totalPageNumber));
    }


    // Utility function for properly resetting the navigation
    private void resetPagination() {
        this.pageNumber = 0;
        currentPageLabel.setText(Integer.toString(pageNumber + 1));
        mainPane.setVvalue(0);
        previousPageButton.setDisable(true);
        if (totalPageNumber > 1) {
            nextPageButton.setDisable(false);
        }
    }


    // Utility function for navigating between pages
    private void navigateTo(String source) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(source));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1280, 720);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            stage.setScene(scene);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
