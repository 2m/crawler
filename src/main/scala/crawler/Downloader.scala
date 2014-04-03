package crawler

import org.apache.http.{Header, HttpResponse, HttpRequest, HttpEntity}
import org.apache.http.impl.client.{DefaultRedirectStrategy, DefaultHttpClient}
import org.apache.http.client.methods.{HttpHead, HttpRequestBase, HttpGet}
import org.apache.http.cookie.Cookie
import javax.xml.bind.DatatypeConverter
import org.apache.http.client.params.HttpClientParams
import org.apache.http.protocol.HttpContext
import java.net.URI

/**
 * Created with IntelliJ IDEA.
 * User: arturas
 * Date: 8/16/13
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
object Downloader {
  /**
   * Downloads given url via GET and returns response entity.
   *
   * Import `crawler._` and use `download(url).getContent.getBytes` to get
   * actual content bytes.
   */
  def download(url: String, cookies: Set[Cookie] = Set.empty,
    credentials: Option[(String, String)] = None): HttpEntity = {
    val http = new DefaultHttpClient()

    val cookieStore = http.getCookieStore
    cookies.foreach(cookieStore.addCookie)

    val request = new HttpGet(url)

    /**
     * This is interesting.
     * Apache's HttpClient sets original headers to the new request after
     * original gets redirected. This is a problem when downloading files from
     * bitbucket.
     *
     * This is wrong in the following situation:
     * 1. We send a request to download a file to bitbucket.com with
     *    'Authorization' header.
     * 2. Bitbucket returns response which is a redirect to
     *    'bbuseruploads.s3.amazonaws.com/.../?Signature=UjARZb9...'
     * 3. If we access new URL with 'Authorization' headers, we get an error
     *    from 'bbuseruploads.s3.amazonaws.com' saying that only one of auth
     *    methods (Authorization header, or Signature query param) can be
     *    specified.
     *
     * Header copying logic is in DefaultRequestDirector.handleResponse()
     * method line 1035. We can't really redefine DefaultRequestDirector
     * because that method has too much code.
     *
     * So I chose to redefine DefaultRedirectStrategy.getRedirect method
     * which returns a redirect object which rejects 'Authorization' headers
     * to be set by anyone.
     */
    http.setRedirectStrategy(new DefaultRedirectStrategy() {
      override def getRedirect
      (request: HttpRequest, response: HttpResponse, context: HttpContext): HttpRequestBase = {
        val uri: URI = getLocationURI(request, response, context)
        val method: String = request.getRequestLine.getMethod
        if (method.equalsIgnoreCase(HttpHead.METHOD_NAME)) {
          new HttpHead(uri) {
            override def setHeaders(headers: Array[Header]) {
              super.setHeaders(headers.filterNot(_.getName == "Authorization"))
            }
          }
        }
        else {
          new HttpGet(uri) {
            override def setHeaders(headers: Array[Header]) {
              super.setHeaders(headers.filterNot(_.getName == "Authorization"))
            }
          }
        }
      }
    })

    credentials.foreach { case (username, password) =>
      val auth =
        DatatypeConverter.printBase64Binary(s"$username:$password".getBytes())
      request.setHeader("Authorization", "Basic " + auth)
      request.getAllHeaders
    }

    val response = http.execute(request)
    response.getEntity
  }
}
