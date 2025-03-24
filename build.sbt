name := """Spark Titanic"""

version := "1.0"

Compile / run / fork := true

javaOptions += "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"

Compile / doc / scalacOptions ++= Seq("-groups", "-implicits", "-deprecation", "-Ywarn-dead-code", "-Ywarn-value-discard", "-Ywarn-unused" )

Test / parallelExecution := false

val sparkVersion = "3.3.0"

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.32.0",
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion
)


