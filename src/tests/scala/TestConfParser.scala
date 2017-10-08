package scala

import conf.ConfParser
import org.scalatest.{FlatSpec, Matchers}

class TestConfParser extends FlatSpec with Matchers{
  "Conf parser" should "parse correctly input string" in {
    val confParser = new ConfParser()
    val conf = confParser.parse("PATH=aaaa\nINTERVAL_MINUTES=1\nLATITUDE=4\nLONGITUDE=32")
    conf.interval shouldEqual 1 * 60 * 1000 * 1000
    assert(conf.path.endsWith("/aaaa"))
    conf.latitude shouldEqual 4
    conf.longitude shouldEqual 32
  }
}
