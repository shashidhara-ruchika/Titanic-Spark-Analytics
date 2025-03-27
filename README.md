# Titanic-Spark-Analytics
# Spark Assignment 2

## Exploratory Data Analysis Performed:

## Feature Engineering Performed:
- Data Preprocessing is performed in titanic_ml.TitanicDataPreprocessor
- Columns Dropped: Name, Ticket, Cabin, Embarked
- Columns Populated with Categorical Index: SexIndex (from Sex column)
- New Column introduced: FamilySize (SibSp + Parch + 1)
- Final Columns used for training: PClass, SexIndex, Age, Fare, FamilySize

## Prediction Model: Logistic Regression
- Classification Column: Survived

## Metrics:

#### Accuracy on Test Data
- Accuracy: 0.937799043062201
- Precision: 0.9380165289256198
- Recall: 0.937799043062201
- F1 Score: 0.9378849538671958

#### Accuracy on Train Data
- Accuracy: 0.8035914702581369
- Precision: 0.8020835948549287
- Recall: 0.803591470258137
- F1 Score: 0.8024560737183835

## How to Set Up Project:
- Import JDK - coretto 18.9
- Import Scala SDK - 2.13.6

## How to run: IntelliJ
1. Click on `Run / Debug Configurations`

2. Select `Edit Configurations`

3. Add VM Option to All files:
```
--add-opens java.base/java.nio=ALL-UNNAMED 
--add-opens java.base/sun.nio.ch=ALL-UNNAMED 
--add-opens java.base/java.util=ALL-UNNAMED 
--add-opens java.base/java.lang.invoke=ALL-UNNAMED 
```

4. Add Program Arguments:
   Add the relative path of the dataset (based on what the Working Directory is) (OR) Add the absolute path of the dataset.


TitanicDatasetAnalyzer
```
src\main\resources\train.csv
```

TitanicTrain
```
titanic_ml.TitanicTrain models\lg src\main\resources\train.csv
```

TitanicPredict
```
models\lg src\main\resources\test.csv predictions\test
```

TitanicEvaluator
```
src\main\resources\gender_submission.csv predictions\test
```

5. Click on `Apply`

6. Exit

7. Click on `Run TitanicDatasetAnalyzer`


## How to run: Command Line

Run Exploratory Data Analysis:
```
sbt "runMain titanic_eda.TitanicDatasetAnalyzer src\main\resources\train.csv"
```

Train the Model (Usage: TitanicTrain <model_output_path> <train_file>):
```
 sbt "runMain titanic_ml.TitanicTrain models\lg src\main\resources\train.csv" 
```

Test the Model on Test Data (Usage: TitanicPredict <model_input_path> <test_file> <predictions_output_path>):
```
 sbt "runMain titanic_ml.TitanicPredict models\lg src\main\resources\test.csv predictions\test"
```

Test the Model on Train Data (Usage: TitanicPredict <model_input_path> <train_file> <predictions_output_path>):
```
 sbt "runMain titanic_ml.TitanicPredict models\lg src\main\resources\train.csv predictions\train"
```

Evaluate the Model on Predicted Test Data (Usage: TitanicTrain <actual_data_path> <predicted_data_path>):
```
sbt "runMain titanic_ml.TitanicEvaluator src\main\resources\gender_submission.csv predictions\test"
```

Evaluate the Model on Predicted Train Data (Usage: TitanicTrain <actual_data_path> <predicted_data_path>):
```
sbt "runMain titanic_ml.TitanicEvaluator src\main\resources\train.csv predictions\train"
```

Clean Project
```
sbt clean
```

## Assignment:

Url: https://www.kaggle.com/competitions/titanic/data Links to an external site.

For this assignment you will use the training and testing datasets.( train.csv & test.csv)

You are to load the dataset using Spark and perform the operations below:



Exploratory Data Analysis- Follow up on the previous spark assignment 1 and explain a few statistics. (20 pts)

Feature Engineering - Create new attributes that may be derived from the existing attributes. This may include removing certain columns in the dataset. (30 pts)

Prediction - Use the train.csv to train a Machine Learning model of your choice & test it on the test.csv. You are required to predict if the records in test.csv survived or not. Note( 1 = Survived, 0 = Dead) (50 pts)

Please note: Do not include the test.csv while training the model. Also do not use the 'Survived' column during training. Doing these would defeat the purpose of the entire model.



The classifier must have an accuracy of 70 % for 100 pts.

The submission must be on a new github repository or databricks public notebook along with the Assignment pdf. Please provide Screenshots of the results in the Assignment.pdf for submission.