package example

import java.net.InetSocketAddress

import uzhttp.Request.Method.{GET, POST}
import uzhttp.{HTTPError, Request, Response}
import uzhttp.server.Server
import zio.{App, RIO, UIO, ZIO}

object ExampleServer extends App {

  def contentType(path: String): String =
    if (path.endsWith(".html")) "text/html"
    else if (path.endsWith(".css")) "text/css"
    else "application/octet-stream"

  def withHeader(h: (String, String)): Response =
    Response.const(Array.emptyByteArray, headers = List(h))

  val deleteUser = withHeader("Set-Cookie" -> "user=; Expires=Thu, 01 Jan 1970 00:00:00 GMT")

  def withUser(u: String): Response = withHeader("Set-Cookie" -> s"user=$u")


  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    Server.builder(new InetSocketAddress("0.0.0.0", 8000))
      .handleSome {
        case req if req.method == GET && req.uri.toString == "/" =>
          Response.fromResource("index.html", req, contentType = "text/html").orDie // deliver a static file from an application resource
        case req if req.method == GET => {
          val p = req.uri.getPath.drop(1)
          Response.fromResource(p, req, contentType = contentType(p)).orDie // deliver a static file from an application resource
        }
        case req if req.method == POST && req.uri.toString == "/tokensignin" => {
          req.body.fold[ZIO[zio.ZEnv, HTTPError, Response]](
            UIO(deleteUser)
          )(b => for {
              body <- b.runCollect
              userId = new String(body.toArray)
              user <- GoogleAuth.verify(userId).catchAll(_ => UIO(None))
              email = user.map(_.email)
            } yield email.fold(deleteUser)(withUser)
          )
        }
      }.serve.useForever.orDie
}
