package com.example.wrappedify;

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

        return mode;
    }
}
