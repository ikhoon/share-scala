import java.time.LocalDateTime
import java.util.Date

import cats.effect.IO

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import cats.implicits._

import scalaz.concurrent.Task

/**
  * Created by ikhoon on 20/08/2017.
  */
object applicative {

  trait Applicative[F[_]] {
    def pure[A](a: A): F[A]

    def ap[A, B](fa: F[A])(ff: F[A => B]): F[B] = {
      flatMap(fa)(a => map(ff)(_(a)))
    }

    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    def map[A, B](fa: F[A])(f: A => B): F[B] =
      ap(fa)(pure(f))

    def map2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = {
      val faab: F[A => (A, B)] = map(fb)(b => (a: A) => (a, b))
      val fab: F[(A, B)] = ap(fa)(faab)
      val fc: F[C] = ap(fab)(pure { case (a, b) => f(a, b) })
      fc
    }
  }

}
