package com.anmvg.cerenia;

import com.anmvg.cerenia.models.Trip;
import com.anmvg.cerenia.models.User;
import com.anmvg.cerenia.services.AuthService;
import com.anmvg.cerenia.services.DataService;
import com.anmvg.cerenia.services.ParameterService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.kordamp.ikonli.javafx.FontIcon;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class TripDetailsController {

    @FXML
    private Button backButton;

    @FXML
    private ImageView tripImage;

    @FXML
    private Text tripName;

    @FXML
    private Text tripLocationText;

    @FXML
    private Text pricePrefix;

    @FXML
    private Text priceSuffix;
    @FXML
    private Text tripDescription;

    @FXML
    private Text tripStartDate;

    @FXML
    private Text tripEndDate;

    @FXML
    private Text tripRemainPeople;

    @FXML
    private Text tripPrice;

    @FXML
    private Button reserveButton;

    @FXML
    private ScrollPane commentPage;

    @FXML
    private HBox tripLocation;

    @FXML
    private Spinner<Integer> numberReservation;

    private static int id;

    User user;

    public void initialize() throws FileNotFoundException {
        backButton.setGraphic(new FontIcon("fa-arrow-circle-left"));
        backButton.setOnAction(event -> {
            navigateToDashboard();
        });

        User currentUser = AuthService.getInstance().getUser();

        // CURRENT TRIP
        Trip trip = DataService.getInstance().getTrip(ParameterService.getInstance().getIdParam());

        // TRIP NAME & LOCATION
        tripName.setText(trip.getName());
        tripName.getStyleClass().add("h3");

        tripLocation.getChildren().add(0, new FontIcon("fas-map-marker-alt"));
        tripLocationText.setText(" " + trip.getCity() + ", " + trip.getCountry());
        tripLocationText.getStyleClass().add("h5");

        // TRIP IMAGE
        InputStream stream = new FileInputStream("./data/images/" + trip.getId() + ".jpg");
        Image image = new Image(stream);
        tripImage.setImage(image);

        tripDescription.setText(trip.getDescription());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        tripStartDate.setText(dateFormat.format(trip.getStartDate()));

        tripEndDate.setText(dateFormat.format(trip.getEndDate()));

        tripRemainPeople.setText(trip.getMaxPeople() + " personne(s) maximum");

        pricePrefix.setText("à partir de");
        tripPrice.setText(trip.getPrice() + " €");
        tripPrice.getStyleClass().setAll("h4", "text-info");
        priceSuffix.setText("par personne");

        reserveButton.setGraphic(new FontIcon("fa-shopping-cart:8:WHITE"));
        reserveButton.getStyleClass().setAll("btn", "btn-warning");


        // Configure the Spinner with values of 1 - 100
        SpinnerValueFactory<Integer> peopleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, trip.getMaxPeople(), 1);
        numberReservation.setValueFactory(peopleValueFactory);
    }

    public void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1280, 720);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            stage.setScene(scene);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void initData (Integer id) {
        TripDetailsController.id = id;
    }
}
