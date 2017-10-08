package polling

import conf.Conf
import rest.RestClient


class Polling(conf: Conf, client: RestClient) extends Thread{

  override def run(): Unit = {
    while(true){
      //client.pullData()
    }
  }
}
