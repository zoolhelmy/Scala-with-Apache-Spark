# Flight Analytics (Scala + Apache Spark)

This project showcases large-scale passenger and flight data analysis using **Apache Spark** and **Scala**, packaged for execution via `spark-submit`. It demonstrates both data engineering fluency and functional programming techniques.

---

## What It Does

Analyzes flight data to answer key questions:

1. **Flights Per Month** — Aggregates number of flights for each calendar month.
2. **Top 100 Frequent Flyers** — Finds passengers with the most distinct flights.
3. **Longest Non-UK Country Run** — Tracks the longest streak of countries visited without returning to the UK.
4. **Passenger Pairs Flying Together** — Detects pairs who shared more than 3 flights.
5. **Extra Marks: Flights Together in Time Range** — Identifies passenger pairs who flew together more than _N_ times between two dates.

---

## Main File Structure

```text
FlightAnalytics/
├── data/
│   ├── flightData.csv
│   └── passengers.csv
├── src/
│   └── main/
│       └── scala/
│           └── FlightAnalysis.scala
│   └── test/
│       └── scala/
│           └── FlightAnalysisTest.scala
├── output/
│   ├── co_flying_pairs/...
│   └── flights_per_month/...
│   └── flights_together_within_date_range/...
│   └── longest_run_without_visiting_the_uk/...
│   └── top_100_frequent_flyers/...
└── project/
    └── build.properties
├── build.sbt
├── plugins.sbt
```

---

## Requirements

- Apache Spark **2.4.8**
- Scala **2.12.10**
- SBT (Scala Build Tool)
- Java JDK 8

---

## How to Build

```bash
sbt clean compile package
```

Generates:

```text
target/scala-2.12/flightanalytics_2.12-0.1.0-FINAL.jar
```

---

## How to Run

```bash
spark-submit ^
  --class FlightAnalysis ^
  --master local[*] ^
  target/scala-2.12/flightanalytics_2.12-0.1.0-FINAL.jar
```

All results will print directly to the console in tabular form.

---

## Question 1 Output: Flights per Month

```text
+-----+-----------------+
|Month|Number_of_Flights|
+-----+-----------------+
|    1|             9700|
|    2|             7300|
|    3|             8200|
|    4|             9200|
|    5|             9200|
|    6|             7100|
|    7|             8700|
|    8|             7600|
|    9|             8500|
|   10|             7600|
|   11|             7500|
|   12|             9400|
+-----+-----------------+
```

---

## Question 2 Output: Top 100 Frequent Flyers

```text
+------------+-----------------+----------+---------+
|Passenger_ID|Number_of_Flights|First_name|Last_name|
+------------+-----------------+----------+---------+
|        2068|               32|   Yolande|     Pete|
|        1677|               27| Katherina| Vasiliki|
|        4827|               27|     Jaime|    Renay|
|        8961|               26|     Ginny|    Clara|
|        3173|               26|  Sunshine|    Scott|
|        5867|               25|     Luise|  Raymond|
|        2857|               25|       Son|  Ginette|
|         760|               25|    Vernia|      Mui|
|        8363|               25|    Branda|   Kimiko|
|        5096|               25|    Blythe|     Hyon|
|        6084|               25|      Cole|   Sharyl|
|         288|               25|    Pamila|    Mavis|
|         917|               25|    Anisha|   Alaine|
|        1240|               24| Catherine|    Missy|
|        5668|               24|    Gladis|  Earlene|
|        1343|               24|   Bennett|    Staci|
|        2441|               24|     Kayla|    Rufus|
|        3367|               24| Priscilla|    Corie|
|        9441|               23|  Annalisa|   Luanna|
|         613|               23|    Palmer|   Yuonne|
+------------+-----------------+----------+---------+
only showing top 20 rows
```

---

## Question 3 Output: Longest Run Without Visiting the UK

```text
+-----------+-----------+
|passengerId|Longest_Run|
+-----------+-----------+
|        721|         18|
|        288|         18|
|       9441|         18|
|       1677|         18|
|       4632|         17|
|       2437|         17|
|       2378|         17|
|       8411|         17|
|       3608|         17|
|       5668|         17|
|       2068|         17|
|       2185|         17|
|       6084|         17|
|       1651|         17|
|       8961|         17|
|       2939|         17|
|       8353|         17|
|        157|         17|
|       2867|         17|
|       2857|         17|
+-----------+-----------+
only showing top 20 rows
```

---

## Question 4 Output: Co-flying Paris (More than 3 times)

```text
+--------------+--------------+--------------------------+
|Passenger_1_ID|Passenger_2_ID|Number_of_flights_together|
+--------------+--------------+--------------------------+
|           701|           760|                        15|
|          2717|          2759|                        14|
|          3503|          3590|                        14|
|          2939|          5490|                        13|
|          4316|          4373|                        12|
|           366|           374|                        12|
|           382|           392|                        12|
|          1337|          1484|                        12|
|          1208|          3093|                        12|
|          1337|          2867|                        12|
|           975|          1371|                        12|
|          2939|          4395|                        12|
|          7877|          9252|                        12|
|           760|           763|                        12|
|          2550|          4441|                        12|
|          4395|          4399|                        12|
|          3278|          5423|                        12|
|          3021|          9522|                        12|
|          2926|          3590|                        12|
|           701|           763|                        12|
+--------------+--------------+--------------------------+
only showing top 20 rows
```

---

## Extra Marks Output: Flights together within date range

```text
+--------------+--------------+--------------------------+----------+----------+
|Passenger_1_ID|Passenger_2_ID|Number_of_flights_together|      From|        To|
+--------------+--------------+--------------------------+----------+----------+
|           755|           771|                        11|2017-01-01|2017-03-01|
|           701|           724|                         9|2017-01-01|2017-03-01|
|           724|           760|                         9|2017-01-01|2017-03-01|
|           746|           751|                         9|2017-01-01|2017-03-01|
|           740|           746|                         9|2017-01-01|2017-03-01|
|           740|           763|                         9|2017-01-01|2017-03-01|
|           751|           763|                         9|2017-01-01|2017-03-01|
|           716|           763|                         9|2017-01-01|2017-03-01|
|          1023|          1095|                         9|2017-01-01|2017-03-01|
|           746|           763|                         9|2017-01-01|2017-03-01|
|           701|           763|                         9|2017-01-01|2017-03-01|
|           724|           746|                         9|2017-01-01|2017-03-01|
|           701|           751|                         9|2017-01-01|2017-03-01|
|           740|           751|                         9|2017-01-01|2017-03-01|
|          1001|          1023|                         9|2017-01-01|2017-03-01|
|           716|           724|                         9|2017-01-01|2017-03-01|
|           724|           763|                         9|2017-01-01|2017-03-01|
|           760|           763|                         9|2017-01-01|2017-03-01|
|           724|           740|                         9|2017-01-01|2017-03-01|
|           740|           772|                         9|2017-01-01|2017-03-01|
+--------------+--------------+--------------------------+----------+----------+
only showing top 20 rows
```

---

## Notes

- Data is read from `/data/flightData.csv` and `/data/passengers.csv`.
- Results are printed as CSV in `/output/`.
- Results are printed using `.show()` from each DataFrame.
- The `flownTogether()` function supports custom date ranges and thresholds.

---

## Credits

Created by zool@zoolhelmy.com
Built with Scala + Apache Spark  
Packaged with on IntelliJ + SBT + Windows CMD
