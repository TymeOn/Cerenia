package com.anmvg.cerenia;

import com.anmvg.cerenia.models.Comment;
import com.anmvg.cerenia.models.Reservation;
import com.anmvg.cerenia.models.Trip;
import com.anmvg.cerenia.models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
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
    }

    public static void main(String[] args) {
        launch();
    }
}