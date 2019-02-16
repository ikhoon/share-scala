import java.text.SimpleDateFormat
import java.time.{Instant, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.util.Date

import cats.Monad
import cats.effect._
import cats.effect.implicits._
import util.log

import scala.concurrent.{Await, Awaitable, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import model._
import cats.implicits._
import cats._

/**
  * Created by ikhoon on 19/08/2017.
  */


object util {
  val formatter = new SimpleDateFormat("HH:mm:ss")
  def log(str: => String): Unit =
    println(s"${formatter.format(new Date())} - $str")

}
object model {
  case class Item(id: Int, catalogId: Int, brandId: Int)
  type Catalog = String
  type Brand = String
  type Wish = String
  type Category = String
  type Detail = String
  type Certification = String
}

sealed trait AsyncApiComponent[AsyncIO[+_]] {

  import model._

  def itemRepository: ItemRepository
  def catalogRepository: CatalogRepository
  def brandRepository: BrandRepository
  def itemWishCountRepository: ItemWishCountRepository
  def categoryRepository: CategoryRepository
  def itemDetailRepository: ItemDetailRepository
  def itemCertificationRepository: ItemCertificationRepository


  trait ItemRepository {
    def findById(id: Int): AsyncIO[Item]
  }

  trait CatalogRepository {
    def findById(id: Int): AsyncIO[Catalog]
  }

  trait BrandRepository {
    def findById(id: Int): AsyncIO[Brand]
  }

  trait ItemWishCountRepository {
    def findByItemId(id: Int): AsyncIO[Wish]
  }

  trait CategoryRepository {
    def findOneByBrandId(id: Int): AsyncIO[Category]
  }

  trait ItemDetailRepository {
    def findByItemId(id: Int): AsyncIO[Detail]
  }

  trait ItemCertificationRepository {
    def findByItemId(id: Int): AsyncIO[Certification]
  }
}

object ScalaFutureApiComponent extends AsyncApiComponent[Future] {

  def itemRepository: ItemRepository = itemRepositoryScalaFuture
  def catalogRepository: CatalogRepository = catalogRepositoryScalaFuture
  def brandRepository: BrandRepository = brandRepositoryScalaFuture
  def itemWishCountRepository: ItemWishCountRepository = itemWishCountRepositoryScalaFuture
  def categoryRepository: CategoryRepository = categoryRepositoryScalaFuture
  def itemDetailRepository: ItemDetailRepository = itemDetailRepositoryScalaFuture
  def itemCertificationRepository: ItemCertificationRepository = itemCertificationRepositoryScalaFuture

  object itemRepositoryScalaFuture extends ItemRepository {
    def findById(id: Int): Future[Item] = Future {
      Thread.sleep(1000)
      log(s"item-$id")
      Item(id, 1000, 100000)
    }
  }

  object catalogRepositoryScalaFuture extends CatalogRepository {
    def findById(id: Int): Future[Catalog] = Future {
      Thread.sleep(1000)
      log(s"catalog-$id")
      s"catalog-$id"
    }
  }

  object brandRepositoryScalaFuture extends BrandRepository {
    def findById(id: Int): Future[Brand] = Future {
      Thread.sleep(1000)
      log(s"brand-$id")
      s"brand-$id"
    }
  }

  object itemWishCountRepositoryScalaFuture extends ItemWishCountRepository {
    def findByItemId(id: Int): Future[Wish] = Future {
      Thread.sleep(1000)
      log(s"wish-$id")
      s"wish-$id"
    }
  }

  object categoryRepositoryScalaFuture extends CategoryRepository {
    def findOneByBrandId(id: Int): Future[Category] = Future {
      Thread.sleep(1000)
      log(s"category-$id")
      s"category-$id"
    }
  }

  object itemDetailRepositoryScalaFuture extends ItemDetailRepository {
    def findByItemId(id: Int): Future[Detail] = Future {
      Thread.sleep(1000)
      log(s"detail-$id")
      s"detail-$id"
    }
  }

  object itemCertificationRepositoryScalaFuture extends ItemCertificationRepository {
    def findByItemId(id: Int): Future[Certification] = Future {
      Thread.sleep(1000)
      log(s"certification-$id")
      s"certification-$id"
    }
  }


}


object CatsEffectApiComponent extends AsyncApiComponent[IO] {
//  type AsyncIO[+A] = cats.effect.IO[A]

  def itemRepository: ItemRepository = itemRepositoryCatsEffect
  def catalogRepository: CatalogRepository = catalogRepositoryCatsEffect
  def brandRepository: BrandRepository = brandRepositoryCatsEffect
  def itemWishCountRepository: ItemWishCountRepository = itemWishCountRepositoryCatsEffect
  def categoryRepository: CategoryRepository = categoryRepositoryCatsEffect
  def itemDetailRepository: ItemDetailRepository = itemDetailRepositoryCatsEffect
  def itemCertificationRepository: ItemCertificationRepository = itemCertificationRepositoryCatsEffect

  object itemRepositoryCatsEffect extends ItemRepository {
    def findById(id: Int): IO[Item] = IO.async[Item] { cb =>
      Thread.sleep(1000)
      log(s"item-$id")
      cb(Right(Item(id, 1000, 100000)))
    }
  }

  object catalogRepositoryCatsEffect extends CatalogRepository {
    def findById(id: Int): IO[Catalog] = IO.async[Catalog] { cb =>
      Thread.sleep(1000)
      log(s"catalog-$id")
      cb(Right(s"catalog-$id"))
    }
  }

  object brandRepositoryCatsEffect extends BrandRepository {
    def findById(id: Int): IO[Brand] = IO.async[Brand] { cb =>
      Thread.sleep(1000)
      log(s"brand-$id")
      cb(Right(s"brand-$id"))
    }
  }

  object itemWishCountRepositoryCatsEffect extends ItemWishCountRepository {
    def findByItemId(id: Int): IO[Wish] = IO.async[Wish] { cb =>
      Thread.sleep(1000)
      log(s"wish-$id")
      cb(Right(s"wish-$id"))
    }
  }

  object categoryRepositoryCatsEffect extends CategoryRepository {
    def findOneByBrandId(id: Int): IO[Category] = IO.async[Category] { cb =>
      Thread.sleep(1000)
      log(s"category-$id")
      cb(Right(s"category-$id"))
    }
  }

  object itemDetailRepositoryCatsEffect extends ItemDetailRepository {
    def findByItemId(id: Int): IO[Detail] = IO.async[Detail] { cb =>
      Thread.sleep(1000)
      log(s"detail-$id")
      cb(Right(s"detail-$id"))
    }
  }

  object itemCertificationRepositoryCatsEffect extends ItemCertificationRepository {
    def findByItemId(id: Int): IO[Certification] = IO.async[Certification] { cb =>
      Thread.sleep(1000)
      log(s"certification-$id")
      cb(Right(s"certification-$id"))
    }
  }

}


object experiment {

  def main(args: Array[String]): Unit = {
    val scalaFutureResult = awaitTime { getProduct(ScalaFutureApiComponent, 10) }
    println(scalaFutureResult)

    val catsEffectResult = awaitTime { getProduct(CatsEffectApiComponent, 10).unsafeToFuture() }
    println(catsEffectResult)
  }

  def awaitTime[R](block: => Awaitable[R]): R = {
    val start = System.nanoTime()
    val result = Await.result(block, 1 minutes)
    val elapsed = System.nanoTime() - start
    println("elapsed : " + elapsed / (1000 * 1000 * 1000.0) + " sec")
    result
  }
  def getProduct[F[+_]: Monad](api: AsyncApiComponent[F], itemId: Int): F[List[String]] = {
    import api._
    val itemFuture = itemRepository.findById(itemId)
    itemFuture.flatMap{ item =>
      val catalogFuture = catalogRepository.findById(item.catalogId)
      val brandFuture =  brandRepository.findById(item.brandId)
      val wishFuture = itemWishCountRepository.findByItemId(item.id)
      val categoryFuture = categoryRepository.findOneByBrandId(item.brandId)
      val itemDetailFuture = itemDetailRepository.findByItemId(item.id)
      val itemCertificationFuture = itemCertificationRepository.findByItemId(item.id)
      for {
        catalog <- catalogFuture
        brand <- brandFuture
        wishCount <- wishFuture
        category <- categoryFuture
        detail <- itemDetailFuture
        certifications <- itemCertificationFuture
      } yield
        List(brand, catalog, wishCount, category, detail, certifications)
    }
  }

}

