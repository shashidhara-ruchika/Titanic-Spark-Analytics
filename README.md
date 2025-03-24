# Titanic-Spark-Analytics
# Spark Assignment 2

### How to Set Up Project:

Import JDK - coretto 18.9

Import Scala SDK - 2.13.6

### How to run: IntelliJ
1. Click on `Run / Debug Configurations`

2. Select `Edit Configurations`

3. Add VM Option:
```
--add-opens java.base/sun.nio.ch=ALL-UNNAMED 
```

4. Add Program Arguments:
   Add the relative path of the dataset (based on what the Working Directory is)
```
"src\main\resources\train.csv"
```
(OR)
Add the absolute path of the dataset. For example, on my machine its
```
"C:\Users\ruchi\Coursework\Big Data\Assignments\Titanic-Spark-Analytics\src\main\resources\train.csv"
```

5. Click on `Apply`

6. Exit

7. Click on `Run TitanicDatasetAnalyzer`


### How to run: Command Line

Run the program: TitanicDatasetAnalyzer by passing relative path of the dataset (train.csv)
```
sbt "run src/main/resources/train.csv"
```

Clean Project
```
sbt clean
```

### Assignment:

Url: https://www.kaggle.com/competitions/titanic/dataLinks to an external site.

For this assignment you will use the training and testing datasets.( train.csv & test.csv)

You are to load the dataset using Spark and perform the operations below:



Exploratory Data Analysis- Follow up on the previous spark assignment 1 and explain a few statistics. (20 pts)

Feature Engineering - Create new attributes that may be derived from the existing attributes. This may include removing certain columns in the dataset. (30 pts)

Prediction - Use the train.csv to train a Machine Learning model of your choice & test it on the test.csv. You are required to predict if the records in test.csv survived or not. Note( 1 = Survived, 0 = Dead) (50 pts)

Please note: Do not include the test.csv while training the model. Also do not use the 'Survived' column during training. Doing these would defeat the purpose of the entire model.



The classifier must have an accuracy of 70 % for 100 pts.

The submission must be on a new github repository or databricks public notebook along with the Assignment pdf. Please provide Screenshots of the results in the Assignment.pdf for submission.