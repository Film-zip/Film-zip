package com.crazy.filmzip.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResponseService {

    private static final OkHttpClient client = new OkHttpClient();

    public static List responseHandler(Request request) {

        List<Map<String, Object>> movieList = new ArrayList<>();
        String imgBaseUrl = "https://image.tmdb.org/t/p/original";

        try (Response response = client.newCall(request).execute()) {
            String jsonResponse = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);
            JsonNode results = rootNode.get("results");

            for (JsonNode movie : results) {
                // Extract specific fields to send to the HTML page
                String title = movie.get("title").asText();
                String release_date = movie.get("release_date").asText();
                String backdrop_path = imgBaseUrl + movie.get("backdrop_path").asText();
                String poster_path = imgBaseUrl + movie.get("poster_path").asText();
                Double RawRating = movie.get("vote_average").asDouble();
                String rating = String.format("%.1f", RawRating);

                if (release_date.isEmpty()) {
                    release_date = "NO DATA";
                }

                if (movie.get("poster_path").isNull()) {
                    poster_path = "/images/NotFound.jpg";
                }

                // Add each movie as a map (key-value pairs) to the list
                movieList.add(Map.of(
                        "title", title,
                        "release_date", release_date,
                        "backdrop_path", backdrop_path,
                        "poster_path", poster_path,
                        "rating", rating
                ));
            }

        } catch (
                IOException e) {
            System.out.println(e.getMessage());
        }
        return movieList;
    }
}