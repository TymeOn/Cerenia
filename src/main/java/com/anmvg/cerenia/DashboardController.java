package com.anmvg.cerenia;

import com.anmvg.cerenia.models.Comment;
import com.anmvg.cerenia.models.Trip;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DashboardController {

    @FXML
    private ScrollPane mainPane;

    @FXML
    private Spinner<Integer> peopleSpinner;

    public void initialize() throws FileNotFoundException {
        DataService dataService = DataService.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // ROOT OF THE LIST
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.prefWidthProperty().bind(mainPane.widthProperty().subtract(10));
        root.setHgap(20);
        root.setVgap(40);

        // TRIP ITERATION
        List<Trip> tripList = dataService.getTripList();
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

//            // HORIZONTAL SEPARATORS
//            root.add(new Separator(Orientation.HORIZONTAL), 0, i+1, 1, 1);
//            root.add(new Separator(Orientation.HORIZONTAL), 1, i+1, 1, 1);
//            root.add(new Separator(Orientation.HORIZONTAL), 2, i+1, 1, 1);
//            root.add(new Separator(Orientation.HORIZONTAL), 3, i+1, 1, 1);
//            root.add(new Separator(Orientation.HORIZONTAL), 4, i+1, 1, 1);
        }

        mainPane.setContent(root);
        mainPane.setPannable(true);
        mainPane.getStyleClass().add("panel-primary");

        // Configure the Spinner with values of 1 - 100
        SpinnerValueFactory<Integer> peopleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,100, 1);
        peopleSpinner.setValueFactory(peopleValueFactory);
    }

}