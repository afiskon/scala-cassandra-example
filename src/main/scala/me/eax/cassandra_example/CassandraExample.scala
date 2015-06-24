package me.eax.cassandra_example

import com.datastax.driver.core._
import me.eax.cassandra_example.dao._
import me.eax.cassandra_example.utils._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object CassandraExample extends App {
  val cluster = {
    Cluster.builder()
      .addContactPoint("10.110.0.10")
      // .withCredentials("username", "password")
      .build()
  }

  val session = cluster.connect("test")
  val todoDao = TodoDAO(session)

  val f = {
    for {
      _ <- todoDao.createTable
      _ = println("Inserting items")
      _ <- {
        ftraverse((1 to 3).toSeq) { n =>
          val item = TodoDTO(n, s"Todo item $n")
          todoDao.insert(item)
        }
      }
      items <- todoDao.select
      _ = println(s"Items: $items")
      _ = println("Deleting item 2")
      _ <- todoDao.delete(2)
      newItems <- todoDao.select
      _ = println(s"New items: $newItems")
      _ <- todoDao.dropTable
    } yield {}
  }

  f onFailure { case e =>
    println(s"ERROR: $e")
    e.printStackTrace()
  }

  Await.ready(f, Duration.Inf)
  cluster.close()
  println("Done!")

}
