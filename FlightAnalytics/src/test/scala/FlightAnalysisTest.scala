import org.scalatest.funsuite.AnyFunSuite
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import java.sql.Date

class FlightAnalysisTest extends AnyFunSuite {

  val spark: SparkSession = SparkSession.builder()
    .master("local[*]")
    .appName("FlightAnalysisTest")
    .getOrCreate()

  import spark.implicits._

  test("Flights per month aggregation works") {
    val sampleData = Seq(
      ("F1", "P1", "2020-01-05", "UK"),
      ("F2", "P2", "2020-01-15", "FR"),
      ("F3", "P3", "2020-02-10", "US")
    ).toDF("flightId", "passengerId", "date", "to")
      .withColumn("date", to_date($"date"))

    val result = sampleData
      .withColumn("Month", month($"date"))
      .groupBy("Month")
      .agg(count("*").alias("Number_of_Flights"))
      .orderBy("Month")

    val output = result.collect().map(r => (r.getInt(0), r.getLong(1)))
    assert(output.contains((1, 2)))
    assert(output.contains((2, 1)))
  }

  test("flownTogether identifies correct pairs") {
    val sampleFlights = Seq(
      ("F1", "P1", Date.valueOf("2017-01-01")),
      ("F1", "P2", Date.valueOf("2017-01-01")),
      ("F2", "P1", Date.valueOf("2017-01-15")),
      ("F2", "P2", Date.valueOf("2017-01-15")),
      ("F3", "P1", Date.valueOf("2017-02-01")),
      ("F3", "P2", Date.valueOf("2017-02-01")),
      ("F4", "P3", Date.valueOf("2017-02-01"))
    ).toDF("flightId", "passengerId", "date")

    val filtered = sampleFlights.filter($"date".between("2017-01-01", "2017-03-01"))

    val coFlights = filtered.as("f1")
      .join(filtered.as("f2"),
        $"f1.flightId" === $"f2.flightId" && $"f1.passengerId" < $"f2.passengerId")
      .select($"f1.passengerId".alias("Passenger_1_ID"), $"f2.passengerId".alias("Passenger_2_ID"))
      .groupBy("Passenger_1_ID", "Passenger_2_ID")
      .agg(count("*").alias("Number_of_flights_together"))
      .filter($"Number_of_flights_together" > 2)

    val result = coFlights.collect().map(r => ((r.getString(0), r.getString(1)), r.getLong(2)))
    assert(result.contains((("P1", "P2"), 3)))
  }

}