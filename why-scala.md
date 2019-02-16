# 스칼라?<br/> 왜 해야할까요?

![fit](http://zeroturnaround.com/wp-content/uploads/2014/05/what-technologies-are-developers-interested-in-640x453.jpg)

---------

![left, fit](http://www.enewstoday.co.kr/news/photo/201602/482806_122974_20.jpg)
![right, fit](https://mud-kage.kakao.com/dn/beR70H/btqcNAo7ULA/kzsmMk9LwBOHgtGGXhN4G1/o.png)

## 1. 흙수저입니다.
스칼라는 어쩌면 노력만으로 금수저를 이길수 있습니다.
 _**Top paying tech in US**_


---------


![left, fit, filtered](https://assets.toptal.io/uploads/blog/image/548/toptal-blog-image-1409691715906.png)

# 2. Java는 <br/>손가락 건강에 좋지 않습니다.


---------
### 3. 흔한건 싫다. We want to be geek.

![fit](http://thumbnails-visually.netdna-ssl.com/most-popular-programming-languages-of-2013_5113fc6a37abb_w1500.jpg)
![fit](http://static1.squarespace.com/static/51361f2fe4b0f24e710af7ae/t/52dc3638e4b0d99728f927ae/1390163522743/codeeval2014.jpg)
![fit](http://cdn-ak.f.st-hatena.com/images/fotolife/f/fxrobot/20150213/20150213195823.jpg)

--------

![](http://cfile28.uf.tistory.com/image/210B3D41524363880F55C8)
## 4. 노령연금의 폐해 - 노후준비
은퇴후에 할수 remote로 일을 구할 수있습니다.

-------

![fit](./img/scala-job-trends.png)
![fit](./img/java-job-trends.png)

[Scala Job Trends](http://www.indeed.com/jobtrends/scala%2Cclojure%2Cgroovy%2Cerlang.html)

--------

## 5. 스칼라로 코딩하는 것은  재미있습니다.

```scala
// 타입추론 - 코드가 간결해 진다.
val a : String = "world"
val b = "hello"
```

```scala
// 멋진 정규식 매칭
val str = "hello world"
val WithHello = "hello (.*)".r
str match {
  case WithHello(next) => println(next)
}
```
그외 많다.

--------

![](./img/quill-contibute.png)

## 6. 성장하는 스칼라 커뮤니티
- 새롭게 만들어지는 프로젝트가 많다.
- scala open source contribute할 기회가 많다.
- [그들은 생각보다 친절함](https://github.com/getquill/quill/pull/308)

--------

## 7. thread pool hell은 이제 그만

![fit](http://image.slidesharecdn.com/theplayframeworkatlinkedin-final-130604033451-phpapp01/95/the-play-framework-at-linkedin-8-638.jpg)

### Scala는 쉽게 async 프로그래밍을 할수 있다.

// Asynchronous computations that yield futures are created with the Future call

val s = "Hello"
val f: Future[String] = Future {
  s + " future!"
}
f onSuccess {
  case msg => println(msg)
}
```

--------

## 8. 코딩이 잼있다.

> 심심할때 취미로 하기에 좋은 언어이다.

![contest](./img/scala-contest.png)

--------

## 9. Rich collection api
![collection](http://image.slidesharecdn.com/whyscala-150415145907-conversion-gate01/95/scala-or-functional-programming-from-a-python-developers-perspective-17-638.jpg?cb=1429110056)

--------

### 10. 쉬운 에러 핸들링 - Try[T]

```scala
import scala.util.{Failure, Success, Try}
val success1: Try[String] = Success("success-1")
val success2: Try[String] = Success("success-2")
val fail: Try[String] = Failure(new IllegalStateException("Error"))

val result1 = for {
  a <- success1
  b <- fail
  c <- success2
} yield c

// result1: scala.util.Try[String] = Failure(java.lang.IllegalStateException: Some Error)
val result2 = for {
  a <- fail
  b <- success1
  c <- success2
  } yield c
// result2: scala.util.Try[String] = Failure(java.lang.IllegalStateException: Some Error)
```
