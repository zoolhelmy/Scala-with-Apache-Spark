object FlightAnalysis extends App {
  import org.apache.spark.sql.{SparkSession, DataFrame}
  import org.apache.spark.sql.functions._
  import org.apache.spark.sql.expressions.Window
  import java.sql.Date

  val spark = SparkSession.builder()
    .appName("FlightAnalytics")
    .master("local[*]")
    .getOrCreate()

  import spark.implicits._

  /**
   * Exports a DataFrame as a single CSV file inside an output subfolder.
   *
   * @param df     The DataFrame to export
   * @param folder The name of the output folder under `/output`
   */

  def exportCSV(df: DataFrame, folder: String): Unit = {
    df.coalesce(1)
      .write
      .option("header", true)
      .mode("overwrite")
      .csv(s"output/$folder")
  }

  // Load flight data
  val flightDF = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("data/flightData.csv")
    .withColumn("date", to_date($"date", "yyyy-MM-dd"))

  // Load passenger data
  val passengerDF = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("data/passengers.csv")

  // Question 1: Flights per Month
  val flightsPerMonth = flightDF
    .withColumn("Month", month($"date"))
    .groupBy("Month")
    .agg(count("*").alias("Number_of_Flights"))
    .orderBy("Month")

  exportCSV(flightsPerMonth, "flights_per_month")

  // Question 2: Top 100 Frequent Flyers
  val topFlyers = flightDF
    .groupBy("passengerId")
    .agg(countDistinct("flightId").alias("Number_of_Flights"))
    .join(passengerDF, "passengerId")
    .orderBy($"Number_of_Flights".desc)
    .limit(100)
    .select(
      $"passengerId".alias("Passenger_ID"),
      $"Number_of_Flights",
      $"firstName".alias("First_name"),
      $"lastName".alias("Last_name")
    )

  exportCSV(topFlyers, "top_100_frequent_flyers")

  // Question 3: Longest Run Without Visiting the UK

  val windowSpec = Window.partitionBy("passengerId").orderBy("date")
  val tagged = flightDF
    .withColumn("isUK", $"to" === "UK")
    .withColumn("ukFlag", when($"isUK", 1).otherwise(0))
    .withColumn("segmentId", sum($"ukFlag").over(windowSpec))

  val nonUK = tagged.filter(!$"isUK")
  val runLengths = nonUK
    .groupBy("passengerId", "segmentId")
    .agg(countDistinct("to").alias("runLength"))

  val longestRun = runLengths
    .groupBy("passengerId")
    .agg(max("runLength").alias("Longest_Run"))
    .orderBy($"Longest_Run".desc)

  exportCSV(longestRun, "longest_run_without_visiting_the_uk")

  // Question 4: Co-flying Pairs (More than 3 times)
  val simplifiedFlights = flightDF.select("flightId", "passengerId")

  val coPassengers = simplifiedFlights.as("f1")
    .join(simplifiedFlights.as("f2"),
      $"f1.flightId" === $"f2.flightId" && $"f1.passengerId" < $"f2.passengerId")
    .select($"f1.passengerId".alias("Passenger_1_ID"), $"f2.passengerId".alias("Passenger_2_ID"))

  val frequentPairs = coPassengers
    .groupBy("Passenger_1_ID", "Passenger_2_ID")
    .agg(count("*").alias("Number_of_flights_together"))
    .filter($"Number_of_flights_together" > 3)
    .orderBy($"Number_of_flights_together".desc)

  exportCSV(frequentPairs, "co_flying_pairs")

  // Extra Marks: Flights together within date range

  /**
   * Finds passenger pairs who flew together more than `atLeastNTimes` between two dates.
   *
   * @param atLeastNTimes Minimum number of shared flights
   * @param fromDate Start of the date range (inclusive)
   * @param toDate End of the date range (inclusive)
   * @return DataFrame of qualifying passenger pairs with counts and date range
   */

  def flownTogether(atLeastNTimes: Int, fromDate: Date, toDate: Date): DataFrame = {
    val filteredFlights = flightDF
      .filter($"date".between(fromDate, toDate))
      .select("flightId", "passengerId")

    val coFlights = filteredFlights.as("f1")
      .join(filteredFlights.as("f2"),
        $"f1.flightId" === $"f2.flightId" && $"f1.passengerId" < $"f2.passengerId")
      .select($"f1.passengerId".alias("Passenger_1_ID"), $"f2.passengerId".alias("Passenger_2_ID"))

    coFlights
      .groupBy("Passenger_1_ID", "Passenger_2_ID")
      .agg(count("*").alias("Number_of_flights_together"))
      .filter($"Number_of_flights_together" > atLeastNTimes)
      .withColumn("From", lit(fromDate))
      .withColumn("To", lit(toDate))
      .orderBy($"Number_of_flights_together".desc)
  }

  val rangePairs = flownTogether(3, Date.valueOf("2017-01-01"), Date.valueOf("2017-03-01"))
  exportCSV(rangePairs, "flights_together_within_date_range")

  println("All analytics completed and CSV saved in /output folders.")

  // Run Output Stream
  println("Question 1: Flights per Month")
  flightsPerMonth.show()
  println("Question 2: Top 100 Frequent Flyers")
  topFlyers.show()
  println("Question 3: Longest Run Without Visiting the UK")
  longestRun.show()
  println("Question 4: Co-flying Pairs (More than 3 times)")
  frequentPairs.show()
  println("Extra Marks: Flights together within date range")
  flownTogether(3, Date.valueOf("2017-01-01"), Date.valueOf("2017-03-01")).show()
}
