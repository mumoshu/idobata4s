package com.github.mumoshu.idobata

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import com.typesafe.scalalogging.slf4j.Logging
import org.scalatest.time.{Millis, Seconds, Span}

class IdobataFunctionalSpec extends FlatSpec with Matchers with ScalaFutures with Logging {

  implicit override val patienceConfig =
    PatienceConfig(timeout =  Span(5, Seconds), interval = Span(1, Millis))

  "A Idobata" should "send messages to rooms" in {


    val idobata = Idobata()
    whenReady(idobata.getRooms("mumoshu", "testroom")) { rooms =>
      logger.info(rooms)
      rooms shouldNot be ("")
    }
    whenReady(idobata.sendMessage(1518, "Hi!")) { response =>
      logger.info(response)
    }
  }

}
