package com.github.mumoshu.idobata

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import com.typesafe.scalalogging.slf4j.Logging
import org.scalatest.time.{Millis, Seconds, Span}
import scala.concurrent.Future

class IdobataFunctionalSpec extends FlatSpec with Matchers with ScalaFutures with Logging {

  implicit override val patienceConfig =
    PatienceConfig(timeout =  Span(5, Seconds), interval = Span(1, Millis))

  private val roomId = 1518
  private val userId = 1877
  private val botId = 1880

  "A Idobata" should "send messages to rooms" in {

    val idobata = Idobata()

    def test[A](future: Idobata => Future[A]) = whenReady(future(idobata)){ response =>
      logger.info(response.toString)
    }

    test(_.getRooms("mumoshu", "testroom"))
    test(_.getRoom(roomId))
    test(_.getRooms(Seq(roomId)))
    test(_.sendMessage(roomId, "Hi!"))
    test(_.getMessages(roomId, olderThan = 1513410))
    test(_.getMessages(Seq(1513410)))
    test(_.getUser(userId))
    test(_.getUsers(Seq(userId)))
    test(_.getBots(Seq(botId)))
  }

}
