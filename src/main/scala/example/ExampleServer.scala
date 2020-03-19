package example

import java.net.InetSocketAddress

import uzhttp.Request.Method.GET
import uzhttp.server.Server
import uzhttp.{Request, Response}
import uzhttp.websocket.Frame
import zio.{App, Task, ZIO}

object ExampleServer extends App {

  override def run(args:  List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    Server.builder(new InetSocketAddress("0.0.0.0", 8000))
      .handleSome {
        case req if req.method == GET =>
          Response.fromResource("index.html", req).orDie // deliver a static file from an application resource
        case req if req.method == GET =>
          Response.fromResource(req.uri.toString, req).orDie // deliver a static file from an application resource
        case req if req.uri.toString == "/" =>
          Response.html("<html><body><h1>Hello world!</h1></body></html>").cached // deliver a constant HTML response
      }.serve.useForever.orDie
}