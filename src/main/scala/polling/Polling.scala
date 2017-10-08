package polling

import java.io.{File, OutputStreamWriter}

import api_calls.Point.GPSPoint
import api_calls.ServicesQuery
import conf.Conf
import json2csv.{JsonToCsv, JsonToCsvImpl}
import rest.RestClient
import java.io.FileWriter

import scala.util.parsing.json.JSON


class Polling(conf: Conf, client: RestClient) extends Runnable{
  val servicesQuery = new ServicesQuery
  val jsonToCsv = new JsonToCsvImpl
  val gpsPoint = new GPSPoint(conf.latitude, conf.longitude)
  val file = new File(conf.path)
  override def run(): Unit = {
    var fw : FileWriter = null
    while(true){
      val query = servicesQuery.build(gpsPoint, conf.maxDists, conf.maxResults)
      val response = client.pullData(query)
      val jsonData = JSON.parseFull(response).get.asInstanceOf[Map[String, _]]
      val csvData = jsonToCsv.trasform(jsonData)
      val file = new File(conf.path).getCanonicalFile
      if(!file.exists()){
        fw = new FileWriter(file, true)
        fw.write(jsonToCsv.fields)
        fw.flush()
      }
      fw = new FileWriter(file, true)
      fw.write(csvData)
      Thread.sleep(conf.interval / 1000)
    }
  }
}
