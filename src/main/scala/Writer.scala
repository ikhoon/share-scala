import cats.Semigroup
import cats.implicits._

/**
  * Created by ikhoon on 14/08/2017.
  */
case class Writer[L, A](run: (L, A)) {

  def map[B](f: A => B): Writer[L, B] =
    Writer(run._1, f(run._2))

  def flatMap[B](f: A => Writer[L, B])(implicit semigroup: Semigroup[L]): Writer[L, B] = {
    val r2 = f(run._2).run
    Writer(semigroup.combine(r2._1, run._1), r2._2)
  }


}

object WriterTest {
  def main(args: Array[String]): Unit = {
    val unit: Writer[List[String], String] = for {
      int <- Writer(List(s"초기값은 ${10} 이다."), 10)
      str <- Writer(List(s"정수(${10})를 문자열로 변환한다."), int.toString)
      replicate <- Writer(List(s"문자열을 3개 복제한다. "), str * 3)
    } yield replicate
    val (log, value) = unit.run
    println(s"result value is : $value")
    log.foreach(println)
  }
}

case class State[S, A](run: S => (S, A)) {

  def map[B](f: A => B): State[S, B] =
    State(s1 => {
      val (s2, a) = run(s1)
      (s2, f(a))
    })

  def flatMap[B](f: A => State[S, B]): State[S, B] =
    State(s1 => {
      val (s2, a) = run(s1)
      val value: State[S, B] = f(a)
      f(a).run(s2)
    })

}

// state를 사용한 예제는 http://eed3si9n.com/herding-cats/State.html 를 참고함
object StateTest {
  def main(args: Array[String]): Unit = {
    type Stack = List[Int]
    def push(a: Int): State[Stack, Unit] = State {
      case xs => (a :: xs, ())
    }
    val pop: State[Stack, Int] = State {
      case x :: xs => (xs, x)
      case Nil => throw new IllegalStateException("스택이 비었습니다.")
    }

    def stackManip: State[Stack, Int] = for {
      _ <- push(10)
      a <- pop
      b <- pop
    } yield b
    val initial = List(1, 2, 3, 4, 5)
    val result = stackManip.run(initial)
    println(result)


  }
}
