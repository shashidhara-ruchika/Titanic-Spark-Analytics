# Titanic-Spark-Analytics
# Spark Assignment 2


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
"spark-titanic\src\main\resources\train.csv"
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