package com.yetu.oauth2provider.utils

import org.scalatest._

import scala.concurrent.{ Await, Future }

trait AsyncBeforeAndAfterEach extends SuiteMixin {
  this: Suite =>

  import scala.concurrent.duration._
  import scala.language.postfixOps

  protected def beforeEach(): Future[Unit] = Future.successful(Unit)

  protected def afterEach(): Future[Unit] = Future.successful(Unit)

  abstract protected override def runTest(testName: String, args: Args): Status = {

    var thrownException: Option[Throwable] = None

    val beforeFuture: Future[Unit] = beforeEach()
    Await.result(beforeFuture, 15 seconds)

    try {
      super.runTest(testName, args)
    } catch {
      case e: Exception =>
        thrownException = Some(e)
        FailedStatus
    } finally {
      try {

        val afterFuture = afterEach()
        Await.result(afterFuture, 15 seconds)

        thrownException match {
          case Some(e) => throw e
          case None    =>
        }
      } catch {
        case laterException: Exception =>
          thrownException match { // If both run and afterAll throw an exception, report the test exception
            case Some(earlierException) => throw earlierException
            case None                   => throw laterException
          }
      }
    }
  }
}
