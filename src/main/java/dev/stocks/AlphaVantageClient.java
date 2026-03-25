package dev.stocks;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class AlphaVantageClient {
    private static final String BASE = "https://www.alphavantage.co/query";
    private final OkHttpClient http = new OkHttpClient();
    private final String apiKey;

    public AlphaVantageClient(String apiKey) {
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalArgumentException("Set ALPHAVANTAGE_API_KEY env var");
        this.apiKey = apiKey;
    }

    /** Returns a map of date -> close price (most-recent first). */
    public NavigableMap<LocalDate, Double> getDailyAdjustedCloses(String symbol, boolean compact) throws IOException {
        String outputsize = compact ? "compact" : "full";
        String url = BASE + "?function=TIME_SERIES_DAILY&symbol=" + symbol +
                "&outputsize=" + outputsize + "&apikey=" + apiKey;
        Request req = new Request.Builder().url(url).build();
        try (Response res = http.newCall(req).execute()) {
            if (!res.isSuccessful())
                throw new IOException("HTTP " + res.code() + " for " + symbol);
            String body = Objects.requireNonNull(res.body()).string();
            System.out.println(body);
            JsonObject root = JsonParser.parseString(body).getAsJsonObject();
            JsonObject series = root.getAsJsonObject("Time Series (Daily)");
            if (series == null)
                throw new IOException("No time series for " + symbol + " (check symbol or rate-limit)");
            NavigableMap<LocalDate, Double> out = new TreeMap<>(Comparator.reverseOrder());
            for (Map.Entry<String, JsonElement> e : series.entrySet()) {
                LocalDate d = LocalDate.parse(e.getKey());
                JsonObject bar = e.getValue().getAsJsonObject();
                // Adjusted close = "5. adjusted close"
                // double close = bar.get("5. adjusted close").getAsDouble();
                // double close = bar.get("4. close").getAsDouble();
                if (!bar.has("4. close"))
                    continue;
                double close = bar.get("4. close").getAsDouble();
                out.put(d, close);
            }
            return out;
        }
    }
}
