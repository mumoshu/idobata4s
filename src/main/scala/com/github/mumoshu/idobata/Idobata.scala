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
  type ID = Long

  val IdobataURL = "https://idobata.io"
  val IdobataPusherKey = "44ffe67af1c7035be764"
  val APIToken = apiToken

  val headers = Map("X-API-Token" -> Seq(APIToken), "User-Agent" -> Seq(s"hubot-idobata / v0.0.2"))
  val apiSeedRequest = url(s"${IdobataURL}/api/seed").setHeaders(headers)
  val messagesRequest = url(s"${IdobataURL}/api/messages").setHeaders(headers)
  val roomsRequest = url(s"${IdobataURL}/api/rooms").setHeaders(headers)
  val usersRequest = url(s"${IdobataURL}/api/users").setHeaders(headers)
  val botsRequest = url(s"${IdobataURL}/api/bots").setHeaders(headers)
  val organizationsRequest = url(s"${IdobataURL}/api/organizations").setHeaders(headers)

  def apiSeedAsString: Future[String] = Http(apiSeedRequest OK as.String)
  def apiSeedAsJson: Future[JValue] = Http(apiSeedRequest OK as.json4s.Json)

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
    Http(messagesRequest << Map("message[room_id]" -> roomId.toString, "message[source]" -> source) OK as.String)
  }

  def getOrganizations =
    Http(organizationsRequest OK as.String)

  def getOrganizations(organizationSlug: String) =
    Http(organizationsRequest <<? Map("organization_slug" -> organizationSlug) OK as.String)

  def getRooms(organizationSlug: String, roomName: String) = {
    Http(roomsRequest <<? Map("organization_slug" -> organizationSlug, "room_name" -> roomName) OK as.String)
  }

  def getRooms(ids: Traversable[ID]) = {
    Http(roomsRequest <<? Map("ids[]" -> ids.mkString(",")) OK as.String)
  }

  def getRoom(id: ID) = {
    Http(roomsRequest / id OK as.String)
  }

  def touchRoom(roomId: Int) = {
    Http((roomsRequest / roomId.toString).POST OK as.String)
  }

  def getUsers(ids: Traversable[ID]) =
    Http(usersRequest <<? Map("ids[]" -> ids.mkString(",")) OK as.String)

  def getUsers =
    Http(usersRequest OK as.String)

  def getUser(id: ID) =
    Http(usersRequest / id OK as.String)

  def getBots =
    Http(botsRequest OK as.String)

  def getBots(ids: Traversable[ID]) =
    Http(botsRequest <<? Map("ids[]" -> ids.mkString(",")) OK as.String)

  def getMessages(roomId: ID, olderThan: ID) =
    Http(messagesRequest <<? Map("room_id" -> roomId.toString, "older_than" -> olderThan.toString) OK as.String)

  def getMessages(ids: Traversable[ID]) =
    Http(messagesRequest <<? Map("ids[]" -> ids.mkString(",")) OK as.String)

  def deleteMessage(id: ID) =
    Http((messagesRequest / id DELETE) OK as.String)
}
