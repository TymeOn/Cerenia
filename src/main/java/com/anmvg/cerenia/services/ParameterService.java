package com.anmvg.cerenia.services;


public class ParameterService {

    // the singleton instance
    private static ParameterService instance;

    private static int idParam;

    // the service constructor, fetching the initial data
    private ParameterService() {}

    // the service instance getter, as it is a singleton
    public static ParameterService getInstance() {
        if (instance == null) {
            instance = new ParameterService();
        }
        return instance;
    }

    // getter and setter for the id param static value
    public int getIdParam() { return idParam; }
    public void setIdParam(int idParam) { ParameterService.idParam = idParam;}
}
