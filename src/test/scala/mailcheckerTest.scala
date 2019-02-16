import org.scalatest.FunSuite

/**
  * Created by ikhoon on 2016. 5. 22..
  */
class mailcheckerTest extends FunSuite {

  test("testIs_email") {
    //val emails = List("plop@plop.com", "my.ok@ok.plop.com", "my+ok@ok.plop.com", "my=ok@ok.plop.com", "ok@gmail.com", "ok@hotmail.com")
    val emails = List("plop@plop.com")
    assert(emails.forall(email => mailchecker.is_email(email)))
  }

  test("testValid_email") {

  }

}
