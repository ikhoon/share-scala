/**
  * Created by Liam.M(엄익훈) on 3/2/16.
  */
object Implicit extends App {
  // implicit

  implicit class IntOp(int: Int) {
    def to_s() = int.toString
    def toList() = List(int)
  }

  val a : Int = 10
  println(a.to_s())
  println(a.toList())
}
