name := "scala-cassandra-example"

version := "0.1"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-Xmax-classfile-name", "100")

libraryDependencies ++= Seq(
    "com.datastax.cassandra" % "cassandra-driver-core" % "2.1.6"
  )

