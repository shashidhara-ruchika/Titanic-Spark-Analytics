package titanic_eda

import org.apache.spark.sql.functions.{round, _}
import org.apache.spark.sql.{DataFrame, RelationalGroupedDataset, SparkSession}
import titanic_ml.TitanicDataPreprocessor


object TitanicDatasetAnalyzer {

    def main(args: Array[String]): Unit = {
        if (args.length < 1) {
            println("Dataset's filepath as argument is not provided")
            sys.exit(1)
        }
        val filePath = args(0)

        implicit val sparkSession: SparkSession = initalizeSparkSession()

        sparkSession.sparkContext.setLogLevel("ERROR") // Ignore all the INFO and WARN messages

        println("\n\nSpark Assignment - 2\n\n")

        val dataFrame = loadDataset(sparkSession, filePath)

        val titanicDf = cleanDataset(dataFrame)

        performExploratoryDataAnalysis(titanicDf)
    }

    def initalizeSparkSession(): SparkSession = {
        SparkSession.builder()
            .appName("TitanicDatasetAnalyzer")
            .master("local[*]")
            .getOrCreate()
    }

    def loadDataset(sparkSession: SparkSession, filePath: String): DataFrame = {
        sparkSession.read
            .format("csv")
            .option("header", "true")
            .option("inferSchema", "true")
            .load(filePath)
    }

    def calculateAgeStatistics(groupedDf: RelationalGroupedDataset): DataFrame = {
        groupedDf.agg(
            count("*").alias("Count"),
            round(mean("age"), 2).alias("Mean"),
            round(expr("percentile_approx(age, 0.5)"), 2).alias("Median"),
            round(stddev("age"), 2).alias("StdDev"),
            round(skewness("age"), 2).alias("Skewness")
        )
    }

    def calculateAgeStatistics(df: DataFrame): DataFrame = {
        df.agg(
            count("*").alias("Count"),
            round(mean("age"), 2).alias("Mean"),
            round(expr("percentile_approx(age, 0.5)"), 2).alias("Median"),
            round(stddev("age"), 2).alias("StdDev"),
            round(skewness("age"), 2).alias("Skewness")
        )
    }

    def cleanDataset(df: DataFrame): DataFrame = {

        // Check the total number of rows in the dataset
        val rowCount = df.count()
        println(s"Total number of rows: $rowCount")

        df.describe("Survived", "Pclass", "Sex", "Age", "SibSp", "Parch", "Fare", "Embarked").show()

        // Check which columns have empty values
        println("\nNull Count by each column:")
        val columnNullCountDf = df.select(df.columns.map(c => count(when(col(c).isNull, 1)).alias(c)): _*)
        columnNullCountDf.show()

        // Decide what to use to fill empty values in Age column with intuition of grouping with Pclass & Sex
        // If Skewness is low, age can be filled with its mean, or else its median
        println("Ungrouped Age Statistics:")
        val ungroupedAgeStatistics = calculateAgeStatistics(df)
        ungroupedAgeStatistics.show()

        println("Age Statistics Grouped by Pclass:")
        val pclassGroupedAgeStatistics = calculateAgeStatistics(df.groupBy("Pclass"))
        pclassGroupedAgeStatistics.orderBy("Pclass").show()

        println("Age Statistics Grouped by Sex:")
        val sexGroupedAgeStatistics = calculateAgeStatistics(df.groupBy("Sex"))
        sexGroupedAgeStatistics.orderBy("Sex").show()

        println("Age Statistics by Grouped Pclass and Sex:")
        val pclassSexGroupedAgeStatistics = calculateAgeStatistics(df.groupBy("Pclass", "Sex"))
        pclassSexGroupedAgeStatistics.orderBy("Pclass", "Sex").show()

        TitanicDataPreprocessor.preprocessData(df)
    }

    def performExploratoryDataAnalysis(titanicDf: DataFrame): Unit = {

        // 1.  What is the average ticket fare for each Ticket class?
        val avgFareAndSurvivalPercentageByClassDf = titanicDf.groupBy("Pclass")
            .agg(round(avg("Fare"), 2).alias("AverageFare"), round(avg("Survived") * 100, 2).alias("SurvivalPercentage"))
            .orderBy("Pclass")

        println("\n\n1. Average Ticket Fare for each Ticket class:")
        avgFareAndSurvivalPercentageByClassDf.select("Pclass", "AverageFare").show()

        // 2.  What is the survival percentage for each Ticket class?
        println("\n2. Survival Percentage for each Ticket class:")
        avgFareAndSurvivalPercentageByClassDf.select("Pclass", "SurvivalPercentage").show()

        // Which class has the highest survival rate?
        val highestSurvivalClass = avgFareAndSurvivalPercentageByClassDf.orderBy(desc("SurvivalPercentage")).first()
        println(s"Class with the highest survival rate: ${highestSurvivalClass.getAs[Int]("Pclass")}")


        println("\n\nTo find the possible number of people who could be a person, given the Age,\n" +
            "since few of the Passenger's Age is unknown, I have considered them in the Age filter")

        // 3. Rose DeWitt Bukater was 17 years old when she boarded the titanic.
        // She is traveling with her mother and fiance( they are not married yet, so they are not related).
        // She is traveling first class. With the information of her age, gender, class she is traveling in,
        // and the fact that she is traveling with one parent, find the number of passengers who could possibly be Rose.
        // ( PS: if you watched the movie you will know if she survived or died )
        val possibleRoseDf = titanicDf.filter(
            (col("Age").isNull || (col("Age") >= 17 && col("Age") < 18)) &&
                col("Parch") === 1 &&
                col("SibSp") === 0 &&
                col("Sex") === "female" &&
                col("Pclass") === 1 &&
                col("Survived") === 1
        )
        val possibleRoseCount = possibleRoseDf.count().toInt
        println(s"\n\n3. Number of Passengers who could possibly be Rose: ${possibleRoseCount}")

        // 4. Jack Dawson born in 1892 died on April 15, 1912. He is either 20 or 19 years old.
        // He travels 3rd class and has no relatives onboard. Find the number of passengers who could possibly be Jack?
        val possibleJackDf = titanicDf.filter(
            (col("Age").isNull || (col("Age") >= 19 && col("Age") < 21)) &&
                col("Parch") === 0 &&
                col("SibSp") === 0 &&
                col("Sex") === "male" &&
                col("Pclass") === 3 &&
                col("Survived") === 0
        )
        val possibleJackCount = possibleJackDf.count().toInt
        println(s"\n\n4. Number of passengers who could possibly be Jack: ${possibleJackCount}")


        println("\n\nI have considered mean to be filled for the the \"Age \" column\n" +
            "as the skewness is low and the mean is close to the median\n" +
            "and the Age column is not normally distributed")

        // 5. Split the age for every 10 years. 1-10 as one age group, 11- 20 as another etc.
        println("\n5. Splitting Age Group for every 10 years:")
        val titanicDfWithAgeGrouped = titanicDf.withColumn("AgeGroup",
            when(col("Age") > 0 && col("Age") <= 10, "0-10")
                .when(col("Age") > 10 && col("Age") <= 20, "11-20")
                .when(col("Age") > 20 && col("Age") <= 30, "21-30")
                .when(col("Age") > 30 && col("Age") <= 40, "31-40")
                .when(col("Age") > 40 && col("Age") <= 50, "41-50")
                .when(col("Age") > 50 && col("Age") <= 60, "51-60")
                .when(col("Age") > 60 && col("Age") <= 70, "61-70")
                .when(col("Age") > 70 && col("Age") <= 80, "71-80")
                .when(col("Age") > 80 && col("Age") <= 90, "81-90")
                .when(col("Age") > 90 && col("Age") <= 100, "91-100")
                .when(col("Age") > 100, "100+")
        )
        val ageGroupStatisticsDf = titanicDfWithAgeGrouped.groupBy("AgeGroup")
            .agg(
                round(avg("Fare"), 2).alias("AverageFare"),
                round(avg("Survived") * 100, 2).alias("SurvivalPercentage"),
                count("*").alias("TotalPassengers"),
            ).orderBy("AgeGroup")
        ageGroupStatisticsDf.show()

        // What is the relation between the ages and the ticket fare?
        println("Relation between the ages & ticket fare:\n" +
            "Age groups (Mid-Age) between 30 - 70 have higher fares" +
            " compared to age groups (Youth) < 30 and (Seniors) > 70")
        val highestSurvivalAgeGroup = ageGroupStatisticsDf.orderBy(desc("SurvivalPercentage")).first()

        // Which age group most likely survived ?
        println(s"\nAge Group with the highest survival percentage: ${highestSurvivalAgeGroup.getAs[String]("AgeGroup")}")

        // 6. Value counts of the columns
        println("\n\n6. Value counts of the columns:")
        titanicDf.groupBy("Survived").count().show()
        println("Only 342 Passengers survived out of 891\n\n")

        titanicDf.groupBy("Pclass").count().show()
        println("Most number of passengers from 3rd ticket class\n\n")

        titanicDf.groupBy("Embarked").count().show()
        println("Most number of passengers, count = 644 embarked from Southampton\n\n")


        // 7. Correlation between the columns
        println("\n\n7. Correlation between the columns:")
        titanicDf.select(corr("Survived", "Pclass").alias("Survived_Pclass"),
            corr("Survived", "Age").alias("Survived_Age"),
            corr("Survived", "SibSp").alias("Survived_SibSp"),
            corr("Survived", "Parch").alias("Survived_Parch"),
            corr("Survived", "Fare").alias("Survived_Fare"),
            corr("Survived", "FamilySize").alias("Survived_FamilySize"),
            corr("Survived", "SexIndex").alias("Survived_SexIndex")
        ).show()
        println("There is high correlation between the columns: Survived & Pclass, Survived & Fare, Survived & SexIndex\n\n")

        // 8. Survival Rate by Gender
        println("8. Survival Rate by Gender:")
        titanicDf.groupBy("Sex")
            .agg(round(avg("Survived") * 100, 2).alias("SurvivalRate"), count("*").alias("TotalPassengers"), avg("Survived").alias("SurvivalRate"))
            .show()
        println("Females more likely survived\n\n")

        // 9. Survival Rate, Ticket Fare & Count by Embarkation Port
        println("9. Survival Rate by Embarkation Port:")
        titanicDf.groupBy("Embarked")
            .agg(round(avg("Survived") * 100, 2).alias("SurvivalRate"), round(avg("Fare"), 2).alias("AverageFare"), count("*").alias("TotalPassengers"), avg("Survived").alias("SurvivalRate"))
            .show()
        println("No conclusive remark with survival rate, but ticket prices were the highest from Cherbourg\n\n")

        // 10. Survival Rate by Pclass
        println("10. Survival Rate by Pclass:")
        titanicDf.groupBy("Pclass")
            .agg(round(avg("Survived") * 100, 2).alias("SurvivalRate"), count("*").alias("TotalPassengers"), avg("Survived").alias("SurvivalRate"))
            .show()
        println("1st class more likely survived\n\n")

        // 11. Family Size Count
        println("11. Family Size Count:")
        titanicDf.groupBy("FamilySize")
            .agg(count("*").alias("TotalPassengers"))
            .show()
        println("11. More than 50% of passengers traveled alone\n\n")

        // 12. Ticket Fare by Pclass, Sex
        println("12. Ticket Fare by Pclass, Sex")
        titanicDf.groupBy("Pclass", "Sex")
            .agg(round(avg("Fare"), 2).alias("AverageFare"), count("*").alias("TotalPassengers"), round(avg("Survived") * 100, 2).alias("SurvivalRate"))
            .show()
        println("1st class & females were given more preference\n\n")

        // 13. Ticket Fare by Pclass, Embarked
        println("13. Ticket Fare by Pclass, Embarked")
        titanicDf.groupBy("Pclass", "Embarked")
            .agg(round(avg("Fare"), 2).alias("AverageFare"), count("*").alias("TotalPassengers"), round(avg("Survived") * 100, 2).alias("SurvivalRate"))
            .orderBy("Pclass", "Embarked")
            .show()
        println("1st class passengers embarked from Cherbourg paid the highest fare")
        println("3rd class passengers embarked from Queenstown paid the lowest fare")
        println("For 1st & 2nd class passengers, the highest fare was paid by passengers embarked from Cherbourg, then Southampton and then Queenstown\n\n")


    }

}