package com.anmvg.cerenia;

import com.anmvg.cerenia.models.Comment;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
import java.util.Date;
import java.util.List;


public class TripInfosController {

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

    @FXML
    private Button commentButton;

    User user;
    private boolean addedToCart = false;

    public void initialize() throws FileNotFoundException {
        backButton.setGraphic(new FontIcon("fa-arrow-circle-left"));
        backButton.setOnAction(event -> {
            navigateTo(ParameterService.getInstance().getLastVisited());
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
        reserveButton.setOnAction(event -> {
            addedToCart = true;
            DataService.getInstance().getReservationList().add(new Reservation(
                    DataService.getInstance().getNextReservationId(),
                    currentUser,
                    trip,
                    0,
                    numberReservation.getValue(),
                    new Date()
            ));
            DataService.getInstance().saveReservationList();

            reserveButton.setGraphic(new FontIcon("fas-check:8:WHITE"));
            reserveButton.setText(" Ajouté");
        });


        // Configure the Spinner with values of 1 - 100
        SpinnerValueFactory<Integer> peopleValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, trip.getMaxPeople(), 1);
        numberReservation.setValueFactory(peopleValueFactory);

        List<Comment> tripComments = trip.getComments();


        DateFormat dateFormatC = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");

        // ROOT OF THE LIST
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.prefWidthProperty().bind(commentPage.widthProperty().subtract(10));
        root.setHgap(20);
        root.setVgap(40);

        // COMMENT ITERATION
        for (int i = 0; i < tripComments.size(); i++) {

            Comment comment = tripComments.get(i);

            // COMMENT User NAME
            VBox CommentGeneralInfo = new VBox();
            Label username = new Label(" " + comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
            username.getStyleClass().add("h3");

            Label note = new Label(" " + comment.getRating() + " / 5 ");
            note.setPadding(new Insets(0, 0, 0, 15));
            note.setFont(Font.font("System", 15));

            FontIcon star = new FontIcon("fa-star");
            star.setIconSize(15);
            star.setIconColor(Color.web("#d8d800"));
            HBox starBox = new HBox(1,note , star);
            starBox.setAlignment(Pos.CENTER_LEFT);

            HBox profile = new HBox(0, new FontIcon("fa-user-circle:30:BLUE"), username, starBox);
            profile.setAlignment(Pos.CENTER_LEFT);
            CommentGeneralInfo.getChildren().addAll(profile);
            root.add(CommentGeneralInfo, 0, i, 1, 1);
            GridPane.setHgrow(CommentGeneralInfo, Priority.ALWAYS);

            VBox commentaryDesc = new VBox();
            Pane canvas = new Pane();
            canvas.setPrefSize(10,10);
            commentaryDesc.setAlignment(Pos.CENTER_LEFT);
            Text commentDescription = new Text(" " + comment.getText());
            commentaryDesc.getChildren().addAll(canvas, commentDescription);
            CommentGeneralInfo.getChildren().addAll(commentaryDesc);

            VBox commentarDate = new VBox();
            Pane grid = new Pane();
            grid.setPrefSize(15,15);
            Label commentDate = new Label("Posté le " + dateFormatC.format(comment.getCreatedAt()));
            commentarDate.getChildren().addAll(grid, commentDate);
            CommentGeneralInfo.getChildren().addAll(commentarDate);





        }
        commentPage.setContent(root);
        commentPage.setPannable(true);
        commentPage.getStyleClass().add("panel-primary");

        // Horizontal scroll bar is only displayed when needed
        commentPage.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

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
