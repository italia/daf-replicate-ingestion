import java.io.{File, FileReader}
import java.nio.CharBuffer

import conf.ConfParser
import polling.Polling
import rest.RestClientImpl

import scala.io.Source

object Service extends App{

  override def main(args: Array[String]): Unit = {
    val confParser = new ConfParser()
    val confPath = new File("service.conf").getCanonicalPath
    val fileContents = Source.fromFile(confPath).getLines().reduce((a, b) => a + "\n" + b)
    val conf = confParser.parse(fileContents)
    val client = new RestClientImpl()
    val polling = new Polling(conf, client)
    polling.run()
  }
}
