import com.twitter.bijection.Bijection
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.{Await => ScalaAwait, Future => ScalaFuture}
import com.twitter.util.{Await => TwitterAwait, Future => TwitterFuture}

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by ikhoon on 2016. 9. 25..
  */
class BijectionTest extends FunSuite with Matchers {

  import com.twitter.bijection.twitter_util.UtilBijections._
  case class Hello(world: String)
  test("twitter future to scala future using bijection") {
    val scalaFuture: ScalaFuture[Hello] = Bijection[TwitterFuture[Hello], ScalaFuture[Hello]](TwitterFuture.value(Hello("World")))
    ScalaAwait.result(scalaFuture, Duration.Inf) shouldBe Hello("World")
  }

  test("scala future to twitter future using bijection") {
    case class Hello(world: String)
    val twitterFuture: TwitterFuture[Hello] = Bijection.invert[TwitterFuture[Hello], ScalaFuture[Hello]](ScalaFuture.successful(Hello("World!!!")))
    TwitterAwait.result(twitterFuture) shouldBe Hello("World!!!")
  }

  case class Foo(bar: String)
  import FutureOps._
  test("twitter future to scala future with syntax") {
    val scalaFuture = TwitterFuture.value(Foo("Bar")).toScalaFuture
    ScalaAwait.result(scalaFuture, Duration.Inf) shouldBe Foo("Bar")
  }

  test("scala future to twitter future with syntax") {
    val twitterFuture = ScalaFuture.successful(Foo("Bar")).toTwitterFuture
    TwitterAwait.result(twitterFuture) shouldBe Hello("World!!!")
  }
}
