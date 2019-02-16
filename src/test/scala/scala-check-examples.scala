import org.scalacheck.Properties
import org.scalacheck.Prop.forAll


/**
 * Created by liamm on 8/11/15.
 */
object ScalaCheckExamples {

}
object StringSpecification extends Properties("String") {

  property("startsWith") = forAll { (a: String, b: String) =>
    (a+b).startsWith(a)
  }

  property("concatenate1") = forAll { (a: String, b: String) =>
    println(s"a : $a, b : $b")
    (a+b).length >= a.length && (a+b).length >= b.length
  }

  property("concatenate2") = forAll { (a: String, b: String) =>
    (a+b).length >= a.length && (a+b).length >= b.length
  }

  property("substring") = forAll { (a: String, b: String, c: String) =>
    (a+b+c).substring(a.length, a.length+b.length) == b
  }

}

