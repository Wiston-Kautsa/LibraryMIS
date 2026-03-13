package com.mycompany.librarymis;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Preferences {

    public static final String CONFIG_FILE = "config.json";

    private int nDaysWithoutFine;
    private float finePerDay;
    private String username;
    private String password;

    public Preferences() {

        nDaysWithoutFine = 14;
        finePerDay = 2;
        username = "admin";
        password = "admin";
    }

    public int getnDaysWithoutFine() {
        return nDaysWithoutFine;
    }

    public void setnDaysWithoutFine(int nDaysWithoutFine) {
        this.nDaysWithoutFine = nDaysWithoutFine;
    }

    public float getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(float finePerDay) {
        this.finePerDay = finePerDay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*
    Create configuration file with default values
    */
    public static void initConfig() {

        Writer writer = null;

        try {

            System.out.println("Creating default configuration file...");

            Preferences preference = new Preferences();
            Gson gson = new Gson();

            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);

            System.out.println("Default configuration created successfully.");

        } catch (IOException ex) {

            System.err.println("Error creating configuration file.");
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);

        } finally {

            try {

                if (writer != null) {
                    writer.close();
                }

            } catch (IOException ex) {

                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
    Load preferences from config file
    */
    public static Preferences getPreferences() {

        Gson gson = new Gson();
        Preferences preferences = new Preferences();

        try {

            preferences = gson.fromJson(
                    new FileReader(CONFIG_FILE),
                    Preferences.class
            );

            System.out.println("Preferences loaded successfully.");

        } catch (FileNotFoundException ex) {

            System.out.println("Configuration file not found. Creating default config...");
            initConfig();

            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }

        return preferences;
    }

    /*
    Save preferences to config file
    */
    public static void writePreferences(Preferences preferences) {

        Writer writer = null;

        try {

            Gson gson = new Gson();

            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preferences, writer);

            System.out.println("Preferences saved successfully.");

        } catch (IOException ex) {

            System.err.println("Error saving preferences.");
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);

        } finally {

            try {

                if (writer != null) {
                    writer.close();
                }

            } catch (IOException ex) {

                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}