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
    private List<User> userList;
    private List<Trip> tripList;
    private List<Reservation> reservationList;

    // constants for finding the data files
    private final String DATA_PATH = "./data/";
    private final String USERS_FILE = "users.json";
    private final String TRIPS_FILE = "trips.json";
    private final String RESERVATIONS_FILE = "reservations.json";
    private final Integer NB_THREADS = 4;


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

    // method to save JSON data to a file
    private void saveJSONData(String path, JSONArray jsonData) {
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.write(jsonData.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // fetching all the data
    public void fetchAll() {
        fetchUsers();
        fetchTrips();
        fetchReservations();
    }

    // fetching all the users from the local json
    private void fetchUsers() {
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

    // saving all the trips in memory to a local json
    public void saveTripList() {
        JSONArray finalArray = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Trip trip : tripList) {
            JSONObject obj = new JSONObject();

            obj.put("id", trip.getId());
            obj.put("name", trip.getName());
            obj.put("hostId", trip.getHost().getId());
            obj.put("city", trip.getCity());
            obj.put("country", trip.getCountry());
            obj.put("startDate", sdf.format(trip.getStartDate()));
            obj.put("endDate", sdf.format(trip.getEndDate()));
            obj.put("price", trip.getPrice());
            obj.put("maxPeople", trip.getMaxPeople());
            obj.put("description", trip.getDescription());

            JSONArray commentsArray = new JSONArray();

            for (Comment comment : trip.getComments()) {
                JSONObject commObj = new JSONObject();

                commObj.put("userId", comment.getUser().getId());
                commObj.put("rating", comment.getRating());
                commObj.put("text", comment.getText());
                commObj.put("createdAt", sdf.format(comment.getCreatedAt()));

                commentsArray.put(commObj);
            }

            obj.put("comments", commentsArray);

            finalArray.put(obj);
        }

        saveJSONData(DATA_PATH + TRIPS_FILE, finalArray);
    }

    // fetching all the reservations from the local json
    private void fetchReservations() {
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

    // saving all the reservations in memory to a local json
    public void saveReservationList() {
        JSONArray finalArray = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Reservation resa : reservationList) {
            JSONObject obj = new JSONObject();

            obj.put("id", resa.getId());
            obj.put("tripId", resa.getTrip().getId());
            obj.put("userId", resa.getUser().getId());
            obj.put("state", resa.getState());
            obj.put("nbPeople", resa.getNbPeople());
            obj.put("createdAt", sdf.format(resa.getCreatedAt()));

            finalArray.put(obj);
        }

        saveJSONData(DATA_PATH + RESERVATIONS_FILE, finalArray);
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
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= NB_THREADS; i++) {
            int finalI = i;
            Thread t = new Thread(() -> {

                int startIndex;
                int stopIndex;

                if (finalI == 1) {
                    startIndex = 0;
                    stopIndex = (int) Math.floor(tripList.size() / (float) NB_THREADS);
                } else if (finalI == NB_THREADS) {
                    startIndex = (int) Math.floor(tripList.size() / (float) NB_THREADS) * (finalI - 1);
                    stopIndex = tripList.size();
                } else {
                    startIndex = (int) Math.floor(tripList.size() / (float) NB_THREADS) * (finalI - 1);
                    stopIndex = (int) Math.floor(tripList.size() / (float) NB_THREADS) * finalI;
                }

                for (int j = startIndex; j < stopIndex; j++) {
                    Trip trip = tripList.get(j);
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
                        match = (end.after(trip.getEndDate())) || end.equals(trip.getEndDate());
                    }

                    if (nbPeople > 1 && match) {
                        match = (trip.getMaxPeople() >= nbPeople);
                    }

                    if (match) {
                        resultList.add(trip);
                    }
                }
            });
            t.start();
            threads.add(t);
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return resultList;
    }

    // getting all the trips from a specific host
    public List<Trip> getHostTripList(User host) {
        List<Trip> resultList = new ArrayList<>();

        for (Trip trip : tripList) {
            if (Objects.equals(trip.getHost().getId(), host.getId())) {
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

    // getting the next id for a reservation
    public int getNextReservationId() {
        Integer newId = 0;

        for (Reservation reservation : reservationList) {
            if (reservation.getId() > newId) {
                newId = reservation.getId();
            }
        }

        return newId++;
    }

}
