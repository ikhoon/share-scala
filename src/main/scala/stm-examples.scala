import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future

object StmExamples extends StrictLogging {
  import scala.concurrent.stm._

  val urls = Ref[List[String]](Nil)
  val clen = Ref(0)


  def addUrls(url: String): Unit = atomic { implicit txn =>
    urls() = url :: urls()
    clen() = clen() + url.length + 1
    logger.info("addUrls: " + url)
  }

  def getUrlArray(): Array[Char] = atomic { implicit txn =>
    val array = new Array[Char](clen())
    for ((ch, i) <- urls().map(_ + "\n").flatten.zipWithIndex) {
      array(i) = ch
    }
    logger.info("array : " + array.mkString)
    array
  }

}

object AtomicHistorySTM extends App with StrictLogging {
  import StmExamples._
  import scala.concurrent.ExecutionContext.Implicits.global
  Future {
    addUrls("http://scala-lang.org")
    addUrls("https://github.com/scala/scala")
    addUrls("https://scala-lang.org/api")
  }
  Thread.sleep(25)
  Future {
    try { logger.info(s"sending ${getUrlArray().mkString}") }
    catch { case e: Exception => logger.error("Ayayay....", e)}
  }
  Thread.sleep(5000)
}