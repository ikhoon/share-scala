import com.twitter.util.Future

/**
  * Created by ikhoon on 2016. 9. 25..
  */
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
  def getOrder(userId: Long): Future[List[Order]] =
    Future.value(orders.getOrElse(userId, Nil))

  def getAddress(orderId: Long): Future[Option[Address]] =
    Future.value(addresses.get(orderId))

}
