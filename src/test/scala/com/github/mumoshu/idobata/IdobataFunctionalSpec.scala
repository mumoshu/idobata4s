package com.github.mumoshu.idobata

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import com.typesafe.scalalogging.slf4j.Logging
import org.scalatest.time.{Millis, Seconds, Span}
import scala.concurrent.Future

class IdobataFunctionalSpec extends FlatSpec with Matchers with ScalaFutures with Logging {

  implicit override val patienceConfig =
    PatienceConfig(timeout =  Span(5, Seconds), interval = Span(1, Millis))

  "A Idobata" should "send messages to rooms" in {

    val idobata = Idobata()

    def test[A](future: Idobata => Future[A]) = whenReady(future(idobata)){ response =>
      logger.info(response.toString)
    }

    val roomId = 1518
    val userId = 1877
    val botId = 1880
    val organizationSlug = "mumoshu"
    val messageId = 1513410
    val roomName = "testroom"

    test(_.getRooms(organizationSlug, roomName))
    test(_.getRoom(roomId))
    test(_.getRooms(Seq(roomId)))
    test(_.sendMessage(roomId, "Hi!"))
    test(_.getMessages(roomId, olderThan = messageId))
    test(_.getMessages(Seq(messageId)))
    test(_.getUser(userId))
    test(_.getUsers(Seq(userId)))
    test(_.getUsers)
    test(_.getBots(Seq(botId)))
    test(_.getBots)
    test(_.getOrganizations)
    test(_.getOrganizations(organizationSlug))
  }

}
