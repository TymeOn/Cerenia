package com.anmvg.cerenia;

import com.anmvg.cerenia.models.Comment;
import com.anmvg.cerenia.models.Reservation;
import com.anmvg.cerenia.models.Trip;
import com.anmvg.cerenia.models.User;
import com.anmvg.cerenia.services.DataService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("dashboard-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setTitle("Cerenia");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        DataService dataService = DataService.getInstance();

        // TODO : Defining reservation state enum

        // Displaying the data (for testing purposes)
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        System.out.println("Utilisateurs :");
        for (User user : dataService.getUserList()) {
            System.out.println(
                user.getId() + " " +
                user.getFirstName() + " " +
                user.getLastName() + " " +
                user.getEmail() + " " +
                user.getPassword() + " " +
                user.isHost()
            );
        }

        System.out.println("\nSejours :");
        for (Trip trip : dataService.getTripList()) {
            StringBuilder comments = new StringBuilder("{");
            for (Comment comment : trip.getComments()) {
                comments.append("[")
                    .append(comment.getUser().getId())
                    .append(" ")
                    .append(comment.getRating())
                    .append(" ")
                    .append(comment.getText())
                    .append(" ")
                    .append(comment.getCreatedAt())
                    .append("]");
            }
            comments.append("}");

            System.out.println(
                trip.getId() + " " +
                trip.getName() + " " +
                "[" + trip.getHost().getId() + "] " +
                trip.getCity() + " " +
                trip.getCountry() + " " +
                dateFormat.format(trip.getStartDate()) + " " +
                dateFormat.format(trip.getEndDate())  + " " +
                trip.getPrice() + " " +
                trip.getMaxPeople() + " " +
                trip.getDescription() + " " +
                comments
            );
        }

        System.out.println("\nReservation :");
        for (Reservation reservation : dataService.getReservationList()) {
            System.out.println(
                reservation.getId() + " " +
                "[" + reservation.getUser().getId() + "] " +
                "[" + reservation.getTrip().getId() + "] " +
                reservation.getState() + " " +
                reservation.getNbPeople() + " " +
                dateFormat.format(reservation.getCreatedAt())
            );
        }

        DataService.getInstance().saveReservationList();
        DataService.getInstance().saveTripList();
    }

    public static void main(String[] args) {
        launch();
    }
}