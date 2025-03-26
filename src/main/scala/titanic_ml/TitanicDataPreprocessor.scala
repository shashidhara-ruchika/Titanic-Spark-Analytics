package titanic_ml

import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, mean}

object TitanicDataPreprocessor {

    def preprocessData(df: DataFrame): DataFrame = {

        // Fill missing values for "Age" and "Fare" with mean
        val ageMean = df.select(mean("Age")).first().getDouble(0)
        val fareMean = df.select(mean("Fare")).first().getDouble(0)
        val dfWithNoNulls = df.na.fill(Map(
            "Age" -> ageMean,
            "Fare" -> fareMean
        ))

        // Indexing categorical columns for "Sex"
        val indexedDf = new StringIndexer()
            .setInputCol("Sex")
            .setOutputCol("SexIndex")
            .fit(dfWithNoNulls)
            .transform(dfWithNoNulls)

        // Adding new columns for "FamilySize"
        val finalDf = indexedDf.withColumn("FamilySize", col("SibSp") + col("Parch") + 1)

        // Cast all relevant columns to DoubleType
        finalDf
            .withColumn("Pclass", col("Pclass").cast("double"))
            .withColumn("SexIndex", col("SexIndex").cast("double"))
            .withColumn("Age", col("Age").cast("double"))
            .withColumn("Fare", col("Fare").cast("double"))
            .withColumn("FamilySize", col("FamilySize").cast("double"))
    }

}
