package example

import java.util.Collections

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import zio.blocking.Blocking
import zio.{RIO, blocking}

object GoogleAuth {

  case class GoogleUser(email: String)

  val verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
    // Specify the CLIENT_ID of the app that accesses the backend:
    .setAudience(Collections.singletonList("477400905880-35lan5cf4rvc039pqdobn7qgitn3i2rl.apps.googleusercontent.com"))
    // Or, if multiple clients access the backend:
    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
    .build();

  def verify(token: String): RIO[Blocking, Option[GoogleUser]] =
    blocking.effectBlocking(verifier.verify(token)).map(
      Option(_).map { t =>
        val payload = t.getPayload
        // Print user identifier
        val userId = payload.getSubject
        System.out.println("User ID: " + userId)
        // Get profile information from payload
        val email = payload.getEmail
        val name = payload.get("name").asInstanceOf[String]
        val pictureUrl = payload.get("picture").asInstanceOf[String]
        val locale = payload.get("locale").asInstanceOf[String]
        val familyName = payload.get("family_name").asInstanceOf[String]
        val givenName = payload.get("given_name").asInstanceOf[String]
        GoogleUser(email)
    })



}
