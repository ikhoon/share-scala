/**
  * Created by ikhoon on 2016. 3. 21..
  */
object Solution {

  def f(arr: List[Int]): List[Int] = {
    arr.foldLeft((1, List[Int]())) {
      case ((index, acc), item) if index % 2 == 0 => (index + 1, item :: acc)
      case ((index, acc), _) => (index + 1, acc)
    }._2.reverse
  }
}

object RunApp extends App {
  println(Solution.f(io.Source.stdin.getLines.toList.map(_.trim).map(_.toInt)).mkString("\n"))

}
