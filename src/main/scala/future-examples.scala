import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.async.Async.{async, await}

/**
 * Created by liamm on 7/6/15.
 */
object FutureExample extends App {

  import FutureLogic._
  import FutureData._

  /**
   *  future와 Await#result를 이용한 procedural 스타일의 프로그래밍
   *
   */

  val timeout = 5 second

  val user1IdFuture = getUserId(user_1_session)
  val user1Id = Await.result(user1IdFuture, timeout)
  val user1OrderFuture = getFirstOrders(user1Id)
  val user1Order = Await.result(user1IdFuture, timeout)
  val user1OrderItemsFuture = getOrderItems(user1Order)
  val user1OrderItems = Await.result(user1OrderItemsFuture, timeout)

  println(s"## user1 Order Items : ${user1OrderItems}")

  /**
   * flatMap을 이용한 3개의 future를 compostion해서 결과를 얻을수 있다.
   */

  val user2OrderItemsFuture : Future[List[Int]] =
    getUserId(user_2_session).flatMap { case userId =>
      getFirstOrders(userId).flatMap { case order =>
        getOrderItems(order)
    }
  }
  user2OrderItemsFuture.foreach { orderItems =>
    println(s"## user2 Order Items : ${orderItems}")
  }

  /**
   * for comprehension 이용한 3개의 future 의 composition 으로 결과를 얻을수 있다.
   */

  val user3OrderItemsFuture = for {
    userId <- getUserId(user_3_session)
    order <- getFirstOrders(userId)
    orderItems <- getOrderItems(order)
  } yield orderItems

  user3OrderItemsFuture.map {orderItems =>
    println(s"## user3 Order Items : ${orderItems}")
  }

  /**
    * scala의 async와 await를 이용하면 더욱더 쉬운 비동기 프로그래밍을 할수 있다.
    */
  async {
    val user4Id = await(getUserId(user_4_session))
    val order4 = await(getFirstOrders(user4Id))
    val orderItems4 = await(getOrderItems(order4))
    println(s"## user4 Order Items : ${orderItems4}")
  }
  Thread.sleep(5000)
}

object FutureLogic extends StrictLogging{
  import FutureData._

  def getUserId(session: String) = Future {
    logger.debug(s"# Start  : GET userId from session : ${session}")
    Thread.sleep(1000)
    logger.debug(s"# Finish : GET userId from session : ${session}")
    sessions.getOrElse(session, 0)
  }

  def getFirstOrders(userId: Int) = Future {
    logger.debug(s"# Start  : GET orders by userId : ${userId}")
    Thread.sleep(1000)
    logger.debug(s"# Finish : GET orders by userId : ${userId}")
    orders.getOrElse(userId, List.empty).head
  }

  def getOrderItems(orderId: Int): Future[List[Int]] = Future {
    logger.debug(s"# Start  : GET order items by orderId : ${orderId}")
    Thread.sleep(1000)
    logger.debug(s"# Finish : GET order items by orderId : ${orderId}")
    orderItems.getOrElse(orderId, List.empty)
  }

}
object FutureData {
  val user_1_session = "user-1-session-key"
  val user_2_session = "user-2-session-key"
  val user_3_session = "user-3-session-key"
  val user_4_session = "user-4-session-key"

  val sessions = Map(
    user_1_session -> 1,
    user_2_session -> 2,
    user_3_session -> 3,
    user_4_session -> 4
  )


  val orders = Map(
    1 -> List(1, 2, 3),
    2 -> List(4, 5, 6),
    3 -> List(7, 8, 9),
    4 -> List(10, 11, 12)
  )

  val orderItems = Map(
    1 -> List(11, 12, 13),
    2 -> List(21, 22, 23),
    3 -> List(31, 32, 33),
    4 -> List(41, 42, 43),
    5 -> List(51, 52, 53),
    6 -> List(61, 62, 63),
    7 -> List(71, 72, 73),
    8 -> List(81, 82, 83),
    9 -> List(91, 92, 93),
    10 -> List(171, 172, 173),
    11 -> List(181, 182, 183),
    11 -> List(191, 192, 193)
  )

}
