package com.anmvg.cerenia;

import com.anmvg.cerenia.models.Reservation;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CartController {

    @FXML
    private ScrollPane mainPane;

    @FXML
    private Button backButton;

    @FXML
    private Button validateButton;


    // Cart initialization
    public void initialize() {

        // BACK BUTTON
        backButton.setGraphic(new FontIcon("fa-arrow-circle-left"));
        backButton.setOnAction(event -> {
            navigateTo("dashboard-view.fxml");
        });

        // DISPLAY THE LIST OF TRIPS
        User currentUser = AuthService.getInstance().getUser();
        List<Reservation> reservationList = DataService.getInstance().findReservationList(new Integer[]{0}, currentUser, null);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // VALIDATE BUTTON
        validateButton.setGraphic(new FontIcon("fas-euro-sign:8:WHITE"));
        validateButton.setOnAction(event -> {
            for (Reservation resa : reservationList) {
                resa.setState(1);
            }
            DataService.getInstance().saveReservationList();
            this.initialize();
        });

        // ROOT OF THE LIST
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.prefWidthProperty().bind(mainPane.widthProperty().subtract(10));
        root.setHgap(20);
        root.setVgap(40);

        // TRIP ITERATION
        for (int i = 0; i < reservationList.size(); i++) {
            Reservation reservation = reservationList.get(i);
            Trip trip = reservation.getTrip();

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
            Button infoButton = new Button("En savoir +");
            infoButton.getStyleClass().setAll("btn", "btn-success");
            root.add(infoButton, 5, i, 1, 1);

            infoButton.setOnAction(event -> {
                ParameterService.getInstance().setIdParam(trip.getId());
                ParameterService.getInstance().setLastVisited("cart-view.fxml");
                navigateTo("trip-infos-view.fxml");
            });

            // INFO BUTTON
            Button deleteButton = new Button("Supprimer");
            deleteButton.getStyleClass().setAll("btn", "btn-danger");
            root.add(deleteButton, 6, i, 1, 1);

            deleteButton.setOnAction(event -> {
                DataService.getInstance().getReservationList().remove(reservation);
                DataService.getInstance().saveReservationList();
                this.initialize();
            });
        }

        mainPane.setContent(root);
        mainPane.setPannable(true);
        mainPane.getStyleClass().add("panel-primary");

    }


    // Utility function for navigating between pages
    private void navigateTo(String source) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(source));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1280, 720);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            stage.setScene(scene);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

}
