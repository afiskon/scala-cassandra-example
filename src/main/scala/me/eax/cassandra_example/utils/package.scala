package me.eax.cassandra_example

import com.datastax.driver.core._
import com.google.common.util.concurrent._
import scala.concurrent._

package object utils {

  implicit def cassandraFutureToScalaFuture(future: ResultSetFuture): Future[ResultSet] = {
    val promise = Promise[ResultSet]()

    val callback = new FutureCallback[ResultSet] {
      def onSuccess(result: ResultSet): Unit = {
        promise success result
      }

      def onFailure(err: Throwable): Unit = {
        promise failure err
      }
    }

    Futures.addCallback(future, callback)
    promise.future
  }

  def ftraverse[A, B](xs: Seq[A])(f: A => Future[B])(implicit ec: ExecutionContext): Future[Seq[B]] = {
    if(xs.isEmpty) Future successful Seq.empty[B]
    else f(xs.head) flatMap { fh => ftraverse(xs.tail)(f) map (r => fh +: r) }
  }
}
