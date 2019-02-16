/**
  * Created by ikhoon on 2016. 9. 25..
  */

import scala.concurrent.{Future => ScalaFuture}
import com.twitter.util.{Future => TwitterFuture}
import com.twitter.bijection.twitter_util.UtilBijections._
import com.twitter.bijection.Bijection
import scala.concurrent.ExecutionContext.Implicits.global

object FutureOps {
  implicit class RichTwitterFuture[A](future: TwitterFuture[A]) {
    def toScalaFuture: ScalaFuture[A] =
      Bijection[TwitterFuture[A], ScalaFuture[A]](future)
  }
  implicit class RichScalaFuture[A](future: ScalaFuture[A]) {
    def toTwitterFuture: TwitterFuture[A] =
      Bijection.invert[TwitterFuture[A], ScalaFuture[A]](future)
  }
}
