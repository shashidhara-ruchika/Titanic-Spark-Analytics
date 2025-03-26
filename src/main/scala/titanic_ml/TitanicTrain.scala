package titanic_ml

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession
import titanic_ml.TitanicDataPreprocessor.preprocessData

object TitanicTrain {
    def main(args: Array[String]): Unit = {

        // Get the path of the model output path and training data file from arguments
        if (args.length < 2) {
            println("Usage: TitanicTrain <model_output_path> <train_file>")
            sys.exit(1)
        }
        val modelPath = args(0)
        val trainPath = args(1)

        // Initialize Spark session
        val spark = SparkSession.builder()
            .appName("Titanic Train Model")
            .master("local[*]")
            .getOrCreate()
        spark.sparkContext.setLogLevel("ERROR")

        // Load training data
        val df = spark.read.option("header", "true").option("inferSchema", "true").csv(trainPath)

        // Preprocess data
        val processedDf = preprocessData(df).select("Survived", "Pclass", "SexIndex", "Age", "Fare", "FamilySize")

        // Define feature columns
        val featureCols = Array("Pclass", "SexIndex", "Age", "Fare", "FamilySize")
        val assembler = new VectorAssembler().setInputCols(featureCols).setOutputCol("features")

        // Define classifier
        val lg = new LogisticRegression().setLabelCol("Survived").setFeaturesCol("features")

        // Build pipeline
        val pipeline = new Pipeline().setStages(Array(assembler, lg))

        // Train model
        val model = pipeline.fit(processedDf)

        // Save model
        model.write.overwrite().save(modelPath)

        println("Model training complete and saved to " + modelPath)

        // Stop Spark session
        spark.stop()
    }

}
