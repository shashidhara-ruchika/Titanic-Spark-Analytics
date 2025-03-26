package titanic_ml

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

object TitanicEvaluator {
    def main(args: Array[String]): Unit = {

        // Get the paths of actual and predicted data from arguments
        if (args.length < 2) {
            println("Usage: TitanicTrain <actual_data_path> <predicted_data_path>")
            sys.exit(1)
        }
        val actualDataPath = args(0)
        val predictedDataPath = args(1)

        // Initialize Spark session
        val spark = SparkSession.builder()
            .appName("Titanic Evaluator")
            .master("local[*]")
            .getOrCreate()
        spark.sparkContext.setLogLevel("ERROR")

        // Load actual test data
        val actualData = spark.read.option("header", "true").csv(actualDataPath)

        // Load predicted data (part-<something>.csv) using wildcard to select all files starting with part-
        val predictedData = spark.read.option("header", "true").csv(predictedDataPath + "/part-*.csv")

        // Join actual and predicted data on PassengerId
        val joinedData = actualData.join(predictedData, "PassengerId")
        val joinedDataWithTypes = joinedData.withColumn("Survived", col("Survived").cast("double"))
            .withColumn("Survived_Prediction", col("Survived_Prediction").cast("double"))

        // Initialize evaluator
        val evaluator = new MulticlassClassificationEvaluator()
            .setLabelCol("Survived")
            .setPredictionCol("Survived_Prediction")

        // Display Metrics
        // Calculate accuracy
        val accuracy = evaluator.setMetricName("accuracy").evaluate(joinedDataWithTypes)
        println(s"Accuracy: $accuracy")

        // Calculate precision
        val precision = evaluator.setMetricName("weightedPrecision").evaluate(joinedDataWithTypes)
        println(s"Precision: $precision")

        // Calculate recall
        val recall = evaluator.setMetricName("weightedRecall").evaluate(joinedDataWithTypes)
        println(s"Recall: $recall")

        // Calculate F1 score
        val f1Score = evaluator.setMetricName("f1").evaluate(joinedDataWithTypes)
        println(s"F1 Score: $f1Score")

        // Stop Spark session
        spark.stop()
    }
}
