package com.github.mumoshu.idobata

import dispatch._, Defaults._
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.concurrent.ExecutionContext.global
import scala.util.Try

case class APISeed(version: Int, records: Records)
case class Records(bot: Bot)
case class Bot(id: Int, name: String, iconURL: String, apiToken: String, status: String, channelName: String)

case class Idobata(apiToken: String = "28df745bf9e7ea110de8e02d0d641e44") {
  val IdobataURL = "https://idobata.io"
  val IdobataPusherKey = "44ffe67af1c7035be764"
  val APIToken = apiToken

  val apiSeedRequest = url(s"${IdobataURL}/api/seed")
  val messagesRequest = url(s"${IdobataURL}/api/messages")
  val roomsRequest = url(s"${IdobataURL}/api/rooms")
  val usersRequest = url(s"${IdobataURL}/api/users")

  val headers = Map("X-API-Token" -> Seq(APIToken), "User-Agent" -> Seq(s"hubot-idobata / v0.0.2"))
  val apiSeedRequestWithHeaders = apiSeedRequest.setHeaders(headers)
  
  def apiSeedAsString: Future[String] = Http(apiSeedRequestWithHeaders OK as.String)
  def apiSeedAsJson: Future[JValue] = Http(apiSeedRequestWithHeaders OK as.json4s.Json)

  def apiSeed: Future[APISeed] = {
    implicit val fmts = DefaultFormats
    import org.json4s.Extraction.extract

    apiSeedAsJson map { json =>
      val bot = json \ "records" \ "bot"
      APISeed(
        extract[Int](json \ "version"),
        Records(
          Bot(
            id = extract[Int](bot \ "id"),
            name = extract[String](bot \ "name"),
            iconURL = extract[String](bot \ "icon_url"),
            apiToken = extract[String](bot \ "api_token"),
            status = extract[String](bot \ "status"),
            channelName = extract[String](bot \ "channel_name")
          )
        )
      )
    }
  }

  def sendMessage(roomId: Int, source: String) = {
    Http(messagesRequest.setHeaders(headers) << Map("message[room_id]" -> roomId.toString, "message[source]" -> source) OK as.String)
  }

  def getRooms(organizationSlug: String, roomName: String) = {
    Http(roomsRequest.setHeaders(headers) <<? Map("organization_slug" -> organizationSlug, "room_name" -> roomName) OK as.String)
  }

  def touchRoom(roomId: Int) = {
    Http((roomsRequest / roomId.toString).POST OK as.String)
  }
}
