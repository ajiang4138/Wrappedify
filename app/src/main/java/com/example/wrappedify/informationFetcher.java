package com.example.wrappedify;

import com.example.wrappedify.firebaseLogin.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class informationFetcher {

    public static ArrayList<String> top3Genre(ArrayList<String> genres) {
        HashMap<String, Integer> frequencyMap = new HashMap<>();
        for (String str : genres) {
            frequencyMap.put(str, frequencyMap.getOrDefault(str, 0) + 1);
        }

        int j = 0;
        ArrayList<String> mode = new ArrayList<>();

        while (j < 3) {
            String mostOccurring = null;
            int maxFrequency = 0;

            for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
                if (entry.getValue() > maxFrequency) {
                    mostOccurring = entry.getKey();
                    maxFrequency = entry.getValue();
                }
            }

            mode.add(mostOccurring);
            frequencyMap.remove(mostOccurring);
            j++;
        }
        User.setGenres(mode);

        return mode;
    }

    public static String genresText(ArrayList<String> mode) {
        String output = "Top Genres: ";

        for (int i = 0; i < mode.size(); i++) {
            output += mode.get(i) + ", ";
        }

        return output.substring(0, output.length() - 2);
    }

    public static String namesText(ArrayList<String> names) {
        String output = "Top Artists: \n";

        for (int i = 0; i < names.size(); i++) {
            output += names.get(i) + "\n";
        }

        return output.substring(0, output.length() - 1);
    }

    public static String artistText(ArrayList<String> artistNames) {
        String output = "";

        for (int i = 0; i < artistNames.size(); i++) {
            output += artistNames.get(i) + ", ";
        }

        return output.substring(0, output.length() - 2);
    }

    public static String artistIdUrl(ArrayList<String> artistId) {
        String output = "";

        for (int i = 0; i < artistId.size() - 2; i++) {
            output += artistId.get(i) + "%2C";
        }

        return output.substring(0, output.length() - 3);
    }

    public static String trackIdUrl(ArrayList<String> trackId) {
        String output = "";

        for (int i = 0; i < trackId.size(); i++) {
            output += trackId.get(i) + "%2C";
        }

        return output.substring(0, output.length() - 3);
    }

    public static String genreUrl(ArrayList<String> mode) {
        String output = "";

        for (int i = 0; i < mode.size() - 2; i++) {
            output += mode.get(i).replace(" ", "+") + "%2C";
        }

        return output.substring(0, output.length() - 3);
    }
}
