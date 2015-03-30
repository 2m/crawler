import crawler._
import org.specs2

class TestCrawler extends Crawler {
  var result = ""
  def crawl = {
    navigateTo("https://www.google.com") {
      in(form having id("tsf")) {
        in(input having id("lst-ib")) {
          typeIn("bplawler")
        }
        in(input having name("btnK")) {
          click.==>
        }
      }
    }
    onCurrentPage {
      result = from(div having id("resultStats")).getTextContent

      forAll(div having xPath("""//ol[@id = "rso"]/li/div[@class = "vsc"]""")) {
        println(from(anchor having xPath("h3/a")).getTextContent)
      }
    }
  }
}

object SimpleCrawlerTest extends specs2.mutable.Specification {
  "Vanity Googling for bplawler" should {
    val c = new TestCrawler
    c.crawl
    "give me search results that start with 'About'" in {
      c.result must startWith("About")
    }
  }
}
