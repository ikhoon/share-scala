## Finatra 활용 팁(?)이라고 말하기에 민망한 소소한 이야기들

### 외부와 통신을 해야 한다면 finagle client를 최대한 활용하라.
Finatra는 finagle 위에서 동작하기 때문에 finagle의 강력한 기능을 자연스럽게 쓸수 있다.

Finatra의 http client를 사용해보았다면 이미 finagle http client를 사용한 것이다.

아래 코드 처럼 finatra http client는 finagle의 http client를 그대로 활용한다.
```scala
package com.twitter.finatra.httpclient

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}

object RichHttpClient {

  /* Public */

  def newClientService(dest: String): Service[Request, Response] = {
    Http.newClient(dest).toService
  }

  def newSslClientService(sslHostname: String, dest: String): Service[Request, Response] = {
    Http.client.withTls(sslHostname).newService(dest)
  }
}
```

참조: https://github.com/twitter/finatra/blob/develop/httpclient/src/main/scala/com/twitter/finatra/httpclient/RichHttpClient.scala

> 이외 thrift, mysql, memcached, redis, spdy, http2, zipkin등 [다양한 프로토콜](https://github.com/twitter/finagle)을 활용할수 있다.

#### finagle을 사용함으로 인해 얻을수 있는 이득은
* finagle은 높은 성능과 동시성을 위해 설계 되었다, 적은 수의 vm, cpu, memory로 고성능을 낼수 있다.
* finagle은 **무조건**:sunglasses: NonBlocking I/O이다. 모든 finagle의 client들은 Blocking I/O를 사용하고 있지 않는다. 굿이다
> Finagle implements uniform client and server APIs for several protocols, and is designed for high performance and concurrency.

* Blocking IO인 JDBC도 netty를 활용하여 비동기로 구현하여 finagle-mysql를 활용하면 mysql 서버와 비동기로 통신할수 있다. :blush:
* Blocking IO는 최대한 피하라. Blocking IO 정말 어쩔수 없이 사용해야한다면 별도의 thread pool(execution context)를 만들어서 사용하기 권장한다.
* JDBC 이외에는 프로토콜의 Nonblocking I/O 구현 라이브러리는 쉽게 찾을수 있고 finagle은 이런 프로토콜의 상당수 이미 구현해놓고 있다.
* circuit breaker를 제공해준다. - 장애 대응, failover
* twitter server admin 페이지를 활용해서 down stream을 모니터링 할수 있다. - 모니터링, 에러감지
* open zipkin 을 활용하여 distributed tracing이 가능하다. - 분산 시스템의 성능 튜닝, 병목현상 감지

### 200 vs. core + 1
다양한 장점을 가진 Finagle을 잘 활용하려면 비동기 프로그래밍에 익숙해져야 한다.
* 동기화 서버인 Tomcat은 기본 thread가 **200**이다.
* 비동기 서버는 client의 요청을 처리하기 위해서 많은 수의 thread를 생성하지 않는다. 기본 설정은 **core수 + 1**개의 thread만 생성한다.
* 동기화 서버보다 훨씬 적은 수의 thread이지만 Non blocking I/O + Async만 사용하면 이값이 최적의 값이 될것이다.

### Twitter, 그들만의 세상
Twitter의 scala OSS(open source software)는 오랫동안 개발되었고 안정적이며
또한 그 영역이 방대하고 많다.
twitter에서 만드는 OSS는 그들이 기존에 만들었던 OSS에 의존성을 가지는 경우가 대부분이다.
그로 인해서 twitter만의 특화된 자료구조형을 사용하는 경우가 많다. 이로인해 생기는 불편함도 있다.

#### Twitter Future
Twitter는 scala에서 제공해주는 `scala.concurrent.Future`를 사용하지 않고 별도로 구현한 [twitter/util](https://github.com/twitter/util)에서 제공하는 자료 구조형, `com.twitter.util.Future`를 표준으로 사용한다.

Finatra에서는 future를 반환하면 그 값을 onSuccess가 되는 시점에 rendering을 해줌으로서 쉽게 비동기 프로그래밍을 할수 있게 도와준다.

```scala
import com.twitter.finatra.http._
@Singleton
class ExampleController extends Controller {
  get("/twitter/future") { request: Request =>
    import com.twitter.util.Future
    // future를 반환해도 내부의 value "hello world"가 출력된다.
    Future.value("<h1>Hello, world!</h1>")
  }

  get("/scala/future") { request: Request =>
    import scala.concurrent.Future
    // future를 반환하지만 scala의 future이기 때문에 객체의 주소값이 출력이 된다.
    Future.successful("<h1>Hello, world!</h1>")
  }

}
```

#### Scala Future to Twitter Future
많이 사용하는 FRM database access library인 [slick](http://slick.lightbend.com/)의 경우 scala future를 반환한다.
Future안에 있는 데이터를 finatra에서 rendering을 위해서는 다시 twitter future로 변환해야한다.

이를 위해서 [twitter/bijection](https://github.com/twitter/bijection/blob/develop/bijection-util/src/main/scala/com/twitter/bijection/twitter_util/UtilBijections.scala#L68-L92)를 사용하면 된다.

```scala
import com.twitter.bijection.Bijection
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.{Await => ScalaAwait, Future => ScalaFuture}
import com.twitter.util.{Await => TwitterAwait, Future => TwitterFuture}

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class BijectionTest extends FunSuite with Matchers {

  import com.twitter.bijection.twitter_util.UtilBijections._
  case class Hello(world: String)
  test("twitter future to scala future") {
    val scalaFuture: ScalaFuture[Hello] = Bijection[TwitterFuture[Hello], ScalaFuture[Hello]](TwitterFuture.value(Hello("World")))
    ScalaAwait.result(scalaFuture, Duration.Inf) shouldBe Hello("World")
  }

  test("scala future to twitter future") {
    case class Hello(world: String)
    val scalaFuture: TwitterFuture[Hello] = Bijection.invert[TwitterFuture[Hello], ScalaFuture[Hello]](ScalaFuture.successful(Hello("World!!!")))
    TwitterAwait.result(scalaFuture) shouldBe Hello("World!!!")
  }
}

```

하지만 bijection api를 일일이 사용하는건 귀찮다. 조금더 재활용 가능한 코드로 바꾸어보자.
scala의 implicit class를 활용해서 `toScalaFuture`, `toTwitterFuture` 함수를 추가해 본다.

```scala
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
```

이제 위의 `Bijection`을 이용한 코드는 syntax를 활용한 코드로 바꾸어 사용할수 있다.

```scala
class BijectionTest extends FunSuite with Matchers {
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
```
FutureOps만 import 하면 scala future와 twitter future 사이에 자유롭게 변환이 가능한다.

### Future[List[Future[Option[A]]]]
Monad 자료 구조형 그중에서도 `Future`에 대해서 잘 다룰수 있어야 한다.
조금만 익숙해지면 어렵지 않다. 아니 오히려 쉽다.

아마 자바에서 비동기 프로그래밍을 구현하는것 보다 100배는 쉬울것이다.

비동기 프로그램을 구현하다 보면 모나드 클래스가 중첩되어 복잡한 nested 자료 구조형을 가지게 될수 있다.
* 중첩된 같은 monad `M[M[A]`는 `flatten`을 통해서 `M[A]`로 변경할 수 있다.
* 중첩된 다른 monad `F[G[A]]`는 `sequence`를 통해서 `G[F[A]]`로 변경될수 있다.

간단한 예를 들어 보겠다.

아래 코드는 특정 유저의 주문목록과 주문에 해당하는 주소를 알수 있는 간단한 API로 구성되어 있다.
```scala
object OrderData {

  // define data type
  case class Order(id: Long, userId: Long, name: String)
  case class Address(id: Long, orderId: Long, city: String)

  // mock data
  val orders = Map[Long, List[Order]](
    1L -> List(Order(1, 1, "사과"), Order(2, 1, "배"), Order(3, 1, "귤"))
  )
  val addresses = Map[Long, Address](
    1L -> Address(1, 1, "서울"),
    2L -> Address(1, 2, "대구"),
    3L -> Address(1, 3, "부산")
  )

  // access data asynchronous
  def getOrder(userId: Long): Future[Seq[Order]] =
    Future.value(orders.getOrElse(userId, Nil))

  def getAddress(orderId: Long): Future[Option[Address]] =
    Future.value(addresses.get(orderId))

}
```

이 코드를 이용해서 비동기로 데이터를 가져오는 코드를 만들어 보겠다.
```scala
val userId = 1L
// 주문 리스트를 Future를 이용해서 담아 왔다.
val ordersFuture: Future[Seq[Order]] = getOrder(userId)

// 배송주소를 가져오자. map만 사용하겠다.
val shippingAddress: Future[Seq[Future[Option[(Order, Address)]]]] =
  ordersFuture map { // future를 벗겨낸다.
    orders => orders map { order => // 리스트를 벗겨낸다.
      getAddress(order.id) map { mayBeAddress => // 배송 주소를 Future에 담아서 가져온다.
        mayBeAddress map { // 주문과 주소를 같이 반환한다.(주소가 있는 주문만이 유효한 주문이다.)
          address => (order, address)
        }
      }
    }
  }
```

shippingAddress의 자료구조형이 `Future[Seq[Future[Option[(Order, Address)]]]]`이다.

**복잡하다.**

단순화 해보자.
우리가 원하는 자료구조형은 Future[Seq[(Order, Address)]]이다.
위의 Future[Seq[Future[Option[(Order, Address)]]]]를 차근 차근 바꾸어 보자.

* **Step.1** Twitter Future의 collect나 ScalaFuture의 sequence를 이용하여 Seq[Future[A]]를 Future[List[A]]로 변경한다.
```scala
val step1: Future[Future[Seq[Option[(Order, Address)]]]] = shippingAddress.map(seqFuture => Future.collect(seqFuture))
```

* **Step.2** 이제 중복된 future, Future[Future[A]]를 flatten 연산자를 이용해서 없앨수 있다.
```scala
val step2: Future[Seq[Option[(Order, Address)]]] = step1.flatten
```

* **Step.3** Seq[Option[A]] 또한 flatten연산자를 이용해서 Seq[A]로 변경할수 있다.
그리고 최종적으로 우리가 원하는 데이터 구조형을 얻을수 있다.
```scala
val step3: Future[Seq[(Order, Address)]] = step2.map(_.flatten)
```

map을 이용해서 데이터를 가져오는 과정 부터 이를 다시 재조립하는 과정까지 복잡헤 보인다.

이는
* flatMap
* for comprehension
* monad transformer

활용하면 손쉽게 제어할수 있다.

이부분은 본 글의 범위를 벗어난다 생각되어 생략하겠습니다.

감사합니다.

## 이상~ 끝
