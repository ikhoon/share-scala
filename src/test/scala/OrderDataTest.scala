import com.twitter.util.Future
import org.scalatest.FunSuite

/**
  * Created by ikhoon on 2016. 9. 25..
  */
class OrderDataTest extends FunSuite {

  import OrderData._
  test("get order and shipping address") {
    val userId = 1L
    // 주문 리스트를 Future를 이용해서 담아 왔다.
    val ordersFuture: Future[Seq[Order]] = getOrder(userId)

    // 배송주소를 가져오자. map만 사용하겠다.(flatMap, monadtransformer를 사용할수 있다.)
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

    // 우리가 원하는 자료구조형은 Future[Seq[(Order, Address)]]이다.
    // 위의 Future[Seq[Future[Option[(Order, Address)]]]]를 원하는 형태로 바꾸어 보자.

    // step.1 Twitter Future의 collect나 ScalaFuture의 sequence를 이용하여 Seq[Future[A]]를 Future[List[A]]로 변경한다.
    val step1: Future[Future[Seq[Option[(Order, Address)]]]] = shippingAddress.map(seqFuture => Future.collect(seqFuture))
    // step.2 이제 중복된 future, Future[Future[A]]를 flatten 연산자를 이용해서 없앨수 있다.
    val step2: Future[Seq[Option[(Order, Address)]]] = step1.flatten
    // step.3 Seq[Option[A]] 또한 flatten연산자를 이용해서 Seq[A]로 변경할수 있다.
    // 그리고 최종적으로 우리가 원하는 데이터 구조형을 얻을수 있다.
    val step3: Future[Seq[(Order, Address)]] = step2.map(_.flatten)


  }
}
