package com.anmvg.cerenia.services;

import com.anmvg.cerenia.models.Comment;
import com.anmvg.cerenia.models.Reservation;
import com.anmvg.cerenia.models.Trip;
import com.anmvg.cerenia.models.User;
import org.json.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.IOUtils;


public class DataService {

    // the singleton instance
    private static DataService instance;

    // fetching all the users from the local json
    private static List<User> userList;
    private static List<Trip> tripList;
    private static List<Reservation> reservationList;

    // constants for finding the data files
    private final String DATA_PATH = "./data/";


    // the service constructor, fetching the initial data
    private DataService() {
        try {
            fetchAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // the service instance getter, as it is a singleton
    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }


    // method to get JSON data from file
    private JSONArray getJSONData(String path) {
        File file = new File(path);
        JSONArray json = null;

        try {
            if (file.exists()){
                InputStream is = new FileInputStream(path);
                String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
                json = new JSONArray(jsonTxt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    // fetching all the data
    public void fetchAll() {
        fetchUsers();
        fetchTrips();
        fetchReservations();
    }

    // fetching all the users from the local json
    private void fetchUsers() {
        String USERS_FILE = "users.json";
        JSONArray usersJSON = getJSONData(DATA_PATH + USERS_FILE);
        userList = new ArrayList<>();

        // putting the users in the list
        for (int i = 0; i < usersJSON.length(); i++) {
            userList.add(
                new User(
                    usersJSON.getJSONObject(i).getInt("id"),
                    usersJSON.getJSONObject(i).getString("firstname"),
                    usersJSON.getJSONObject(i).getString("lastname"),
                    usersJSON.getJSONObject(i).getString("email"),
                    usersJSON.getJSONObject(i).getString("password"),
                    usersJSON.getJSONObject(i).getBoolean("host")
                )
            );
        }
    }

    // fetching all the trips from the local json
    private void fetchTrips() {
        String TRIPS_FILE = "trips.json";
        JSONArray tripsJSON = getJSONData(DATA_PATH + TRIPS_FILE);
        tripList = new ArrayList<>();

        for (int i = 0; i < tripsJSON.length(); i++) {

            // getting the host
            User host = getUser(tripsJSON.getJSONObject(i).getInt("hostId"));

            // getting the dates
            Date startDate = new Date();
            Date endDate = new Date();
            try {
                String startDateString = tripsJSON.getJSONObject(i).getString("startDate");
                String endDateString = tripsJSON.getJSONObject(i).getString("endDate");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startDate = sdf.parse(startDateString);
                endDate = sdf.parse(endDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // getting the comments
            ArrayList<Comment> comments = new ArrayList<>();
            JSONArray commentsArray = tripsJSON.getJSONObject(i).getJSONArray("comments");
            for (int j = 0; j < commentsArray.length(); j++) {

                // getting the user
                User user = getUser(commentsArray.getJSONObject(j).getInt("userId"));

                // getting the creation date
                Date createdAt = new Date();

                try {
                    String createdAtString = commentsArray.getJSONObject(j).getString("createdAt");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    createdAt = sdf.parse(createdAtString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                comments.add(
                    new Comment(
                        user,
                        commentsArray.getJSONObject(j).getInt("rating"),
                        commentsArray.getJSONObject(j).getString("text"),
                        createdAt
                    )
                );
            }

            // putting the trips in the list
            tripList.add(
                new Trip(
                    tripsJSON.getJSONObject(i).getInt("id"),
                    tripsJSON.getJSONObject(i).getString("name"),
                    host,
                    tripsJSON.getJSONObject(i).getString("city"),
                    tripsJSON.getJSONObject(i).getString("country"),
                    startDate,
                    endDate,
                    tripsJSON.getJSONObject(i).getFloat("price"),
                    tripsJSON.getJSONObject(i).getInt("maxPeople"),
                    tripsJSON.getJSONObject(i).getString("description"),
                    comments
                )
            );
        }
    }

    // fetching all the reservations from the local json
    private void fetchReservations() {
        String RESERVATIONS_FILE = "reservations.json";
        JSONArray reservationsJSON = getJSONData(DATA_PATH + RESERVATIONS_FILE);
        reservationList = new ArrayList<>();

        for (int i = 0; i < reservationsJSON.length(); i++) {

            // getting the creation date
            Date createdAt = new Date();
            try {
                String createdAtString = reservationsJSON.getJSONObject(i).getString("createdAt");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                createdAt = sdf.parse(createdAtString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // getting the user
            User user = getUser(reservationsJSON.getJSONObject(i).getInt("userId"));

            // getting the trip
            Trip trip = getTrip(reservationsJSON.getJSONObject(i).getInt("tripId"));

            // putting the trips in the list
            reservationList.add(
                new Reservation(
                    reservationsJSON.getJSONObject(i).getInt("id"),
                    user,
                    trip,
                    reservationsJSON.getJSONObject(i).getInt("state"),
                    reservationsJSON.getJSONObject(i).getInt("nbPeople"),
                    createdAt
                )
            );
        }
    }


    // userList getter
    public List<User> getUserList() {
        return userList;
    }

    // tripList getter
    public List<Trip> getTripList() {
        return tripList;
    }

    // tripList search function
    public List<Trip> searchTripList(String destination, Date start, Date end, int nbPeople) {
        List<Trip> resultList = new ArrayList<>();

        for (Trip trip : tripList) {
            boolean match = true;

            if (!destination.isEmpty()) {
                for (String word : destination.split(" ")) {
                    if (word.length() > 1) {
                        match = (
                                (trip.getName().toLowerCase().contains(word.toLowerCase())) ||
                                (trip.getCountry().toLowerCase().contains(word.toLowerCase())) ||
                                (trip.getCity().toLowerCase().contains(word.toLowerCase())) ||
                                (trip.getDescription().toLowerCase().contains(word.toLowerCase()))
                        );
                        if (match) {
                            break;
                        }
                    }
                }
            }

            if (start != null && match) {
                match = (start.before(trip.getStartDate()) || start.equals(trip.getStartDate()));
            }

            if (end != null && match) {
                System.out.println(end);
                System.out.println(trip.getEndDate());
                match = (end.after(trip.getEndDate())) || end.equals(trip.getEndDate());
            }

            if (nbPeople > 1 && match) {
                match = (trip.getMaxPeople() >= nbPeople);
            }

            if (match) {
                resultList.add(trip);
            }
        }

        return resultList;
    }

    // reservationList getter
    public List<Reservation> getReservationList() {
        return reservationList;
    }

    // reservationList search function
    public List<Reservation> findReservationList(Integer[] states, User customer, User host) {
        List<Reservation> resultList = new ArrayList<>();

        for (Reservation reservation : reservationList) {
            boolean match = true;

            if (states.length > 0) {
                for (Integer s : states) {
                    match = (Objects.equals(reservation.getState(), s));
                    if (match) {
                        break;
                    }
                }
            }

            if (customer != null && match) {
                match = (Objects.equals(reservation.getUser().getId(), customer.getId()));
            }

            if (host != null && match) {
                match = (Objects.equals(reservation.getTrip().getHost().getId(), host.getId()));
            }

            if (match) {
                resultList.add(reservation);
            }
        }

        return resultList;
    }

    // getting a user by id
    public User getUser(int id) {
        for (User user : userList) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    // getting a trip by id
    public Trip getTrip(int id) {
        for (Trip trip : tripList) {
            if (trip.getId() == id) {
                return trip;
            }
        }
        return null;
    }

    // getting a reservation by id
    public Reservation getReservation(int id) {
        for (Reservation reservation : reservationList) {
            if (reservation.getId() == id) {
                return reservation;
            }
        }
        return null;
    }

}
