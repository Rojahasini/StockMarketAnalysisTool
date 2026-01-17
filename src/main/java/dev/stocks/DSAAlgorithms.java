package dev.stocks;

import java.util.*;

public class DSAAlgorithms {

    /** Stock Span using a stack: O(n). Input: list of prices oldest->newest. */
    public static int[] stockSpan(List<Double> prices) {
        int n = prices.size();
        int[] span = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // stores indices with decreasing prices

        for (int i = 0; i < n; i++) {
            double p = prices.get(i);
            while (!stack.isEmpty() && prices.get(stack.peek()) <= p)
                stack.pop();
            span[i] = stack.isEmpty() ? (i + 1) : (i - stack.peek());
            stack.push(i);
        }
        return span;
    }

    /** Moving average over window k using a queue: O(n). */
    public static List<Double> movingAverage(List<Double> prices, int k) {
        if (k <= 0)
            throw new IllegalArgumentException("k must be > 0");
        List<Double> out = new ArrayList<>();
        Deque<Double> q = new ArrayDeque<>();
        double sum = 0.0;
        for (int i = 0; i < prices.size(); i++) {
            sum += prices.get(i);
            q.addLast(prices.get(i));
            if (q.size() > k)
                sum -= q.removeFirst();
            if (q.size() == k)
                out.add(sum / k);
        }
        return out;
    }

    /** Top K gainers/losers using heaps from % change map (symbol->pctChange). */
    public static List<Map.Entry<String, Double>> topGainers(Map<String, Double> pct, int k) {
        PriorityQueue<Map.Entry<String, Double>> minHeap = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getValue));
        for (var e : pct.entrySet()) {
            if (minHeap.size() < k)
                minHeap.offer(e);
            else if (e.getValue() > minHeap.peek().getValue()) {
                minHeap.poll();
                minHeap.offer(e);
            }
        }
        List<Map.Entry<String, Double>> out = new ArrayList<>(minHeap);
        out.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return out;
    }

    public static List<Map.Entry<String, Double>> topLosers(Map<String, Double> pct, int k) {
        PriorityQueue<Map.Entry<String, Double>> maxHeap = new PriorityQueue<>(
                (a, b) -> Double.compare(b.getValue(), a.getValue()));
        for (var e : pct.entrySet()) {
            if (maxHeap.size() < k)
                maxHeap.offer(e);
            else if (e.getValue() < maxHeap.peek().getValue()) {
                maxHeap.poll();
                maxHeap.offer(e);
            }
        }
        List<Map.Entry<String, Double>> out = new ArrayList<>(maxHeap);
        out.sort(Comparator.comparingDouble(Map.Entry::getValue));
        return out;
    }
}
