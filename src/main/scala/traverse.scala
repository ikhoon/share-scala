import cats.{Applicative, Apply, Eval, Traverse}
import cats.implicits._
import cats.instances.future
import cats.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
/**
  * Created by ikhoon on 20/08/2017.
  */
object traverse {

  case class Foo[A](a: A) {
    def foldLeft[B](b: B)(f: (B, A) => B): B = f(b, a)
    def foldRight[B](lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = f(a, lb)
    def traverse[G[_], B](f: A => G[B])(implicit G: Applicative[G]): G[Foo[B]] = {
      G.map(f(a))(Foo(_))
    }
  }
  case class Bar[A](a: A){
    def foldLeft[B](b: B)(f: (B, A) => B): B = f(b, a)
    def foldRight[B](lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = f(a, lb)
    def traverse[G[_], B](f: A => G[B])(implicit G: Applicative[G]): G[Bar[B]] = {
      G.map(f(a))(Bar(_))
    }
  }

  def main(args: Array[String]): Unit = {

    val a = Future { 10 }
    val b = Future { 20 }
    val ff: Future[Int => Int] = Future { (_: Int) + 20 }

    val foo: Foo[Int] = Foo(10)
    implicit val fooTraverse = new Traverse[Foo] {
      override def traverse[G[_]: Applicative, A, B](fa: Foo[A])(f: (A) => G[B]): G[Foo[B]] = fa traverse f

      override def foldLeft[A, B](fa: Foo[A], b: B)(f: (B, A) => B): B = fa.foldLeft(b)(f)

      override def foldRight[A, B](fa: Foo[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = fa.foldRight(lb)(f)
    }
    implicit val barTraverse = new Traverse[Bar] {
      override def traverse[G[_]: Applicative, A, B](fa: Bar[A])(f: (A) => G[B]): G[Bar[B]] = fa traverse f

      override def foldLeft[A, B](fa: Bar[A], b: B)(f: (B, A) => B) : B = fa.foldLeft(b)(f)

      override def foldRight[A, B](fa: Bar[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = fa.foldRight(lb)(f)
    }

    implicit val barApplicative = new Applicative[Bar] {
      override def pure[A](x: A): Bar[A] = Bar(x)

      override def map[A, B](fa: Bar[A])(f: A => B): Bar[B] =
        ap(pure(f))(fa)

      override def ap[A, B](ff: Bar[A => B])(fa: Bar[A]): Bar[B] = Bar(ff.a(fa.a))
    }
    val list: Bar[Foo[Int]] = Traverse[Foo].traverse[Bar, Int, Int](foo)(int => Bar(int))

    println(list)
  }
}
