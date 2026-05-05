# Flight Analytics

[![Scala](https://img.shields.io/badge/Scala-2.12.10-red.svg)](https://www.scala-lang.org/)
[![Apache Spark](https://img.shields.io/badge/Apache%20Spark-2.4.8-orange.svg)](https://spark.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A Scala-based project for analyzing large-scale passenger and flight data using Apache Spark. This application demonstrates data engineering techniques, functional programming, and distributed computing to extract insights from flight datasets.

## Features

This project performs comprehensive flight data analysis to answer key business questions:

1. **Flights Per Month**: Aggregates the total number of flights for each calendar month.
2. **Top 100 Frequent Flyers**: Identifies the top 100 passengers with the most distinct flights, including their names and flight counts.
3. **Longest Non-UK Country Run**: Calculates the longest streak of consecutive countries visited by each passenger without returning to the UK.
4. **Passenger Pairs Flying Together**: Detects pairs of passengers who have flown together more than 3 times.
5. **Flights Together in Time Range**: Identifies passenger pairs who flew together more than N times within a specified date range.

## Project Structure

```
FlightAnalytics/
├── data/
│   ├── flightData.csv          # Flight records dataset
│   └── passengers.csv          # Passenger information dataset
├── src/
│   ├── main/
│   │   └── scala/
│   │       └── FlightAnalysis.scala  # Main analysis logic
│   └── test/
│       └── scala/
│           └── FlightAnalysisTest.scala  # Unit tests
├── output/                      # Generated output directories
│   ├── co_flying_pairs/
│   ├── flights_per_month/
│   ├── flights_together_within_date_range/
│   ├── longest_run_without_visiting_the_uk/
│   └── top_100_frequent_flyers/
├── project/
│   └── build.properties        # SBT build properties
├── build.sbt                   # SBT build configuration
└── plugins.sbt                 # SBT plugins
```

## Prerequisites

Before running this project, ensure you have the following installed:

- **Java JDK 8** or higher
- **Scala 2.12.10**
- **Apache Spark 2.4.8**
- **SBT (Scala Build Tool)**

### Installation

1. **Install Java JDK**:
   ```bash
   # On Ubuntu/Debian
   sudo apt-get install openjdk-8-jdk

   # On macOS (using Homebrew)
   brew install openjdk@8
   ```

2. **Install Scala**:
   ```bash
   # Download and install from https://www.scala-lang.org/download/
   ```

3. **Install Apache Spark**:
   ```bash
   # Download Spark 2.4.8 from https://spark.apache.org/downloads.html
   # Extract and set SPARK_HOME environment variable
   export SPARK_HOME=/path/to/spark
   export PATH=$PATH:$SPARK_HOME/bin
   ```

4. **Install SBT**:
   ```bash
   # On Ubuntu/Debian
   echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
   echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
   curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
   sudo apt-get update
   sudo apt-get install sbt

   # On macOS
   brew install sbt
   ```

## Data Description

The project uses two main datasets:

- **flightData.csv**: Contains flight records with details such as flight dates, passenger IDs, and destinations.
- **passengers.csv**: Contains passenger information including IDs, first names, and last names.

Ensure these files are placed in the `data/` directory before running the analysis.

## Building the Project

To compile and package the application:

```bash
sbt clean compile package
```

This will generate the JAR file at:
```
target/scala-2.12/flightanalytics_2.12-0.1.0-FINAL.jar
```

## Running the Analysis

Execute the application using `spark-submit`:

```bash
spark-submit \
  --class FlightAnalysis \
  --master local[*] \
  target/scala-2.12/flightanalytics_2.12-0.1.0-FINAL.jar
```

- `--master local[*]`: Runs Spark locally using all available cores. For cluster deployment, replace with your cluster master URL.
- Results will be printed to the console in tabular format and saved to the `output/` directory.

### Command Line Options

The application currently runs with default parameters. For future enhancements, consider adding command-line arguments for custom date ranges or thresholds.

## Sample Outputs

### 1. Flights Per Month

```
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

### 2. Top 100 Frequent Flyers

```
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
(only showing top 20 rows)
```

### 3. Longest Run Without Visiting the UK

```
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
(only showing top 20 rows)
```

### 4. Co-flying Pairs (More than 3 times)

```
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
(only showing top 20 rows)
```

## Testing

Run the unit tests using SBT:

```bash
sbt test
```

Tests are located in `src/test/scala/FlightAnalysisTest.scala`.

## Acknowledgments

- Apache Spark for distributed computing capabilities.
- Scala community for functional programming support.
- Quantexa for providing the dataset and problem statement.

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
