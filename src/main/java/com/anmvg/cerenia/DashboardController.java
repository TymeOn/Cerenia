package com.anmvg.cerenia;

import com.anmvg.cerenia.models.Comment;
import com.anmvg.cerenia.models.Trip;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DashboardController {

    @FXML
    private ScrollPane mainPane;

    public void initialize(){
        DataService dataService = DataService.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Region spacer = new Region();
        spacer.setMinWidth(Region.USE_PREF_SIZE);

        // ROOT OF THE LIST
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        for (Trip trip : dataService.getTripList()) {
            // TRIP LINE
            HBox line = new HBox();
            line.setSpacing(10);
            line.setPadding(new Insets(10));
            line.setAlignment(Pos.CENTER_LEFT);
            line.setHgrow(spacer, Priority.ALWAYS);

            // TRIP NAME & LOCATION
            VBox tripGeneralInfo = new VBox();

            Label tripName = new Label(trip.getName());
            tripName.getStyleClass().add("h3");

            Label tripLocationText = new Label(" " + trip.getCity() + ", " + trip.getCountry());
            tripLocationText.getStyleClass().add("h5");
            HBox tripLocation = new HBox(new FontIcon("fas-map-marker-alt"), tripLocationText);

            tripGeneralInfo.getChildren().addAll(tripName, tripLocation);
            line.getChildren().add(tripGeneralInfo);

            // TRIP START & END
            Label tripPeriod = new Label(
                dateFormat.format(trip.getStartDate()) +
                " - " +
                dateFormat.format(trip.getEndDate())
            );
            line.getChildren().add(tripPeriod);

            // TRIP MAX PEOPLE
            Label tripMaxPeople = new Label(trip.getMaxPeople() + " Personne(s)");
            line.getChildren().add(tripMaxPeople);

            line.getChildren().add(spacer);

            // TRIP PRICE
            VBox tripPrice = new VBox();
            tripPrice.setAlignment(Pos.CENTER_RIGHT);
            Label pricePrefix = new Label("à partir de");
            Label price = new Label(trip.getPrice() + " €");
            price.getStyleClass().setAll("h4", "text-info");
            Label priceSuffix = new Label("par personne");
            tripPrice.getChildren().addAll(pricePrefix, price, priceSuffix);
            line.getChildren().add(tripPrice);

            // INFO BUTTON
            Button infoButton = new Button("En savoir +");
            infoButton.getStyleClass().setAll("btn", "btn-success");
            line.getChildren().add(infoButton);

            // ADDING THE LINE
            root.getChildren().add(line);
        }

        mainPane.setContent(root);
        mainPane.setPannable(true);
        mainPane.getStyleClass().add("panel-primary");
    }

}