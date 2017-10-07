package scala

import conf.ConfParser
import org.scalatest.{FlatSpec, Matchers}

class TestConfParser extends FlatSpec with Matchers{
  "Conf parser" should "parse correctly input string" in {
    val confParser = new ConfParser()
    val conf = confParser.parse("PATH=aaaa\nINTERVAL_MINUTES=1")
    conf.interval shouldEqual 1 * 60 * 1000 * 1000
    assert(conf.path.endsWith("/aaaa"))
  }
}
