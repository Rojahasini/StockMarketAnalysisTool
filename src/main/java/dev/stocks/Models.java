package dev.stocks;

import java.time.LocalDate;
import java.util.NavigableMap;

public class Models {
    public record Series(String symbol, NavigableMap<LocalDate, Double> closeByDate) {
    }

    /** Compute percentage change between two closes. */
    public static double pctChange(double from, double to) {
        return (to - from) / from * 100.0;
    }
}
