package dev.stocks;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import static dev.stocks.Models.pctChange;

public class Main {
    public static void main(String[] args) throws IOException {
        String key = System.getenv("ALPHAVANTAGE_API_KEY");

        if (key == null || key.isBlank()) {
            try {
                key = java.nio.file.Files.readString(
                        java.nio.file.Paths.get("APIkey.txt")).trim();
            } catch (IOException e) {
                throw new RuntimeException("API key not found in env or file", e);
            }
        }

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("API key missing");
        }
        
        AlphaVantageClient api = new AlphaVantageClient(key);

        // 🔧 Configure here
        List<String> symbols = List.of("AAPL");
        // List<String> symbols = List.of("AAPL", "MSFT", "GOOGL", "AMZN", "TSLA",
        // "META");
        int lookbackDays = 20; // for trend & percent changes
        int maWindow = 5; // moving average window
        int topK = 3;

        // 1) Fetch data
        Map<String, Models.Series> seriesMap = new HashMap<>();
        for (String s : symbols) {
            var map = api.getDailyAdjustedCloses(s, true); // compact ~100 days
            seriesMap.put(s, new Models.Series(s, map));
        }

        // 2) Build % change map over the last N trading days
        Map<String, Double> pct = new HashMap<>();
        for (var entry : seriesMap.entrySet()) {
            var closesDesc = entry.getValue().closeByDate(); // newest->oldest
            List<Double> closesNewestFirst = new ArrayList<>(closesDesc.values());
            if (closesNewestFirst.size() < lookbackDays + 1)
                continue;
            double end = closesNewestFirst.get(0);
            double start = closesNewestFirst.get(lookbackDays);
            pct.put(entry.getKey(), pctChange(start, end));
        }

        // 3) Top gainers/losers (Heap)
        var gainers = DSAAlgorithms.topGainers(pct, topK);
        var losers = DSAAlgorithms.topLosers(pct, topK);

        System.out.println("=== Top " + topK + " Gainers (" + lookbackDays + "d) ===");
        gainers.forEach(e -> System.out.printf("%-6s %+6.2f%%%n", e.getKey(), e.getValue()));
        System.out.println("\n=== Top " + topK + " Losers (" + lookbackDays + "d) ===");
        losers.forEach(e -> System.out.printf("%-6s %+6.2f%%%n", e.getKey(), e.getValue()));

        // 4) Pick one symbol to demonstrate Stock Span + Moving Average
        String demo = symbols.get(0);
        var closeByDate = seriesMap.get(demo).closeByDate();
        // Convert to oldest->newest for span and MA
        List<Double> closesOldToNew = new ArrayList<>(closeByDate.descendingMap().values());
        int[] span = DSAAlgorithms.stockSpan(closesOldToNew);
        var ma = DSAAlgorithms.movingAverage(closesOldToNew, maWindow);

        System.out.println("\n=== " + demo + " Stock Span (last " + Math.min(lookbackDays, span.length) + " days) ===");
        for (int i = Math.max(0, span.length - lookbackDays); i < span.length; i++) {
            System.out.printf("Day %3d  Close=%8.2f  Span=%d%n", i + 1, closesOldToNew.get(i), span[i]);
        }

        System.out.println("\n=== " + demo + " Moving Average (k=" + maWindow + ") - last "
                + Math.min(lookbackDays, ma.size()) + " points ===");
        for (int i = Math.max(0, ma.size() - lookbackDays); i < ma.size(); i++) {
            System.out.printf("MA[%3d]=%8.2f%n", i + 1, ma.get(i));
        }

        // 5) Bonus: print the date range used
        LocalDate newest = closeByDate.firstKey();
        LocalDate oldest = closeByDate.lastKey();
        System.out.println("\nData window for " + demo + ": " + oldest + " → " + newest);
    }
}
