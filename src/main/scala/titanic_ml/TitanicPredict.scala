package titanic_ml

import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession
import titanic_ml.TitanicDataPreprocessor.preprocessData

object TitanicPredict {
    def main(args: Array[String]): Unit = {

        // Get the path of the model input path, test file and predictions output path from arguments
        if (args.length < 3) {
            println("Usage: TitanicPredict <model_input_path> <test_file> <predictions_output_path")
            sys.exit(1)
        }
        val modelInputPath = args(0)
        val testPath = args(1)
        val predictionsOutputPath = args(2)

        // Initialize Spark session
        val spark = SparkSession.builder()
            .appName("Titanic Predict")
            .master("local[*]")
            .getOrCreate()
        spark.sparkContext.setLogLevel("ERROR")

        // Load test data
        val df = spark.read.option("header", "true").option("inferSchema", "true").csv(testPath)
        val processedDf = preprocessData(df).select("PassengerId", "Pclass", "SexIndex", "Age", "Fare", "FamilySize")

        // Load trained model
        val model = PipelineModel.load(modelInputPath)

        // Make predictions
        val predictions = model.transform(processedDf)
        val survivedPredictions = predictions.select("PassengerId", "prediction")
            .withColumnRenamed("prediction", "Survived_Prediction")

        // Save predictions
        survivedPredictions.coalesce(1).write.mode("overwrite").option("header", "true").csv(predictionsOutputPath)

        println("Predictions saved to " + predictionsOutputPath)

        // Stop Spark session
        spark.stop()
    }
}

