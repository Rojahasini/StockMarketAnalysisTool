# 📈 Stock Market Analysis Tool

## 🚀 Overview

This project is a Java-based stock analysis tool that fetches real-time historical stock data using the AlphaVantage API and applies Data Structures and Algorithms (DSA) to analyze trends and compute key metrics.

---

## ⚙️ Features

* Fetches stock data from external API
* Computes percentage change over time
* Identifies top gainers and losers (Heap)
* Calculates stock span (Stack)
* Computes moving averages (Sliding Window)

---

## 🧠 DSA Concepts Used

* Stack → Stock Span Problem
* Queue → Moving Average (Sliding Window)
* Heap → Top K Gainers/Losers
* HashMap → Symbol mapping
* TreeMap → Time-series sorted data

---

## 📊 Sample Output

```
=== Top 3 Gainers (20d) ===
AAPL   -7.53%

=== AAPL Moving Average ===
MA[96] = 250.00
```

---

## 🧪 Challenges Faced

* API rate limiting (handled by reducing requests)
* Premium endpoint restriction (switched to free endpoint)
* JSON parsing issues (fixed field mapping)

---

## ▶️ How to Run

1. Get API key from AlphaVantage
2. Set environment variable:

   ```
   setx ALPHAVANTAGE_API_KEY your_key
   ```
3. Run `Main.java`

---

## 💡 Future Improvements

* Multi-threaded API calls
* Data caching
* Graph visualization
* CLI input support
