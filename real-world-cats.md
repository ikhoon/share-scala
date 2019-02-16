slidenumbers: true
autoscale: true

<br/>
<br/>

# [fit] Real World ![](https://typelevel.org/cats/img/navbar_brand2x.png) **Cats**

<br/>

# @ikhoon

^안녕하세요 엄익훈입니다.

^오늘은 스칼라의 functional programming 라이브러리로 유명한
cats를 실무에서 어떻게 사용하는 공유하는 시간을 가져보겠습니다.

---

# 스칼라 여정기

*리치자바 시절* 🐣 - 스칼라 별거아니네, 람다 편하네 건방쩖 (~ 2015)

*모나드 겨우 알아가던 시절* 🤩  - 이런것도 있어? 신났음 (2016)

*Category theory 공부하던 시절* 🤬 - 좌절... 끝이 어디냐! (2017)

*Pure functional로 가는길* 🛤 - 새로운 시작 (2018 ~)

인생무상이로다...


^스칼라로 개발한지는 3~4년정도 됬었습니다.

^ 개인적으로 스칼라를 배우고 공부한 과정을 적어봤는데요.
여러분들은 어떤 과정을 겪고 있는지 궁금합니다.

^처음에는 많은 분들과 시작이 비슷했다고 생각이 되는데
스칼라를 리치자바 스타일로 개발던것 같아요.
람다 조금 쓸줄아는 정도 인데요.
그때 함수형 언어 별거아니네 이런생각을 많이 했던 시기 였습니다.

^ 건방진 시기였죠...

^ 그리고 모나드를 알아가면서 기존에 몰랐던 함수형 언어의 재미에 푹 빠져서 배우는게 신이 났었죠.

^ 그런데 카테고리 이론를 공부하면서 좌절감을 느끼게 되더라구요.
어느정도 공부해야 잘 할수 있을지에 대한 의문도 생겼구요

^ 3~4년 정도 계속 공부하다 보니 스칼라, 함수형 언어의 끝은
카테고리 이론이 아니라 순수함수가 있는것 같아요.

^ 그래서 먼저 제가 잊고 지냈던
함수에 대해서 다시 알아보려 합니다.

---

![fit right](./img/function-quiz.png)

# 기본 부터 다시 시작해보자, __y = f(x)__


1\. 다음중 함수가 아닌것은?


^ 여러분들 혹시 중학교때 배운 함수의 정의 기억이 나시나요?

^ 집합 X의 원소 하나에는 Y의 원소 하나만이 대응된다

^ 집합 X의 원소는 모두 대응되는 집합 Y의 원소가 있다.

^ 그럼 정답을 맞춰볼까요?



---

# 기본 부터 다시 시작해보자, __y = f(x)__

![fit right](./img/function-solve.png)

1\. 다음중 함수가 아닌것은?

정답 : 3, 4

<br/>
> "함수가 아니지만 함수라는 이름이 붙은
*부분* 정의 *함수*의 개념이 존재한다." [^29]

^ 정답은 3번과 4번 입니다.

^ 함수의 첫번째 정의
집합 X의 원소 하나에는 Y의 원소 하나만이 대응된다
에 위배됩니다.

^ 3번은 하나의 입력에 2개의 결과가 았어서 안되고

^ 그리고 4번은
"집합 X의 원소는 모두 대응되는 집합 Y의 원소가 있다."
에 위배됩니다.

^ 3의 입력에 대한 결과값이 없는 형태입니다.
부분 정의 함수라고 합니다.

^ 이는 스칼라에도 있는 용어죠
particial function, 부분함수라고 부르구요
하지만 명확히 말하면 함수는 아닙니다.

---

# 기본 부터 다시 시작해보자, __y = f(x)__

> 그러면 우리가 매일 함수라고 호칭하며 만드는 함수는?

<br/>
**2\. 다음중 함수가 아닌것은?**

![right fit](./img/function-code-quiz.png)

^수학적인 개념이 어려울수도 있으니
중학교 졸업한지가 언제인지도 당연히 잊을수 밖에 없습니다.

^그러면 우리가 매일 만지는 코드를 가지고 이야기 해볼께요.

^다음중 함수가 아닌거은 무엇일까요?

^ 서지연?

---

**아 내가 만든건 함수가 아니였구나... 함수가 뭐길래**

![inline fill](./img/function-code-solve.png)

^ 간단한 코드이지만 우리는 코드를 함수로 만드는 것이 중요합니다.
우리는 입력값에 대한 출력값을 원했지
Exception을 원하진 않았으니까요

^ 코드의 내부 구현을 보면 예외가 발생한다는것을 말해주고 있지만
우리는 그 함수의 시그니쳐만 보고 함수가 어떻게 동작하는지 알수 있으면 더 좋겠죠.
이함수는 int를 받아서 bigint를 반환한다고 말해주고 있으니까요.

---

<br/>
<br/>


```scala
def factorial(n: Int): Either[String, BigInt] =
  if (n < 0) Left("n은 0 혹은 그보다 큰 양수이어야 합니다")
  else if (n == 0) Right(1)
  else factorial(n - 1).map(_ * n)
```

<br/>
*진짜 함수*를 만들기 위해서 cats를 사용할때입니다.
Either와 같은 Monad*ic* 타입 다루는데 cats만한게 없으니까요 ;-)


^ 그러면 순수함수와 cats가 무슨 상관이 있을까요?
여기 코드를 보면 순수함수로 만들기 위해서 반환값을 Either로 반환 하였습니다.
Either 같은 Monadic 타입을 잘 다루기 위해서는 cats가 필요합니다.



---

수학에서는 **함수**라 불리고

코딩할때는 *순수함수*라 불리는 녀석과 함께

# **순수함수 + 프로그래밍**에 대해 알아보자

![fit](http://juan-medina.com/assets/img/Functional-Prog.png)

^ 그러면서 코드를 작성할때 함수로 만드는게 왜 중요한지 좀더 알아볼까요?

---

# __순수 함수__의 정의 및 장점[^2]


- They’re easier to _reason about_
- They’re easier to _combine_
- They’re easier to _test_
- They’re easier to _debug_
- They’re easier to _parallelize_
- They are _idempotent_
- They offer _referential transparency_
- They are _memoizable_
- They can be _lazy_

![left fit](https://alvinalexander.com/images/fp-book/pure-functions/1-Pure-Function-Equation.png)

^ 왼쪽은 순수함수의 정의이고 오른쪽은 장점인데요.

^ 순수함수는 출력값이 입력값에만 의존해야 하며 사이드 이펙트가 없어야합니다.

^ 이런 순수함수는 결과에 대한 추론이 쉽고
합성, 테스트, 디버깅, 병렬처리, 멱등성, 참조 투명성의 특성이 있습니다.
그리고 출력값이 입력값에만 의존하기 때문에 저장해도 되고 나중에 동작해도 같은 결과를 얻을수 있는 lazy의 특성이 있습니다.

---

# 스칼라에서 __순수 함수__의 장점 활용하기

* **순수 함수의 장점을 살려놓은 라이브러리들이 많이 있다.**
> 합성(_**compose**_), 병렬처리(_**parallel**_)
저장(_**memoize**_), 느긋함(_**lazy**_)를 잘 지원해준다.

* **DRY(Don't Repeat Yourself)**
> 직접 만들려하지 말자


^ 스칼라는 순수함수를 추구하는 언어가 아니기 때문에 스칼라에서 순수함수의 스타일로 개발을 하려면 pure function의 장점을 살려놓은 라이브러리는 이용하는게 좋습니다.

^ 이런함수들은 합성이나, 병렬처리, 결과값에 대한 저장 그리고 lazy를 잘 지원해줍니다.

^ 역시나 직접만들 필요는 없죠?

---

# 어떤 라이브러리를 사용할것인가?

조건 1 - 문서화가 잘되어 있고 참조 문서가 많은가?
<br/>
![inline 90%](./img/cats-doc.png)

* 잘정리된 _공식 문서_
* Scala with Cats[^15] _책_도 있음
* 많은 레퍼런스 문서, 구글에서 "scala cats"로 검색시 _458,000개 결과_

---

# 어떤 라이브러리를 사용할것인가?

조건 2 - 안정적이고 활발하고 지속적으로 개발되고 있는가?
<br/>
![inline 100%](./img/cats-contribute.png)

* _209_명의 컨트리뷰터
* _3452_개의 커밋
* 전체 Pull Request _1524_개(42개 Open, 1482개 Closed)
* _1.0.0_ 릴리즈 이후 바이너리 호환성을 중요시 하고 있다.

^ 즉 안정적이다.

---

# 어떤 라이브러리를 사용할것인가?


조건 3 - 라이브러리와 관련된 ecosystem이 잘 구축되어 있는가?

* _**circe**_: pure functional *JSON* library
* _**doobie**_: a pure functional *JDBC* layer for Scala
* _**finch**_: Scala combinator library for building Finagle *HTTP* services
* _**FS2**_: compositional, *streaming I/O* library
* _**http4s**_: A minimal, idiomatic Scala interface for *HTTP*
* _**Monix**_: high-performance library for composing asynchronous and *event-based* programs

다양한 목적을 가진 라이브러리들에서 사용되고 있으며 홈페이지에 언급된 라이브러리만 __*31개*__이다.

^ 엔터프라이즈 많이 사용되는 json, http, jdbc, observable, stream 등이
cats의 ecosystem으로 구축되어 잘 연동이 되고 있다.

---
# Cats - 믿을만 오픈소스 라이브러리이다.

![inline fit](http://plastic-idolatry.com/erik/cats2.png)

^ 이제 cats에 대해서 조금더 알아보도록 하겠습니다.

---

# Cats의 구성

![inline 85%](./img/cats-architecture.png)

- Typeclass는 행위를 정의하는 인터페이스의 한 형태이다.[^17]
- Datatype이 '특정 A' typeclass의 instance가 있다는 의미는 '특정 A' typeclass의 행위를 구현하고 지원한다는 뜻이 된다.
- ex) Option(_datatype_)은 Monad(_typeclass_)의 행위를 구현(_instance_)했기 때문에 모나드의 인터페이스(pure, flatMap)을 사용할수 있다.

^ cats는 3가지 요소로 구성되어 있습니다.

^ Typeclass는 행위를 정의하는 인터페이스의 한 형태이다.
타입 클래스는 ad hoc polymorsm 을 통해 오버로딩을 지원하는 타입 시스템 구조이다. 어렵다. 이런말은 하기 싫었습니다.

^ Datatype은 cats에서 제공해주는 자료구조형 입니다.

^ typeclass에 정의된 행위를 datatype이 구현을 하면 그것은 instance가 된다고 할수 있습니다.

^ 조금 말이 어렵나요? 계속 더 알아보죠.

---

![right fit](./img/cats-typeclass.png)

# Cats의 구성

* **_Type classes_** [^30]

* Data types

* Instance

^ cats의 typeclass는 상당히 많은데요.
복잡한 계층구조를 이루고 있습니다.

^ 처음보시는 분은 조금 놀라셨을거라 생각이 됩니다.

^ 타입클래스는 많이들 알고 있는 Monad, Functor이외에
Foldable, Apply, Comonad, InvariantFunctor가
있구요

^ 아래쪽에는 IO를 다루는 cats effect와
젤 아래쪽에는 monad transformer library의 typeclass들이 존재합니다.

^ 이중에서 색깔이 조금더 진한것에만 집중을 하시면 될겁니다.

---

![right fit](./img/cats-typeclass-highlight.png)

# Cats의 구성

* **_Type classes_**

* Data types

* Instance

많고 복잡하다. 🤯

하지만 _Core_ 기능만 알자.
그거면 충분하다.

^ 하지만 다 알필요가 없습니다.
필요한게 있으면 조금씩 알아가면 됩니다.

^ 조금더 확인하기 쉬우라고 나머지 색은 회색처리를 했는데요.
중요하지 않다는 뜻은 아닙니다.
시작하시는 분들을 위한 가이드라고 생각해주세요.

---

![right fit](./img/cats-datatypes2.png)

# Cats의 구성

- Type classes
- **_Data types_**
- Instance


^ cats에서 제공해주는 자료구조형이다.

^ typeclass보다는 적지만 datatype도 종류가 꽤 많습니다.

^ 처음보거나 익숙하지 않은 부분이 많을것 같은데요.

^ 비동기나 side effect를 도와주는 IO타입

^ DI와 함수의 합성을 도와주는 Kleisli

^ State, Writer 모나드가 있구요.

^ 인터프리터 패턴을 구현할수 있게 해주는 Free Monad가 있습니다.

^ 그리고 마지막 단어가 T로 끝나는 Monad Transformer가 오른쪽에 있습니다.

^ 역시나 다 알필요가 없습니다.

---

![right fit](./img/cats-datatypes2-hi.png)

# Cats의 구성

- Type classes
- **_Data types_**
- Instance

cats에서 제공해주는 자료구조형이다.

서로간에 합성을 할수 있게 도와주고
특히 _OptionT_와 _EitherT_는 👍👍이다.

^ 역시나 조금더 중요하다고 생각되는것만 하이라이딩을 했는데요.

^ 그중에서도 가장 많이 사용되는 자료 구조형을 뽑으라 하면

^가장많이 사용되는 자료구조형은 monad transformer인
OptionT와 EitherT입니다.
초반엔 두개에 대해서만 알아두시면 좋을것 같습니다.

^ Monad Transformer에 대해서는 조금후에 다시 조금더 자세히 알아보도록 하겠습니다.


^ 빨간책에 나오는 State, Writer, Reader 같은건 조금 천천히 아셔도 됩니다.

---


# Cats의 구성

- Type classes
- Data types
- **_Instance_**


*Instance*는 Datatype과 typeclass를 연결시켜주는 고리 역할이다.
Data type과 type class 사이는 구현이 불가능한 관계인 경우도 있다.

하지만 아직은 신경쓰지말자.

기본적인 _core datatype_과 _core typeclass_의 instance는 다 구현되어 있다.[^18]


^ 화면 그대로 읽자

---

# Cats 시작하기 전에 - Monad 왜 중요한가?

함수형 언어를 공부하면 Monad에 대한 이야기가 많다.

Monad가 왜 중요할까?

^ 모나드의 수학적인 정의에 대해서 알아볼까요?

---

![fit right](https://i.pinimg.com/originals/10/3d/22/103d225157184787a944b56fb2ece777.jpg)

# Monad 위키백과 정의 [^8]

C가 범주라고 하자. 그렇다면 자기 함자 C -> C 들을 대상으로 하고, 이들 사이의 자연 변환들을 사상으로 하는 자기 함자 범주 End(C)를 생각하자. End(C)는 모노이드 범주이며, 따라서 End(C) 속의
모노이드 대상을 생각할 수 있다. End(C)의 모노이드를 C의 _모나드_라고 한다.

^ 한국말인지 의심이 되는 단어들이네요.

---

# Cats 시작하기 전에 - Monad 왜 중요한가?

함수형 언어를 공부하면 Monad에 대한 이야기가 많다.

모나드가 왜 중요할까?


<br/>
~~수학이 아닌~~

소프트웨어 설계의 관점에서의 *flatMap*(Monad)에서 알아보자.


^ 저는 수학을 좋아하지만 수학자가 아닙니다.


^ 저는 소프트웨어 개발자이기 때문에

^ 우리들 만의 언어로
소프트웨어 엔지니어링 관점에서,
소프트웨어 설계의 관점에서 Monad를 알아보겠습니다.

---

**Cats 시작하기 전에 - 기본이 되는 함수**

Ordered를 상속받아 _compare 함수를 구현_하면
4개의 비교함수(<, <=, >, >=)를 _공짜_로 얻을수 있다.

```scala
case class Version(major: Int, minor: Int, patch: Int) extends Ordered[Version] {
  def compare(that: Version): Int =
    if(major > that.major) 1
    else if (major == that.major && minor >  that.minor) 1 ...
    else -1
}

Version(1, 11, 1) <  Version(1, 1, 1) // false
Version(1, 10, 1) >  Version(0, 0, 1) // true
Version(10, 9, 3) <= Version(0, 0, 1) // false
```

^ 많이 사용하는 패턴인데요

^ 하나의 함수만 구현해서 4개의 함수를 추가로 얻을수 있습니다.
소프트웨어 설계의 관점에서 이는 중요한 로직이 응집도가 강해지고
중복 코드를 없앰으로 인해서 경제적이라 할수가 있는데요.


---

**소프트웨어 설계의 관점에서의 _Monad는
코드의 재사용성을 극대화 할수 있는 도구_이다.**

기본이 되는 두개의 함수 _pure와 flatMap만 구현_하면 많은 유용한 함수를 *공짜* 🎉로 얻을수 있다.(약 *110개*)

-  *map* = pure + flatMap
- *ap* = flatMap + map
- *product* = map + ap
- *map2* = map + product


^ 물론 map으로도 flatMap을 만들수 있습니다.
flatten이 라는 함수가 존재하면 말입니다.

^ 실제 코드로 어떻게 구현되는지 볼까요?

---

**소프트웨어 설계의 관점에서의 _Monad는 코드의 재사용성을 극대화 할수 있는 도구_이다.**

```scala
def map[A, B](fa: F[A])(f: A => B): F[B] =
  flatMap(fa)(f andThen pure)

def ap[A, B](fa: F[A])(ff: F[A => B]): F[B] =
  flatMap(fa)(a => map(ff)(f => f(a)))

def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] =
  ap(fb)(map(fa)(a => (b: B) => (a, b)))

def map2[A, B, Z](fa: F[A], fb: F[B])(f: (A, B) => Z): F[Z] =
  map(product(fa, fb))(f.tupled)
```

^ 타입 모양이 조금 복잡헤 보일수도 있지만
한줄이면 만들수 있습니다.

^ 신기하지 않나요?
생각보다 함수형 언어는 정교하고 간단하고 심플합니다.

^ 코드로 보니 조금더 쉽지 않나요?

^ 이코드가 어떻게 사용되는지 알아보겠습니다.

^ 첫번째 세션에 강제 할당이 되어서
어떻게 말을 풀까 고민을 했는데
최대한 여기 오신분들에게 나가실때는
cats가 조금더 궁금하고 사용하도록 약을 뿌리도록 하겠습니다.

---

# 어려워 말아요 cats 쓰다보면 조금씩 이해되더라... ✌️

카테고리 이론이 뭔지... 모나드가 뭔지 😨

복잡한 개념은 잠시 내려놓고

기본 개념과 *사용 예를 중심*으로  👉

<br/>
> "부족한 부분은 추가로 backup slide에 자료를 남겨 놓겠습니다"

--

# Cats 시작하기

build.sbt에 의존성 추가

```scala
scalacOptions += "-Ypartial-unification"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.1.0"
=```

그리고 바로 사용

```scala
import cats._
import cats.implicits._
```

간단하다. 복잡한 import가 필요없다 🎉 🎊

^ 여기 나와있는 예제코는 cats 1.1.0 버전 기준으로 작성하였습니다.


---

그럼 👨‍💻실무에서는 언제 어떻게 적용할것인가?

1. 많은 양의 데이터 연동 처리
2. 비동기 필터링
3. 확장가능한 cache 전략
4. nullable 데이터 효과적으로 다루기

^ cats를 사용해서 엔터프라이즈 개발에 도움이 될 수 있는 부분은 정말 많이 있습니다.

^ 몰라서 못쓸뿐이죠. 그중에서

^ rest api를 이용해 데이터를 주고 받는건 개발자 분들이 많이 하는 업무라 생각이 됩니다.
그리고 filter를 이용한 필터링
빠른 응답속도를 위한 cache
nullable한 데이터에 대한 부분

^ 4가지 주제정도로만 줄여서 공유해보도록 하겠습니다.

---

# 1. 많은 양의 데이터 연동 처리

*오늘의 업무* 📑

100개의 상품 정보를 가져와서 처리후 화면에 보여줘야함.
*REST API기준으로 API를 설계*하다 보면
한꺼번에 100개의 상품을 조회 API가 없는 경우도 많다... 🙅‍♂️

*GET /v1/api/items/:id*
기존 API를 활용 100개 상품 조회를 구현해보자.


```scala
val itemIds: List[Int]
// 단건의 상품 정보를 가져오는 API
def findItemById(itemId: Int): Future[Option[Item]]
```

^ 아래 API는 하나의 상품의 정보를 요청할수 있는 API입니다.

---

# 느린 API 응답속도

*__Problem__*

하나의 API가 100ms 걸린다면?
100개의 상품 정보를 가져오는데
합쳐서 *10초?!* 이건 쓸수 없다. 🤦‍♂️

다시 API담당자에게
똑똑 바쁘시겠지만 죄송하지만...
업무가 바쁘셔서 2주 기다려 달란다.😭

*기획자가 쪼은다*
이번주까지 해달라는데 😱

![right fit](http://i.imgur.com/2qx57hH.png)

^ 기존의 API를 연동해야하는 상황에
100개의 상품을 조회하는 API를 만들려면 어떻게 해야 할까요?

---

# 병렬 처리를 통한 응답속도 향상

*__Solution__*

병렬로 처리하자[^25]
하지만 동시에 100개를 요청을 상대방 서버로 할수 없다.

100개를 동시에 요청하면
상대방 서버에서 *DDOS 공격*처럼 느껴질수 있다.

*부담이 안갈 수준으로 병렬로 호출하자.*
100개를 한번에 10개씩 10번에 걸쳐 처리하면
부하에 대한 부담도 없고 빠르게 처리할수 있다.
예상되는 처리 시간은 1초이다.🙂

![right fit](http://i.imgur.com/45HkUaG.png)

^1초라는 응답속도가 OLTP 환경에서 적합하지 않을수 있으나
이부분은 병렬도를 더 높이며 해결될것 같습니다.

---

# 순차와 병렬 처리의 조합

*flatMap*
데이터를 순차적으로 처리할수 있다.

*traverse*
데이터를 동시에 처리(*map*)하고 처리된 결과를 모아(*reduce*)준다.

_flatMap과 traverse를 조합_해서 비지니스의 요구사항을 구현할수 있다.

```scala
def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

def traverse[G[_]: Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]
```

^ 그리고 traverse는 cats뿐만 아니라 스칼라 Future에도 내장되어 있어서
꼭 cats가 아니라도 같은 효과를 얻을수 있습니다.

^ traverse에서 F는 List, 상품 ID의 리스트입니다.
그리고 G가 Future에 해당하면
최종결과는 Future에 List에 상품 정보입니다.

---

__Future, Lazy, Parallel 그리고 traverse특성에 대해 조금 더 알아보기__

- Traverse
Traverse 자체는 병렬 처리를 위한 typeclass가 아니다.
그렇기 때문에 travese 함수 자체로는 *병렬 동작을 보장할수 없다*.

- Parallel (*parTraverse*, *parMapN*)
cats에서는 병렬처리를 위한 typeclass가 별도로 존재한다.
하지만 Scala Future는 Parallel의 instance가 없다.[^28]

- Scala Future (Eager)
대신 Eager evalution되어 *traverse 함수가 병렬*로 동작을 한다.

- Monix Task (Lazy)
Lazy evaluation되고 *traverse 함수가 순차*적으로 실행된다.
*monix의 Task는 parTraverse*를 써야지만 병렬로 동작한다.
코드의 동작 측면에서는 Monix의 Task가 더 예측가능하고 좋다.

```scala
def findItemById(itemId: Int): Task[Item] // Monix의 Task
itemIds.traverse(findItemById)     // 순차 진행하며 10초 걸림
itemIds.parTraverse(findItemById)  // 병렬 처리되며  1초 걸림
```

---

# 한번에 10개씩, 10번에 나누어 처리하자

```scala
사용할 API들
- grouped  : 10개의 단위로 묶고
- foldLeft : List의 요소에 순차 접근하여
- flatMap  : 10개의 처리가 완료된 후에 다음 10개가 처리되게 하며
- traverse : 10개를 동시에 처리할수 있도록 한다.(혹은 parTraverse)

구현은 생각보다 간단함 🙂
val items = itemIds
  .grouped(10).toList                               // 그룹
  .foldLeft(List.empty[Item].pure[Future]) ((acc, group) =>
     acc.flatMap(xs =>                              // 순차
       group.traverse(findItemById).map(xs ::: _))) // 병렬
```

^ 구현은 간단해서 따로 설명을 드릴만한 부분이 없는것 같습니다.
라인별로 살펴보면
위에서부터
그루핑과 순차 진행 처리 마지막에 병렬 처리를 해주는 부분이 있습니다.

---

두번째 이야기

1. 많은 양의 데이터 연동 처리
2. *비동기 필터링*

^ 괜찮으신가요?
졸리시진 않죠?
이제 조금더 재미있는 이야기를 해보려합니다.

---

# 2. 비동기 필터링

_Problem_

주어진 상품 ID들 중에 아직 *유효한 상품ID만 필터링*하고 싶다.
그런데 상품의 상태는 *비동기로 가져*와야 한다.

```scala
val itemIds: List[Int]
// 상품 상태를 가져오는 API
def isActiveItem[F[_]](itemId: Int)(implicit F: Effect[F]): F[Boolean]
```

필요한 로직은 filter + async

^ 상품의 상태를 리모트에서 가져오면 그 결과는 비동기를 표현하는 객체에 담겨져서 오는데요.
그러면 우리가 필요한 로직은
비동기로 가져온 데이터에 대한 필터링 로직입니다.

---

# 2. 비동기 필터링 구현하기


원초적인(_생각나는데로_) 문제를 풀어본다.

```scala
// 1. map을 이용해 상품 정보를 읽어온다.
val step1: List[IO[(Int, Boolean)]] = itemIds.map(itemId =>
  isActiveItem[IO](itemId).tupleLeft(itemId))

// 2. IO가 List안에 있으니 밖으로 꺼내자
val step2: IO[List[(Int, Boolean)]] = step1.sequence

// 3. 이제 겨우 filter해본다.
val step3: IO[List[(Int, Boolean)]] = step2.map(xs => xs.filter(_._2))

// 4. 필요없는 Boolean은 제거
val step4: IO[List[Int]] = step3.map(_.map(_._1))
```

^ 여기에서는 cats의 IO 모나드를 사용했는데요.
구현과정을 살짝 살펴보겠습니다.

^ 상품정보를 가져오고
IO가 List안에 있어서 핸들링하기 힘드니 밖으로 꺼내고
이제 겨우 filter해본다.
필요없는 Boolean은 제거


---

# 비동기 필터링 구현하기

구현하기 불편하고 복잡하다. 👎

나는 필터링만 하고 싶다.

더 나은 방법이 없을까?

---

# 비동기 필터링 구현하기

구현하기 불편하고 복잡하다. 👎

나는 필터링만 하고 싶다.

더 나은 방법이 없을까?

있다! 🙆‍♂️🙆‍♂️🙆‍♂️

_filterA_를 활용하자.

---

# __filterA__ 함수를 이용한 비동기 필터링 구현하기

구현이라 하기도 애매하다. 너무 짧다. filterA함수 하나만 호출하면 된다.

```scala
import cats.mtl.implicits._

// filterA함수를 통해서 앞에 했던 로직을 표현할수 있다.
val activeItem: IO[List[Int]] = itemIds.filterA(isActiveItem[IO])
```

앞서 4개의 step을 통해 했던 작업은 *filterA 함수하나*면 된다.

훨씬 코드가 간결해졌다. 🤩  FP 만세!!!

*외부에서 데이터의 상태의 정보를 가져와 연동*하는 곳에서는 유용한 함수라 생각된다.

^여기서 mtl을 import하는 이유는 뒤쪽에서 추가로 설명드리겠습니다.

---


# **filterA** 함수의 구조 알아보기

```scala
def filterA[G[_], A]
  (fa: F[A])(f: A => G[Boolean])
  (implicit G: Applicative[G]): G[F[A]]
```


- filterA의 predicate
함수가 _A => G[Boolean]_ 형태이다.

- G는 Applicative
Applicative의 instance를 가 있는 List, *Future*, Option가 올수 있다.

- filterA의 활용
꼭 비동기에 국한된건 아니다.
_f: A => Option[Boolean]_ 등 상황에 맞는 함수를 조합해서 사용할수 있다.

---

# cats 1.x 에서 **filterA** 사용하기

cats 0.x 버전에서는 filterA는 import만 해도 사용할수 있고

```scala
import cats.implicits._     // 0.x 전에는 이것만
```

cats 1.x로 넘어가면서 filterA는 _**`cats-mtl`**_[^23]에 이동되었다.
그래서 사용을 하려면 의존성 추가와 import를 해줘야 한다.

```scala
libraryDependencies += "org.typelevel" %% "cats-mtl" % "0.2.1"
import cats.mtl.implicits._ // 추가로 import해줘야함
```

^ 이번에 filterA를 아신분은 나중에 비동기 필터링이 필요할때 사용해보시기 바랍니다.

---

세번째 이야기

1. 많은 양의 데이터 연동 처리
1. 비동기 필터링
2. *확장가능한 cache 서비스 구현*

---

# 3. *확장가능한 cache 서비스 구현*

우리는 빠른🚀 응답을 위해 *cache에 결과값을 저장*해서 사용하는 경우가 많다.
Cache miss가 발생을 보안하기 위해 backup에서 데이터를 읽어와서 처리를 구현했다.

*Problem* - 아래 코드 자체가 문제이다.
무엇을 말하고자 하는지 한눈에 들어 오지 않는다.
많은 패턴 매칭은 코드의 *가독성을 떨어 뜨린다*. *중복코드*가 존재한다. 👎
cache의 layer가 늘어나거나 줄어들어 코드를 바꾸기엔 *확장성도 떨어진다*.

```scala
def fetch[F[_]](id: Int)(implicit F: Effect[F]): F[Option[User]] =
  fetchLocalCache(id).flatMap {                //local cache에서 데이터를 가져오고
    case cached @ Some(_) => F.pure(cached)    //그 값이 있으면 사용한다
    case None =>                               //없으면
      fetchRemoteCache(id).flatMap {           //remote cache에서 데이터를 가져오고
        case cached @ Some(_) => F.pure(cached)//그 값이 있으면 사용한다.
        case None => fetchDB(id)               //또 없으면 DB에서 가져온다.
      }
  }
```

---

*Problem* - 아래 코드 자체가 문제이다.

<br/>

```scala
def fetch[F[_]](id: Int)(implicit F: Effect[F]): F[Option[User]] =
  fetchLocalCache(id).flatMap {                 // local cache에서 가져오고
    case cached @ Some(_) => F.pure(cached)     // 그 값이 있으면 사용한다
    case None =>                                // 없으면
      fetchRemoteCache(id).flatMap {            // remote cache에서 가져오고
        case cached @ Some(_) => F.pure(cached) // 그 값이 있으면 사용한다.
        case None => fetchDB(id)                // 또 없으면 DB에서 가져온다.
      }
  }
```


---

# Cache miss 처리 구현의 문제점

패턴 매칭을 없애고
각각의 cache layer간의 커플링을 줄이고
이를 유지보수 하기 쉽게 정형(*pattern*)화 할수 있을까?


---

# Cache miss 처리 구현의 문제점

패턴 매칭을 없애고
각각의 cache layer간의 커플링을 줄이고
이를 유지보수 하기 쉽게 정형(*pattern*)화 할수 있을까?

있다! 🙆‍♂️🙆‍♂️🙆‍♂️

SemigroupK의

_“pick the first winner” [^10]_ 원칙을 적용하면 된다.

^ semigroupk의 특성을 활용하면 되는데요
semigroupk에는 pick the first winner을 원칙,
먼저 성공한 값에 대해서 취한다는 원칙을
적용하면 됩니다.

^ semigroupk에 대해서 조금더 자세히 알아보도록 하겠습니다.

---

# **SemigroupK** 구조 및 특징 알아보기

**구조** - 인터페이스는 단순하다. 두개의 _F[A]_타입을 합친다

```scala
@typeclass trait SemigroupK[F[_]] {
  @op("<+>") def combineK[A](x: F[A], y: F[A]): F[A]
}
```

**특징** - Plus (or as cats have renamed it, SemigroupK) has "_pick the first winner, ignore errors_" semantics. Although it is not expressed in its laws.[^11]

^ SemigroupK는 semigroup에서 higherkind 타입을 받을수 있는 형태로 바뀌었다 생각하시면 됩니다.

^ 그리고 아래에 중요한 특징이 있는데요. 에러는 무시하고 첫번째 성공한 값을 취하는 특징입니다.

^ 그러면 실제 사용 예를 통해서 조금더 알아보도록 하겠습니다.

---

# SemigroupK 예제를 통해 알아보기 - __IO__

IO 자료구조에 대해서는 *첫번째 값만* 취하려 한다.

만약 첫번째에서 *에러가 발생하면 무시하고 두번째 연산*을 진행한다.

```scala
// "First"만 실행이 된다. IO.apply는 lazy 연산을 하기 때문이다.
val res1 = IO { "First" } <+> IO { "Second" }
res1.unsafeRunSync()  // res1: "First"

// "First Error" 에러💥는 무시되고 두번째 값 "Second"가 나온다.
val res2 = IO.raiseError(new Exception("First Error")) <+> IO { "Second" }
res2.unsafeRunSync()  // res2: "Second"
```

^ IO타입에 대해서 알아보면

^ 앞쪽에 값이 있으면 뒤쪽의 값은 연산을 진행하지 않습니다.

^ 코드를 자세히 살펴보겠습니다.

^ IO는 Lazy연산을 하기 때문에 바로 실행되지 않습니다.

---

# SemigroupK 예제를 통해 알아보기 - __Option__

Option 자료 구조에 대해서는 _|| 연산과 같은 동작이다._

더해지는 연산이 아니다.
앞쪽에 Some 타입이면 뒷부분 연산은 진행하지 않는다.

```scala
1.some <+> none    // Some(1) 🥇
1.some <+> 2.some  // Some(1) 🥇
none   <+> 2.some  // Some(2) 🥈
```

---

# SemigroupK를 이용한 Cache miss 처리하기

이제 이코드는

```scala
def fetch[F[_]](id: Int)(implicit F: Effect[F]): F[Option[User]] =
  fetchLocalCache(id).flatMap {                //local cache에서 데이터를 가져오고
    case cached @ Some(_) => F.pure(cached)    //그 값이 있으면 사용한다
    case None =>                               //없으면
      fetchRemoteCache(id).flatMap {           //remote cache에서 데이터를 가져오고
        case cached @ Some(_) => F.pure(cached)//그 값이 있으면 사용한다.
        case None => fetchDB(id)               //또 없으면 DB에서 가져온다.
      }
  }
```

---

# SemigroupK를 이용한 Cache miss 처리하기

훨씬 코드가 간결해졌다. 🤩  FP 만세!!!

```scala
def fetch[F[_]: Effect](id: Int): F[Option[User]] = (
  OptionT(fetchLocalCache(id))  <+>
  OptionT(fetchRemoteCache(id)) <+>
  OptionT(fetchDB(id))
).value
```

각각의 cache layer간의 코드가 커플링이 없어지고
쉽게 cache layer를 늘리거나 줄일수 있다.

---

마지막 이야기

1. 많은 양의 데이터 연동 처리
1. 비동기 필터링
2. 확장가능한 cache 전략
3. *nullable 데이터 효과적으로 다루기*

---

# 4. nullable 데이터 효과적으로 다루기

*Problem*

많은 비동기 API의 반환되는 데이터가 null이 될수 있어서
함수의 리턴타입이 _Future[Option[A]]_가 된다.

- DB에서 select one
- Cache에서 데이터를 get
- Http request에 대한 응답

Future 하나도 처리하기 힘든데 Option까지...
두개의 다른 monad_ic_이 합쳐졌다.. 어떻게 하지? 🤐

^ 이런 고민들을 많이 하게 됩니다.

^ 특히나 초반에는 Future 타입하나

^ Option 타입 하나만 다루는것도 힘들어 하는데 두개의 Monidic 타입이 중첩되어 있으면 처리하기 힘든 구조가 되어 버립니다.

^ 예를들어 보겠습니다.

---

# 4. 중첩된 모나드 Future[Option[A]]의 문제점

_order -> user -> address_를 얻는 로직이 있다 하자.
Future[Option[A]]는 _**합성할 수 없기**_ 때문에 for문 안에 지저분한 패턴 매칭이 난무한다.🙊

```scala
for {
  orderOption <- findOrder(orderId)
  userOption  <- orderOption match {
    case Some(order) => findUser(order.userId)
    case None => Future.successful(None)
  }
  address     <- userOption match {
    case Some(user) => findAddress(user.id)
    case None => Future.successful(None)
  }
} yield address
```

^ 뭔가 코드가 아름답지 못합니다.
내가 알지 못하는 뭔가가 더 있을것 같다는 생각을 했습니다.

^ FP의 끝은 간결함이니까요.

---

# 4. Monad Transformer Save Us

뭔가 아직은 *부족한* 나의 스칼라 개발 내공

구글링과 책을 읽다보니

Monad Transformer라는 녀석이 존재했다.

기능을 보고 너무 감동 받았다. 🤩

cats를 처음 사용하기 시작한 이유도
Monad Transformer를 datatype을 사용하기 위해서다.

^ 그리고 monad tranformer에 너무 심취한 나머지...

---

![fit 310%](./img/ordersky.png)

# *__그리고 어느날 github에...__*



<br/>

*__Monad transformer 관련된 이상한 공식__*을 남긴다...🤥[^19]
Monad Transformer 찬양론자가 되었습니다...
하지만 Monad Transformer는 개발 생산성 향상에 레알 도움을 준다. ✌️

<br/>
<br/>

^Monad transformer 관련된 공식 남겼습니다.
이 글의 전문은 아래 나와있는 깃헙을 확인하시면 됩니다.

^ 그리고 Monad Transformer 찬양론자가 되었습니다
Monad Transformer를 알고 나서 개발 생산성이 늘었고
코드량이 줄다보니
저의 실력도 늘었나 하는 착각이 들었습니다.

---

# Monad Transformer가 왜 필요한가?
Monad는 합성할수 없다.

이유는 두개의 Monad가 합쳐진 일반화된 _flatMap함수를 구현할 수 없기_ 때문이다. 🙅‍♀️

그래서 Monad Tranformer를 이용해 _Monad를 합성한것과 똑같은 효과_를 얻고자 한다.

Monad 합성에 대한 좀더 자세한 내용은
_Scala with Cats[^15] 5장_을 참조하면 됩니다. 🙏

![right fit](./img/monad-notcompose.png)


^ 오른쪽 코드의 flatMap 함수의 내부 구현은 할수가 없습니다.
fa에 f함수를 적용시키고 Composed[B]르 반환할 방법이 없습니다.

^ 이에 대해한 자세한 내용은 underscore에서 출판한 scala with cats의 5장을 보시면 됩니다.


---

# Monad Transformer 종류 알아보기

WriterT, StateT등 다양한 Monad Transformer가 있지만 자주 쓰이는 _OptionT_와 _EitherT_를 보면

_중간의 타입을 고정_을 한것에 주목하자.(flatMap을 구현하기 위해서)

OptionT = _monad tranformer for Option_ [^5]
**OptionT[F[_], A]** is a light wrapper on an `F[Option[A]]`

EitherT = _monad tranformer for Either_ [^6]
**EitherT[F[_], A, B]** is a light wrapper for `F[Either[A, B]]`

^ OptionT는 F와 A의 사이 중간 monad의 위치에 option이 존재합니다.

^ EitherT도 마찬가지로 F와 A의 사이 중간 monad의 위치에 either존재하게 됩니다.

^ 그리고 그 역할은 F[Option[A]]에 대한 wrapper 역할을 합니다.

^ 두개를 합친 혹은 합성한 하나의 새로운 모나드 만드는 것입니다.

^ 두개의 모나드를 하나의 모나드로 간주하는 것입니다.

---

**심화 과정 - OptionT 구현 알아보기**

너무 디테일한 내용은 피하고 싶었으나 궁금해 하실분을 위해 짧게만 😜
flatMap을 위해 _A => F[Option[A]]_를 _Option[A] => F[Option[B]]_로 변경하는 코드이다.

```scala
case class OptionT[F[_], A](value: F[Option[A]]) {

  def flatMapF[B](f: A => F[Option[B]])(implicit F: Monad[F]): OptionT[F, B] = {

    /* 문제점 : F.flatMap은 Option[A] => F[Option[B]] 함수가 필요하다.
               그러나 f 함수의 형태는 A => F[Option[B]이다.
       해결책 : Option의 패턴 매칭을 통해서 새로운 함수 fn을 만들고
               f 함수를 Option[A] => F[Option[B]] 형태로 변형하자.  */
    def fn(fa: Option[A]): F[Option[B]] = fa match {
      case Some(v) => f(v)
      case None => F.pure[Option[B]](None)
    }
    OptionT(F.flatMap(value)(fn))
  }

  def flatMap[B](f: A => OptionT[F, B])(implicit F: Monad[F]): OptionT[F, B] =
    flatMapF(a => f(a).value)
}
```

---

# 공식 - F[Option[A]]있다면 **OptionT로 감싸자**

이제 난잡한 이코드는 OptinonT로 감싸면

```scala
for {
  orderOption <- findOrder(orderId)
  userOption  <- orderOption match {
    case Some(order) => findUser(order.userId)
    case None => Future.successful(None)
  }
  addr        <- userOption match {
    case Some(user) => findAddress(user.id)
    case None => Future.successful(None)
  }
} yield addr
```

^ 이제 코드를 바꾸어 볼까요?

^ 기존에 존재하던 아주 지저분한 로직입니다.

^ 이제 난잡한 이코드는 OptinonT로 감싸면

---

# 공식 - F[Option[A]]있다면 **OptionT로 감싸자**

훨씬 코드가 간결해졌다. 🤩  FP 만세!!!

```scala
val address = for {
  order <- OptionT(findOrder(orderId))
  user  <- OptionT(findUser(order.userId))
  addr  <- OptionT(findAddress(user.id))
} yield addr
```

^ 패턴 매칭에 찌들어 있던 로직이랑은 비교가 되지 않는다.

^ 패턴 매칭은 없어지고 optionT에 대한 for comprehension으로 만 코드가 구성되었습니다.

^ 실제 패턴매칭은 없어졌다기 보다는 명확하게는 OptionT 내부로 숨겨졌다고 보는게 더 맞을것 같습니다.

---

# 실전 - 모든함수가 Future[Option[A]]를 반환하지는 않는다

*A*, *Option[A]*, *Future[A]*, *Future[Option[A]]*
이런 다른 타입을 반환하는 함수끼리 서로 합성을 할수 있어야 *real world*이다.

아래 코드는 실제 컴파일 되지 않는다. 🙅‍♂️ 타입이 맞지 않기 때문이다.
이를 적절히 lift해서 OptionT로 변환하자.

```scala
def getUserDevice(userId: String): Future[Option[Device]] = {
  val userId = parseUserId(userId)        // Option[Long]
  val user = findUser(userId)             // Future[Option[User]]
  val phoneNumber = getPhoneNumber(user)  // String
  val device = findDevice(phoneNumber)    // Future[Device]
  device
}
```

^ 하나의 함수는 Option만 반환하고

^ 함수는 Future의 Option

^ 또다른 함수는 String

^ 마지막 함수는 Future로 되어 있습니다.

^ 이렇게 서로 다른 타입을 반환하는 하는 함수를 합성할 수 있을까요?

---

# 실전 공식 - 보조함수를 이용해서 __결과타입을 OptionT로 맞추자__

```scala
- 중간에 A => Future[Option[B]] 있다면? 무조건 OptionT 이다!
- 중간에 A => Option[C]         있다면? 무조건 OptionT.fromOption[Future] 이다.
- 중간에 A => D                 있다면? 무조건 OptionT.pure[Future, A] 이다!
- 중간에 A => Future[E]         왔다면? 무조건 OptionT.liftF 이다.

- 반환하는 타입이 Future[Option[A]]라면 여기에 맞추어라 무조건!!!!

def findDeviceByUserId(userId: String): Future[Option[Device]] = (for {
  userId <- OptionT.fromOption[Future](parseUserId(userId)) // Option[Long]
  user   <- OptionT(findUser(userId))                       // Future[Option[User]]
  phone  <- OptionT.pure[Future](getPhoneNumber(user))      // String
  device <- OptionT.liftF(findDevice(phone))                // Future[Device]
} yield device).value
```

^ OptionT에 있는 내장 함수를 이용하면 다양한 타입의 OptionT로 바꿀수 있습니다.

^ Option에서 OptionT로 바꿀수 있는 fromOption

^ string에서 바로 두단계 lift해서 OptionT로 바꿀수 있는 pure함수

^ 그리고 Future의 Device 타입에서 중간에 option타입을 삽입해주는 liftF함수

^ 등 아주 편리한 보조 생성자 함수가 많이 있다.

---

# 들어주셔서 감사합니다.
 📖📚 *__Learn__*ing resources

* https://typelevel.org/cats/
> 공식 문서, 잘 정리되어 있음

* https://www.scala-exercises.org/cats
> 브라우져에서 실습보면서 공부할수 있음

* http://eed3si9n.com/herding-cats
> 아주 친절한 블로그

* https://underscore.io/training/courses/advanced-scala/
> 공짜 무료 이북 핵이득

* (광고) ㄹｒ스칼ㄹｒ코딩단 ☞ｓｌａｃｋ☜  _#ｔｙｐｅｄｒｕｇ_ 💊


---

<br/>
<br/>
# [fit] BACKUP SLIDE

---

부록 슬라이드 - 준비는 했으나 시간 관계상 🙊

Typeclass로 일관된 API사용하기
+ 그외 잡다한 이야기

---

# Monoid 위키백과 정의 [^7]

모노이드(영어: monoid)는 _항등원_을 갖는, _결합 법칙_을 따르는 이항 연산을 갖춘 대수 구조이다. 군의 정의에서 역원의 존재를 생략하거나, 반군의 정의에서 항등원의 존재를 추가하여 얻는다.

![fit right](https://pbs.twimg.com/media/DNb7Qb4W4AAr_6D.jpg)


---

# 같은 역할 다른 API

_map_, _flatMap_

Monad의 instance를 만들수 있는 monadic
Option, List, Future 같은 datatype에서는
대부분 map과 flatMap을 지원한다.

_pure_, _handleError_
하지만 상속에 의한 강제가 아니기 때문에 다를수도 있다.
- monad 생성자(pure)
- 에러처리(handleError)
와 같은 영역은 각 API마다 제각각이다.

특히 자바의 라이브러리를 사용한다면 더 다를것이다.

---

# 같은 역할 다른 API

data type | 성공 생성자 | 실패 생성자 | 에러 처리
----------|----------|-----------|---------
![](https://cdn.iconscout.com/public/images/icon/free/png-512/scala-logo-brand-development-tools-373a61865ee5f645-512x512.png) scala future | successful | failed | recover
![](https://smartblogger.com/wp-content/uploads/2014/03/twitter-logo-blue.png) twitter future | value | exception | handle
![](https://avatars3.githubusercontent.com/u/16490874?s=400&v=4) monix task | pure | onErrorRecover | raiseError

너무나도 다르다.
다른 라이브러리로의 변경이 필요하다면 고쳐야 할 부분이 너무 많다.
가장 중요한건 다른 API를 알아야 한다.
난 _암기 과목이 싫다_ 👎 👎 👎

---

# 다른 API 직접 활용하기

![](https://cdn-images-1.medium.com/max/1600/1*wZ6Mkw9xz5OKjpOMz7d2jA.gif)

---

![fit](./img/different-api.jpg)

---

<br/>
<br/>
<br/>
<br/>
# [fit] 로직은 비슷해하지만 API가 조금씩 **다르다**

![fit](./img/different-api.jpg)


---

우리는 cats의 _typeclass_를 통해서

* scala.cocurent.Future
* com.twitter.util.Future
* monix.eval.Task
* cats.effect.IO

서로 다른 라이브러이지만 _같은 API를 사용_할수 있다.

---

# 기존의 코드, scala Future의 API를 쓰고 있다.

```scala
def getItem(id: Long): ScalaFuture[Either[Throwable, ItemDto]] =
  if(id < 0) ScalaFuture.successful(
    Left(new IllegalArgumentException(s"유효하지 않은 ID입니다. $id")))
  else
    itemRepository.findById(id)
      .flatMap(item =>
        optionRepository.findByItemId(item.id)
          .map(option =>
             Right(ItemDto(item.id, item.name, option))
      .recover { case ex: Throwable => Left(ex)}
```


---

# cats API로 코드 구현하기

```scala
def getItem[F[_]](id: Long)
    (implicit F: MonadError[F, Throwable]): F[Either[Throwable, ItemDto]] =
  if(id < 0) F.pure(
    Left(new IllegalArgumentException(s"유효하지 않은 ID입니다. $id")))
  else
    itemRepository[F].findById(id)
      .flatMap(item =>
        optionRepository[F].findByItemId(item.id)
          .map(option =>
            Either.right[Throwable, ItemDto](ItemDto(item.id, item.name, option))))
      .handleError(ex => Left(ex))
```

pure, flatMap, map, handleError
모두 cats의 api를 사용하였다.

---

# 사용하는 곳에서 원하는 Effect를 주입

이함수는 MonadError에 대한 instance를 가지고 있는
_다양한 monadic data type을 수용_하는 유연한 구조가 되었다.

```scala
// before - only scala future
def getItem(id: Long): ScalaFuture[Either[Throwable, ItemDto]]
// after
def getItem[F[_]]
  (id: Long)(implicit F: MonadError[F, Throwable])
: F[Either[Throwable, ItemDto]]

// getItem을 호출하는 시점에 타입을 지정해주면 된다.
// 그러면 ScalaFuture, TwitterFuture 그리고 MonixTask가 cats api를 통해서 실행된다.
val scalaFuture = getItem[ScalaFuture](10)
val twiiterFuture = getItem[TwitterFuture](10)
val monixTask = getItem[MonixTask](10)

```

---

# 서로의 라이브러리인데 같은 API

자바의 CompletableFuture
왜 이렇게 설계했는지 모르겠다...

```scala
// java completable future
val a = CompletableFuture.completedFuture(10)
// map
val b: CompletableFuture[Int] = a.thenApply(x => x + 10)
// flatMap
val c = b.thenCompose(x => CompletableFuture.completedFuture(x * 10))
```
---


난 암기 과목이 싫다.
_thenApply_, _thenCompose_ 함수를 매번 검색해서 알아낸다.

![inline](./img/java-completable-future.png)

---

# 실습 - Monad instance 만들기

```scala
// CompletableFuture 모나드 instance만들기.
implicit val futureInstance = new Monad[CompletableFuture]
  with StackSafeMonad[CompletableFuture] {

  def pure[A](x: A): CompletableFuture[A] =
    CompletableFuture.completedFuture(x)

  def flatMap[A, B]
    (fa: CompletableFuture[A])
    (f: A => CompletableFuture[B]): CompletableFuture[B] =
    fa.thenCompose(f.asJava)

}
```

---

# Native API vs Cats API

비교하여 보자.

```scala
// Java completable future
val a = CompletableFuture.completedFuture(10)
val b = a.thenApply(x => x + 10)
val c = b.thenCompose(x => CompletableFuture.completedFuture(x * 10))
```

```scala
// Cats monad api
val a2 = 10.pure[CompletableFuture]
val b2 = a2.map(x => x + 10)
val c2 = b2.flatMap(x => (x * 10).pure[CompletableFuture])
```

---

# Real World ![](https://typelevel.org/cats/img/navbar_brand2x.png) **Cats**

개념에 너무 억매이지 말자.

Future, HashMap, HttpServer 구현하진 못해도 잘 쓰고 있다.

우리는 더 어려운 API도 잘 쓰고 있었다.

Cats API가 가져다주는 간결함과 실용성에 대해서 알아보자.

---

조금 예전버전(2015) 이지만 이게 가볍게 보기에는 좋네...[^12]

![inline](http://plastic-idolatry.com/erik/cats-graph.png)

---

# Cats의 구성 - **Typeclass**

그러나 많다고 쫄 필요없다. 🤯

시작하는 단계라면 _Functor_와 _Monad_만 알아도 된다.

```scala
trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

trait Monad[F[_]] {
  def pure[A](a: A): F[A]
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
}
```

---

# Cats의 구성 - **Data types**

역시나 많다고 쫄 필요없다. 😳

시작하는 단계라면 _OptionT_와 _EitherT_만 알아도 된다.

* OptionT = monad tranformer for Option [^5]
```OptionT[F[_], A]``` is a light wrapper on an `F[Option[A]]`

* EitherT = monad tranformer for Either [^6]
```EitherT[F[_], A, B]``` is a lightweight wrapper for `F[Either[A, B]]`

---

# Cats의 구성 - **Instance**

scala stdlib에서 제공하는 data type에 대한 cats typeclass의 instance를 제공

```scala
// .../cats/instances/all.scala
trait AllInstances
  xtends AnyValInstances
  with    ...
  with    ListInstances
  with    MapInstances
  with    OptionInstances
  with    OrderInstances
  with    OrderingInstances
  with    TupleInstances
  with    UUIDInstances
  with    VectorInstances
  with    ...
```

그리고 cats의 data type에한 instance도 제공해줌

---

![right fit](./img/my-monad-transformer1.png)

# *Do It Yourself* 정신!

구글링을하다보니 monad transformer라는 개념이 나오고

cats에 있는지 모르고 🤫


2016년 9월 25일 블로그에 있는 코드 줍기를 하며 monad transformer 직접만들어 사용한다...[^20]

---

# Monad Transformer 공식

- 함수의 리턴타입을 정하고 구현을 시작하라.
- 내부에서 호출하는 함수의 결과를 적절하게 lift하라.
- Future[Option[A]]를 반환한다면 무조건 OptionT를 고려해야한다.

---

# 3. 의문 - Monad Tranformer가 왜 필요한가?

Monad는 compose를 통해서 새로운 Monad를 만들수 없다. 🙅‍♂️
Applicative에서만 정의가 가능한 함수이기 때문이다.[^22]

```scala
trait Applicative[F[_]] {
  def compose[G[_]: Applicative]: Applicative[λ[α => F[G[α]]]] =
    new ComposedApplicative[F, G] {
      val F = self
      val G = Applicative[G]
    }
}
Monad[Future].compose[Option] // Applicative[Future[Option[α]]]
```

---

# 3. 의문 - Monad Tranformer가 왜 필요한가?


모나드가 합성을 할수 없기 때문에
Future[Option[A]]을 나타내는
_Monad를 바로 만들수 없지만_
Monad Transformer를 통해서
합성한것과 같은 효과를 얻을수 있다.[^21]

---

Cats _**Monad가 가지고 있는 다양한 함수들**_

일반적인 함수가 50여개 XXX2~22 모양을 가진함수가 60개 합쳐서 *110여개*쯤 된다.

```scala
// scala reflection으로 함수 뽑아보기
import scala.reflect.runtime.universe._
typeOf[Monad[Future]].members.map(show(_))

// 결과
widen, whileM_, whileM, whenA, void, untilM_,
untilM, unlessA, unit, tupleRight, tupleLeft,
replicateA, product, productREval, productR,
productLEval, productL, point, mproduct, map, lift,
iterateWhileM, iterateWhile, iterateUntilM,
iterateUntil, imap, ifM, fproduct, forEffect,
forEffectEval,followedBy, followedByEval, fmap,
flatten, flatTap, compose, composeFunctor,
composeContravariant, composeContravariantMonoidal,
composeApply, as, ap, <*, <*>, *>,
tuple2~22, map2Eval, map2~22, ap2~22
```

---

# cache miss에 사용한 코드들

```scala
import cats.effect.Effect
// local cache에서
def fetchLocalCache[F[_]] (key: Int)(implicit F: Effect[F]): F[Option[User]]
// remote cache에서
def fetchRemoteCache[F[_]](key: Int)(implicit F: Effect[F]): F[Option[User]]
// persistence db에서
def fetchDB[F[_]]         (key: Int)(implicit F: Effect[F]): F[Option[User]]

```

---

# [fit] 끄읕

[^1]: https://typelevel.org/cats#overview

[^2]: Book, Functional Programming Simplified - Alvin Alexander

[^3]: https://github.com/typelevel/cats

[^4]: https://typelevel.org/cats/datatypes.html

[^5]: https://typelevel.org/cats/datatypes/optiont.html

[^6]: https://typelevel.org/cats/datatypes/eithert.html

[^7]: https://ko.wikipedia.org/wiki/%EB%AA%A8%EB%85%B8%EC%9D%B4%EB%93%9C

[^8]: https://ko.wikipedia.org/wiki/%EB%AA%A8%EB%82%98%EB%93%9C_(%EB%B2%94%EC%A3%BC%EB%A1%A0)

[^9]: https://typelevel.org/cats-effect/typeclasses/sync.html

[^10]: Book, Functional Programming for Mortals with Scalaz - Sam Halliday

[^11]: https://github.com/typelevel/cats-effect/issues/94#issuecomment-351736393

[^12]: https://github.com/typelevel/cats/issues/95#issuecomment-114023955

[^13]: https://alvinalexander.com/scala/fp-book/definition-of-pure-function

[^14]: https://typelevel.org/cats/typeclasses.html

[^15]: https://underscore.io/training/courses/advanced-scala/

[^16]: https://git.io/vxKOT

[^17]: http://learnyouahaskell.com/types-and-typeclasses

[^18]: https://typelevel.org/cats/typeclasses.html#incomplete-type-class-instances-in-cats

[^19]: https://github.com/ikhoon/scala-note/blob/master/scala-exercise/src/test/scala/catsnote/MonadTransformerSaveUs.scala

[^20]: http://loicdescotte.github.io/posts/scala-compose-option-future/

[^21]: http://book.realworldhaskell.org/read/monad-transformers.html

[^22]: https://github.com/typelevel/cats/blob/2ae785f726b810438fa5b429d5c9d28f1be2e69d/core/src/main/scala/cats/Applicative.scala#L88-L92

[^23]: https://github.com/typelevel/cats-mtl/blob/f3bd9aa4c8c0466d8cb4aa25bfcc6480531929c5/core/src/main/scala/cats/mtl/TraverseEmpty.scala#L48

[^24]: https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/data/OptionT.scala

[^25]: Reactive Microservices Architecture By Jonas Bonér, Co-Founder & CTO Lightbend, Inc.

[^26]: https://typelevel.org/cats/typeclasses/traverse.html

[^27]: https://typelevel.org/cats/typeclasses/parallel.html

[^28]: `https://www.reddit.com/r/scala/comments/7qimqg/cats_parallel_future_traverse/`

[^29]: `https://ko.wikipedia.org/wiki/%ED%95%A8%EC%88%98#%EB%B6%80%EB%B6%84_%EC%A0%95%EC%9D%98_%ED%95%A8%EC%88%98_%C2%B7_%EB%8B%A4%EA%B0%80_%ED%95%A8%EC%88%98`

[^30]: https://github.com/tpolecat/cats-infographic

[^31]: https://xlinux.nist.gov/dads/HTML/totalfunc.html
