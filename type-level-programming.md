slidenumbers: true

# [fit] **Type** Level Programming in **scala**

_알_아두면 _쓸_모있을 만한 _스_칼라 _타_입에 대한 이야기

@liam.m


---

# Requirements

* Scala? 문법에 조금은 익숙하면 좋습니다.
* 졸려도 참을 수 있는 체력!
* 욕하지 않고 견딜수 있는 인내심!
* 남들이 안가는 길을 가는 용기!
* 유닛 테스트를 최대한 만들지 않겠다는 패기!

^ 앞으로 40분동안 여기 이런것들이 있으면 도움이 될것 같습니다.

^ 읽기

---

# 버그는 어떻게 예방하나요? 💊

* 테스트
* 방어 프로그래밍
* 테스트
* 코드 리뷰
* 테스트
* 테스트
* 테스트

^ 우리가 만든 코드는 불완전하기 때문에 많은 테스트를 통해서 이를 검증하고 보안합니다.


---

<br/>
<br/>
<br/>

# [fit] 코드 그 자체로 안전할 수는 없을까요?

^ 테스트 말고 다른 방법은 없을까요?

^ 코드 그 자체로 안전할 수는 없을까요?

---

<br/>
<br/>
<br/>
# [fit] Once your code compiles it usually works.[^2]

> <sub> 컴파일되면 엔간하면 동작한다.</sub>

^ 컴파일되면 엔간하면 코드를 동작하게 만드는것이
오늘의 목표입니다.

[^2]: `https://wiki.haskell.org/Why_Haskell_just_works`

---

# TDD

_TDD_ - Type driven development[^3]

컴파일이 되면 코드가 잘 동작하도록
로직이 이상하면 컴파일이 안되도록


^ Type driven development를 조금 시도해보구요

^ 컴파일이 되면 코드가 잘 동작하도록
로직이 이상하면 컴파일이 안되도록 만들어 보겠습니다.



[^3]: https://www.manning.com/books/type-driven-development-with-idris

---

# 이야기 순서

1. _Type_ safe equality - ===
2. Builder pattern using Phantom _type_
3. _Type_ class pattern - implicit
4. Literal _type_ - 42.type
5. Dependent _type_ - a.B


에 관한 _**Type**_ 이야기를 해보겠습니다. 😎


^ 오늘 이야기 할 내용은 크게 5가지 입니다.
type에 관련된 주제로 모아봤습니다.

^ 안전한 값비교를 할수 있는 type safe equality와
필수값을 컴파일 시점에 제한 할수 있는 builder 패턴


^ 함수형언어에서 많이 사용하는 패턴인
Type class pattern에 대해서도 알아보겠습니다.

^ 그리고 아직은 실험 단계에 있고 곧 표준이 될 리터럴 타입

^ 마지막으로 type level programming의 꽃이라 불리는 dependent type에 대해서 알아보겠습니다.

^ 중간에 언제든 편하게 질문하시면 되구요.

^ 나름 흥미있다고 생각되는 주제 들로 선별했는데
저만의 착각일것 같기도 합니다^^

^ 여기 있는 분들 중에 스칼라를 사용하시거나 공부하시는분?
스칼라를 더욱더 고급지게 사용할수 있는 팁을 드리겠습니다.

^ 스칼라를 안쓰시는분?
오늘 스칼라를 안쓰시는 분은 자바를 버리고 스칼라로 올수 있도록 최선을 다해 보겠습니다.


---

# Equality?

```scala
val items = List(
  Item(1, Some("ON_SALE")), Item(2, Some("SOLDOUT")),
  Item(3, Some("ON_SALE")), Item(4, None))

items.filter(item => item.status == "ON_SALE")
```

결과값은?

^ 상품 리스트가 있습니다
판매중인 상품만 필터링하려고 합니다.
결과값은 뭘까요?
손?

---

# Equality?

```scala
val items = List(
  Item(1, Some("ON_SALE")), Item(2, Some("SOLDOUT")),
  Item(3, Some("ON_SALE")), Item(4, None))

items.filter(item => item.status == "ON_SALE")
```

결과값은 Nil 아무것도 없다.

```scala
item.status: Option[String] != "ON_SALE": String
```

^ 결과값은 아무것도 없습니다.
서로 다른 타입인 `Option[String]`과 `String`을 비교했기 때문입니다.

^ 사소한 실수이지만 실제 일어날수 있습니다.

^ 선물하기 검색 API filter에서 option과 string 값을 비교해서
검색에 상품이 노출되지 않은 경우도 있습니다.

^ 어떻게 하면 해결 할수 있을까요?

---

# 유닛 테스트로 오류를 잡을수 있죠. :stuck_out_tongue_winking_eye:

```scala
test("item status filter") {
  val expected = List(
    Item(1, Some("ON_SALE")),
    Item(3, Some("ON_SALE"))
  )

  assert(items == expected) // 테스트 실패!
}
```

^ 당연히 유닛 테스트를 통해서 오류를 잡을수 있습니다.
원하는 결과가 나오지 않으면 테스트가 실패하니까요.
하지만 (다음페이지)

---

# 유닛 테스트로 오류를 잡을수 있죠. :stuck_out_tongue_winking_eye:

```scala
test("item status filter") {
  val expected = List(
    Item(1, Some("ON_SALE")),
    Item(3, Some("ON_SALE"))
  )

  assert(items == expected) // 테스트 실패!
}
```

하지만 우리가 원하는건?

^ 우리가 원하는건 유닛 테스트가 아니죠

---

# 유닛 테스트로 오류를 잡을수 있죠. :stuck_out_tongue_winking_eye:

```scala
test("item status filter") {
  val expected = List(
    Item(1, Some("ON_SALE")),
    Item(3, Some("ON_SALE"))
  )

  assert(items == expected) // 테스트 실패!
}
```

하지만 우리가 원하는건?
_Once your code compiles it usually works._

^ 엔간한 문제는 컴파일가 해결하는 것이 목표입니다.
그러면 그부분에 대한 유닛 테스트는 필요없겠죠?

^ 어떻게 하면 좋을까요?
다른 타입을 비교해서 문제가 되었으니
같은 타입에 대해서만 비교할때 컴파일되고
그렇지 않을 경우에는 오류를 발생하면 좋을것 같네요.

---

# Type safe equality

```scala
implicit class StrictEq[A](a: A) {
  def ===(b: A) = a == b
}
```

^ 여기 3줄의 코드가 있습니다.
이코드는 같은 타입에 대해서만 비교 동작을 합니다.
다른 타입이라면 컴파일이 되지 않습니다.

---

# Type safe equality

```scala
implicit class StrictEq[A](a: A) {
  def ===(b: A) = a == b
}
```

implicit class는 특정 타입에 함수를 추가할수 있다.

A 타입에 _`===`_ 함수를 추가



^ implicit class의 문법을 모르시는 분이 있다면 죄송합니다.

^ 이코드의 동작 원리에 대해서 간단하게 말씀 드리면
implicit class는 특정 타입에 함수를 추가할수 있습니다.

^ 여기서는 A 타입은 _`===`_ 함수를 추가하게 됩니다.
A타입은 따로 바운드 되어 있지 않기 때문에
모든 타입에 대해서 함수를 추가하게 됩니다.


---

이제 _**COMPILE TIME**_에 문제를 해결할수 있습니다.

```scala

val x = Some("ON_SALE")
val y = Some("SOLDOUT")
val z = "ON_SALE"

x === y      // false
x === z      // doesn't compile, 서로 다른 타입 비교안됨
```

이제 보다 안전한 값 비교. ⛑

^ 이제 _**COMPILE TIME**_에 문제를 해결할수 있습니다.

^ 서로 다른 타입에 대한 비교는 컴파일되지 않습니다.

^ 보다 안전한 값비교르 할수 있죠

^ 그런데

---

![fit](http://cfile7.uf.tistory.com/image/26502C43541B9A4106ACF9)

_===_ 를 사용하여 전체 코드를 변경하는건 귀찮다.

<br/>
<br/>
<br/>
<br/>
<br/>

어느 세월에...

^ ===를 사용하여 전체 코드를 변경하는건 귀찮습니다.
하나의 프로젝트가 아니라 여러프로젝트라면 더욱더 그렇습니다.

---

# scalacOptions += "-Xfatal-warnings"


```scala
case class UserId(n: Int)
val john = UserId(7)

john == 7
// <console>:11: warning: comparing values of
// types UserId and Int using `=='
// will always yield false john == 7
```

warning -> _**error**_

로 변경해서 다른 타입 비교는 컴파일막자 ⚠️

^ 코드를 보면 UserId라는 타입과 Int 타입을 비교했습니다.

^ 컴파일시에 발생하는 메시지를 옆의 코드를 보며 다른 타입의 비교해서 _warning_을 주고 있습니다.
이점을 활용할수 있습니다.

^ 왜냐면 스칼라의 컴파일러에는 warning을 error로 올리는 옵션이 있기 때문입니다.

^ "-Xfatal-warning"을 이용해서 컴파일하면 서로 다른 타입 비교는 컴파일 되지 않습니다.

^ 물론 warning을 error로 올리면 기존의 warning을 다 없애야 하는 번거로움이 있습니다만
적용하면 코드의 품질을 올릴수 있는 좋은 옵션이라 생각이 됩니다.

---


쉬어가는 코너

![fit](http://fruzenshtein.com/wp-content/uploads/2016/09/Why-Scala-beats-Java.jpg)


^ 벌써 졸리시진 않죠?

---


![fit](http://fruzenshtein.com/wp-content/uploads/2016/09/Why-Scala-beats-Java.jpg)

^ 자바를 디스하려고 이 그림을 넣은것은 아닙니다.
자바와 스칼라의 생태계에 대해서 이야기 하고 싶었습니다.

^ 자바8에서 lambda, stream, functional interface, optional 등이 추가되었습니다.
이기능들은 이미 오래전부터 스칼라에 존재하고 있었고 프로그래밍을 하는데 도움을 주는 기능이라고 평가 받아 왔습니다.

^ 자바에 가장 많은 영향을 주고 있는 언어중에 하나가 스칼라 인것은 확실하다고 생각합니다.

^ 오늘 이야기 할 내용들이 익숙치 않아 어렵게 느껴질수도 있지만 머지 않은 미래에
이 기능들이 자바적용될것 이라고 생각합니다.

^ 자바만 사용하실 분도 오늘 스칼라 코드를 통해 자바의 미래 예습하는것이라 생각하셔도 무관할것 같습니다.


---

# Builder pattern

"The builder pattern is a good choice when designing classes whose constructors or static factories would have more than a handful of parameters."
> Joshua Bloch, Effective Java

빌더 패턴은 좋다 :+1:

^ 이번에는 빌더 패턴에 대해서 이야기 해보겠습니다.

^ 명서죠, effective 자바에 잘 설명이 나와있는데
빌더 패턴은 많은 분들이 아시겠지만 클래스의 필드가 많을때
이를 쉽게 그리고 효과적으로 생성하기 위해 사용되는 좋은 패턴이라 생각합니다.

---

# Builder pattern

"The builder pattern is a good choice when designing classes whose constructors or static factories would have more than a handful of parameters."
> Joshua Bloch, Effective Java

빌더 패턴은 좋다 :+1: 필수값은 빌더 패턴에서 예외이다 :-1:

^ 하지만 빌더 패턴의 단점은 필수값을 제약하는건 쉽지가 않죠.

^ 우리는 역시나 컴파일러가 필수값을 제약하는걸 해보겠습니다.

---

```java
// 선물하기에서 상품을 표현하는 테이블, 필수값이 많다.
public class Item {
    private Integer id;
    private Integer brandId;
    private Integer catalogId;
    private Integer supplyChannelId;            // optional
    private String supplyChannelItemCode;
    private String supplyChannelCategoryCode;
    private String name;
    private String displayName;
    private Integer itemType;
    private Integer basicPrice;
    private Integer sellingPrice;
    private Integer discountRate;
    private Integer feeRate;
    private Byte periodType;
    private Integer validPeriod;
    private Byte pinIssueType;
    private String pinIssueCsInfo;             // optional
    private Boolean isCancelable;
    private String imageUrl;                   // optional
    private Integer status;
    private Boolean displayYn;
    private Short distType;                    // optional
    private String detailInfo;                 // optional
    private String noticeInfo;                 // optional
    private Instant couponExpiredAt;           // optional
    private Instant releasedAt;
    private Instant expiredAt;
    ... // 이보다 더 있음 총 51개의 필드
}
```

필드가 많으니 빌더 패턴이 좋겠죠?

---

객체의 필수값은 빌더의 생성자로 넘기라 - Joshua Bloch

```java
public class Item {
    public static class Builder {
        // 필수값들은 Builder의 생성자에
         public Builder(Integer id, Integer brandId, Integer catalogId, String supplyChannelItemCode,
               String supplyChannelCategoryCode, String name, String displayName, Integer itemType,
               Integer basicPrice, Integer sellingPrice, Integer discountRate, Integer feeRate,
               Byte periodType, Integer validPeriod, String pinIssueCsInfo, Byte pinIssueType,
               Boolean isCancelable, Integer status, Boolean displayYn, Instant releasedAt, Instant expiredAt) {
             this.id = id; this.brandId = brandId;  this.catalogId = catalogId;
             this.supplyChannelItemCode = supplyChannelItemCode;
             this.supplyChannelCategoryCode = supplyChannelCategoryCode;
             this.name = name;  this.displayName = displayName;  this.itemType = itemType;
             this.basicPrice = basicPrice;  this.sellingPrice = sellingPrice;
             this.discountRate = discountRate; this.feeRate = feeRate;
             this.periodType = periodType; this.validPeriod = validPeriod;
             this.pinIssueType = pinIssueType; this.pinIssueCsInfo = pinIssueCsInfo;
             this.isCancelable = isCancelable; this.status = status;
            this.displayYn = displayYn; this.releasedAt = releasedAt;  this.expiredAt = expiredAt;
        }
        // 선택값들은 보조 함수로 편리하게
        public Builder withSupplyChannelId(Integer val) {
            this.supplyChannelId = val;
            return this;
        }
        public Builder withPinIssueCsInfo(String val) {
            this.pinIssueCsInfo = val;
            return this;
        }
        ...
    }
}
```

---

근데 Builder같지 않는 builder가 만들어졌다.

```java
Item item = new Item.Builder(
        10,
        1000,
        2000,
        "I123",
        "C123",
        "(주)맥도날드",
        "맥도날드",
        101,
        5000,
        3000,
        40,
        5,
        (byte)2,
        365,
        "CS",
        (byte) 3,
        true,
        201,
        true,
        Instant.now(),
        Instant.MAX
).withNoticeInfo("공지").withImageUrl("http://builder.jpg")
 .build();
```

---

<br>
<br>
# 미션 : Builder 같은 builder만들기
- Builder의 withXXX 함수만 사용해서 필수값 제약하기

---

```scala
/* 스칼라 버전 Item.scala */
case class Item(
  id:                          Int,
  brandId:                     Int,
  catalogId:                   Int,
  supplyChannelId:             Option[Int],  // 옵션 타입 말고는
  supplyChannelItemCode:       String,       // 필수값으로 받고 싶다.
  supplyChannelCategoryCode:   String,
  name:                        String,
  displayName:                 String,
  itemType:                    Int,
  basicPrice:                  Int,
  sellingPrice:                Int,
  discountRate:                Int,
  feeRate:                     Int,
  periodType:                  Byte,
  validPeriod:                 Int,
  pinIssueType:                Byte,
  pinIssueCsInfo:              Option[String],
  isCancelable:                Boolean,
  imageUrl:                    Option[String],
  status:                      Int,
  displayYn:                   Boolean,
  distType:                    Option[Short],
  detailInfo:                  Option[String],
  noticeInfo:                  Option[String],
  couponExpiredAt:             Option[Instant],
  releasedAt:                  Instant,
  expiredAt:                   Instant
)
```
^ 스칼라 버전으로 만들어보았습니다. 훨씬더 깔끔하죠?

---

이제 필드의 빌드 상태를 표현하는 type을 만들자.

^ 먼저 빌드 상태를 표현하는 type을 만들겠습니다.


---

이제 필드의 빌드 상태를 표현하는 type을 만들자.

Why?

^ 보통 상태를 표현할때는 flag값을 활용하는데요
이미 true false를 표현하는 boolean값이 있는데
왜 만들까요?

---


이제 필드의 빌드 상태를 표현하는 type을 만들자.

Why? Compiler는 type만 아는 type 바보

^ 컴파일러는 값을 알지는 못하고 type만 알수 있습니다.

---


이제 필드의 빌드 상태를 표현하는 type을 만들자.

Why? Compiler는 type만 아는 type 바보

```scala
// 타입 제약에만 쓰인다.
sealed trait BuildState {

  type Id <: Bool // True이면 가격은 추가됨

  type Name <: Bool  // True이면 이름은 추가됨
}
```

^ 문법이 익숙하지 않을수도 있는데요.
Price와 Name의 빌드 상태를 타입으로 표현했습니다.
BuildState는 값이 아니라 타입을 가지고 있습니다.

^ 그러면 값을 타입으로 표현하는 방법에 대해서 알아보겠습니다.

---

# 값을

```scala
sealed trait Bool

val True = new Bool {}
val False = new Bool {}

val hasOption: Bool = True
```

^ 간단한 불린 값을 True, False를 만들었습니다.

^ 이 불린은 값에 의해서 동작합나다.
컴파일러는 값에 대해서는 체크를 하지 못합니다.
왜냐면 값은 런타임의 영역이기 때문입니다.

^ 이제 값을 타입으로 바꾸어 보겠습니다.

---

# 값을 타입으로 바꾼다

```scala
sealed trait Bool

sealed trait True extends Bool
sealed trait False extends Bool

type HasOption = True
```

^ 차이점이 보이시나요?
뒤에것이랑 다시하면 비교해보겠습니다.

^ 앞에는 true와 false를 변수 val에 할당을 했지만
이건 trait으로 설정해서 True타입과 false 타입을 만들었습니다.

^ 그리고 hasOption에는 값을 할당하는것을
타입을 할당하는 것으로 바꾸었습니다.

---


# 값을 타입으로 바꾸는 규칙들

- `ADT Values : val → trait`

```scala
val True = new Bool {}        ➜     trait True extends Bool
```

^ 값을 타입으로 바꾸는 규칙에 대해서 자세히 알아보면
알지브래릭 데이터 타입을 이루는 True와 False는 trait으로 별도의 타입으로 구성됩니다.

^ 혹시 알지브래릭 데이터 타입, ADT 타입에 대해서 잘 모르시는 분은 스칼라 아지트에 제가 따로 글을 올리겠습니다.
죄송합니다.

---

# 값을 타입으로 바꾸는 규칙들

- `ADT Values : val → trait`

```scala
val True = new Bool {}        ➜     trait True extends Bool
```

- `members    : val → type X`

```scala
val hasOption: Bool = True    ➜     type HasOption = True
```

^ 그리고 hasOption과 같은 멤버 변수 할당은
type 키워드를 이용해서 타입을 할당으로 바꾸어 주면 됩니다.

^ 이외에 몇가지 더있지만 스킵

---

# 참과 거짓을 타입으로 표현했다

```scala

sealed trait Bool

// 값을
val True = new Bool {}
val False = new Bool {}

val hasOption: Bool = True

// 타입으로 바꾼다
sealed trait True extends Bool
sealed trait False extends Bool

type HasOption = True
```

^ 한꺼번에 봐보겠습니다.
값을 타입으로 표현할수 있습니다.

---

Name, DisplayName에 값이 생성되면 True 타입을 할당

```scala
class Builder[B <: BuildState] { self =>
  private var name: Option[String] = None
  private var displayName: Option[String] = None

  def newBuilder[C <: BuildState] = this.asInstanceOf[Builder[C]]

  def withName(name: String) = {
    self.name = Some(name)
    newBuilder[B {type Name = True}]
  }

  def withDisplayName(displayName: String) = {
    self.displayName = Some(displayName)
    newBuilder[B {type DisplayName = True}]
  }
}
```

^ 상품을 상품의 전시을 설정하는 builder입니다.

^ withName안에 Builder에서 Name 타입에 True를 설정하는것을 볼수 있습니다.
값을 할당하는것이 아니라 타입을 할당하고 있습니다.

---

값이 생성되었는지는 타입에 True 타입이 할당 되었는지로 판단

```scala
class Builder[B <: BuildState] { self =>
  def build(implicit
    ev1: B#Id =:= True,   // True 타입이 할당되어야만 컴파일됨
    ev2: B#BrandId =:= True,
    ev3: B#CatalogId =:= True,
    ev4: B#SupplyChannelItemCode =:= True,
    ev5: B#SupplyChannelCategoryCode =:= True,
    ev6: B#Name =:= True,
    ev7: B#DisplayName =:= True,
    ev8: B#ItemType =:= True,
    ev9: B#BasicPrice =:= True,
    ev10: B#SellingPrice =:= True,
    ev11: B#DiscountRate =:= True,
    ev12: B#FeeRate =:= True,
    ...
  ): Item =
    Item(id.get, brandId.get, catalogId.get, supplyChannelId, supplyChannelItemCode.get,
      supplyChannelCategoryCode.get, name.get, displayName.get, itemType.get, basicPrice.get,
      sellingPrice.get, discountRate.get, feeRate.get, periodType.get, validPeriod.get,
      pinIssueType.get, pinIssueCsInfo, isCancelable.get, imageUrl, status.get, displayYn.get,
      distType, detailInfo, noticeInfo, couponExpiredAt, releasedAt.get, expiredAt.get)
}
```

^ build 함수를 호출할때는 Name, DisplayName이 True 타입인지를 타입 체크를 하게 됩니다.

---

선택값의 경우는 BuildState의 타입을 바꿀필요가 없다.

```scala
class Builder[B <: BuildState] { self =>
  private var name: Option[String] = None
  private var detailInfo: Option[String] = None  // optional

  def withName(name: String) = {                 // 필수값
    self.name = Some(name)
    newBuilder[B {type Name = True}]
  }

  def withDetailInfo(detailInfo: String) = {     // 선택값
    self.detailInfo = Some(detailInfo)
    newBuilder[B]
  }
}
```

^ 그리고 withName과 withDistType를 비슷 방법으로 구현할수 있는데
Name을 설정하때는 builder에 True를 설정하는것을 볼수 있습니다.

^ 그리고 tag에는 타입을 변환시키지 않습니다.

^ 그리고 실행을 해보겠습니다.

---

<br>
<br>
# [fit] 잘동작하는지 확인해보자


---

```scala
object Builder {
  def apply() = new Builder[BuildState {}]
}

// 필수값이 설정되지 않았기 때문에 컴파일 되지 않는다
Builder().build                    // doesn't compile, 오 컴파일 안된다. 나이스

Builder()
  .withBasicPrice(5000).build      // doesn't compile, 오오 이것도 컴파일 안된다.
```

^ 옆에 코드 보면서 설명

^ 그냥 초기화나 price만 초기화해서 빌드를 하려고 하면 컴파일이 되지 않습니다.

---


```scala
// 컴파일 된다. withXXX 함수만을 이용해 필수값을 제약. 아주 맘에 듭니다.✌️
val item = Builder()
    .withId(10)
    .withBrandId(1000)
    .withCatalogId(2000)
    .withSupplyChannelItemCode("I123")
    .withSupplyChannelCategoryCode("C123")
    .withName("(주)맥도날드")
    .withDisplayName("맥도날드")
    .withItemType(101)
    .withBasicPrice(5000)
    .withSellingPrice(3000)
    .withDiscountRate(40)
    .withFeeRate(5)
    .withPeriodType(2)
    .withValidPeriod(365)
    .withPinIssueType(3)
    .withIsCancelable(true)
    .withStatus(201)
    .withDisplayYn(true)
    .withReleasedAt(now)
    .withExpiredAt(Instant.MAX)
    .build
```

^ 옆에 코드 보면서 설명

^ 모양이 그럴듯합니다. 개인적으로는 아주 마음에 듭니다.

^ 스칼라의 case class의 기본 생성자를 제공해줍니다.

---

```scala
// named argument를 이용한 객체 생성... 그냥 이게 나을려나? 뻘짓했나? ☠️
val item = Item(
  id = 10, brandId = 1000, catalogId = 2000,
  supplyChannelId = None, supplyChannelItemCode = "I123",
  supplyChannelCategoryCode = "C123", name = "(주)맥도날드",
  displayName = "맥도날드", itemType = 101,
  basicPrice = 5000, sellingPrice = 3000,
  discountRate = 40, feeRate = 5,
  periodType = 2, validPeriod = 365,
  pinIssueType = 3, pinIssueCsInfo = None,
  isCancelable = true, imageUrl = None,
  status = 201, displayYn = true,
  distType = None, detailInfo = None,
  noticeInfo = None, couponExpiredAt = None,
  releasedAt = now, expiredAt = Instant.MAX
)
```

^ 스칼라에서는 name argument라 해서 python처럼 argument의 이름에 값을 할당할수 있습니다.
이를 이용하면 builder 패턴과 비슷한 효과를 낼수 있습니다.
Builder pattern 보다는 가독성이 떨어지지만 간단하게 빌더를 흉내내고 싶다면 이형태를 추천합니다.



---

<br>
<br>

# [fit] 그래도 괜찮지 않았나요?

^ 이정도면 괜찮지 않나요?

^ 오버인가요? ^^

^ 할수 있다는건 중요한 점 이라고 생각합니다.
type level 이를 활용해서 다양한 곳에서 활용하고 있습니다.


---

# 너무 갑자기 훅 치고 들어왔유?

![fit](http://ppss.kr/wp-content/uploads/2013/12/image7.jpeg)

<br>
<br>
<br>
<br>
<br>
<br>
<br>
벌써 졸릴각?

^ 혹시 졸리시는 계신가요? 그렇다면 죄송합니다.
좀더 분발해보겠습니다.

---

# Type class Pattern

다양한 타입에 대한 합을 구하는 함수 _sum_을 구현하고 싶다.
<br>

```scala
case class Point(x: Int, y: Int) // 2차원 좌표

sum(List(1, 2, 3))                  => 6
sum(List(Point(1,10), Point(5, 5))  => Point(6, 15)
```

^ 정수, 실수에 대한 합을 구하고 2차원 좌표에 대한 합도 구하는
그런 함수를 만들어 보려고 합니다.

---

<br>
<br>

```scala
trait Adder[A] {
  def zero: A
  def add(x: A, y: A): A
}
```

^ 두개의 값에 대해서 합을 구하는 인터페이스를 설계해보겠습니다.
add 함수를 통해서 두개의 값을 더하는것을 가능하도록 하고
zero 함수를 통해서 초기값, 항등원을 주도록 하겠습니다.

---

<br>
<br>

```scala
trait Adder[A] {
  def zero: A
  def add(x: A, y: A): A
}

def sum[A](xs: List[A])(adder: Adder[A]): A =
  xs.foldLeft(adder.zero)(adder.add)
```

^ 그리고 list에 내장된 foldLeft함수에
초기값으로 adder의 zero를 주고
합치는 연산은 adder의 add함수를 통해서 하도록 구현할수 있습니다.

^ 이 패턴의 장점은 합치는 방법을 미리 정하지 않고
주입 받기 때문에 다양한 adder의 구현체를 사용하는 곳에서 주입해서 사용할수 있습니다.

---

<br>

```scala
val intAdder = new Adder[Int] {
  def zero: Int = 0
  def add(x: Int, y: Int) = x + y
}

val pointAdder = new Adder[Point] {
  def zero = Point(0, 0)
  def add(a: Point, b: Point) = Point(a.x + b.x, a.y + b.y)
}
```

^ 그리고 합을 구하는 함수를 구현하면 됩니다.
옆에 int타입과 point타입에 대한 adder를 구현하였습니다.
간단하게 더하기 연산을 통해서 합을 구할수 있습니다.

---

# 잘된다.
<br>

```scala
sum(List(1, 2, 3))(intAdder)                      // 7
sum(List(Point(1, 10), Point(5, 5)))(pointAdder)  // Point(6, 15)
```

^ 결과값은 예상한데로 나오구요
잘됩니다.
하지만(다음페이지)

---

# 잘된다. 그러나 뭔가 아름답지 못하다.
<br>

```scala
sum(List(1, 2, 3))(intAdder)                     // <= 달고 다니기 귀찮다.
sum(List(Point(1, 10), Point(5, 5)))(pointAdder)  // <= 여기도
```

^ intAdder와 pointAdder를 달고 다니는 것은 아름답지 못합니다.
왜냐면 int 더하는 방법과 point을 더하는 방법은 뻔하기 때문입니다.

---

# Type class pattern 이란?

```scala
def sum[A](xs: List[A])(implicit adder: Adder[A]): A
```

Ad-hoc polymorphism - type parameter를 이용해서 implicit instance 주입 받아 *polymorphism*하는 기법

DI - compiler가 *dependency injection*


> Spring, Guice는 runtime에 DI가 실행

^ 이런 문제를 해결할수 있는것이 type class pattern입니다.

^ 타입 클래스 패턴은 Ad-hoc polymorphism이라 불리며
type parameter를 이용해서 implicit instance 주입 받아 *polymorphism*하는 기법

^ 이때 implicit instance를 컴파일러가 찾아서 의존성을 주입하게 됩니다.

^ 스프링이나 쥬스가 런타임에 DI를 하는 프레임웍과 달리
컴파일러가 DI를 실행합니다.

^ 이렇게 했을때의 장점은 스프링은 실행을 해봐야지만 특정 타입의 DI를 injection 할수 있을지 없을지 알수 있지만
type class pattern은 컴파일만 해봐도 알수 있습니다.

^ 기존의 코드를 바꾸어 type class 패턴이 되도록 해보겠습니다.(다음페이지)

---

# implicit은 마법사 :dizzy:
```scala
// Adder를 implicit하게 주입
def sum[A](xs: List[A])(implicit adder: Adder[A]): A =
  xs.foldLeft(adder.zero)(adder.add)

// type class instance 도 implicit 하게
implicit val intAdder = new Adder[Int] {
  def zero = 0
  def add(x: Int, y: Int) = x + y
}

implicit val pointAdder = new Adder[Point] {
  def zero = Point(0, 0)
  def add(a: Point, b: Point) = Point(a.x + b.x, a.y + b.y)
}
```

^ 방법은 간단합니다.
기존의 로직을 변경하지 않고 implicit 키워드를 추가만 하면 됩니다.

^ 여기서 중요한 점은 implicit 키워드를 넣을때는 쌍으로 넣어줘야 합니다.
주입 해주는 곳과 주입받는 곳입니다.

---

<br>
<br>

```scala
sum(List(1, 2, 3))(intAdder)
sum(List(Point(1, 10), Point(5, 5)))(pointAdder)
```

^ 그러면 원래 있었던 intAdder는 (다음페이지)

---

<br>
<br>

```scala
sum(List(1, 2, 3))
sum(List(Point(1, 10), Point(5, 5)))
```

^ 뿅하고 사라집니다. 훨씬 깔끔하죠?

^ 함수가 어떤 행동을 하는지 가독성도 좋습니다.
그리고 (다음페이지)

---

<br>
<br>

```scala
sum(List(1, 2, 3))
sum(List(Point(1, 10), Point(5, 5)))

// String은 합칠 방법을 모르기 때문에 compile이 안된다.
sum(List("Hello", "World"))  // doesn't compile

// 깔끔 깔끔 🤵
```

^ 스트링은 합치는 방법을 제공해주지 않았기 떼문에 컴파일시에 오류가 발생하게 됩니다.
예측되지 않는 타입에 대한 sum함수 호출은 컴파일 타입에 잡을수 있습니다.
좋습니다.

---

# String도 더하고 싶다면?

```scala
// string instance만 추가하면 된다.
implicit val stringAdder = new Adder[String] {
  def zero = ""
  def add(x: String, y: String) = x + y
}


sum(List("Hello", "World"))  // => "HelloWorld"
```

^ 만약 스트링 타입에 대해서 합치는 연산을 하고 싶다면
string에 대한 adder instance를 구현해주면 됩니다.
확장성도 있습니다.

^ 그리고 스트링을 합치는 방법을 커스텀마이징하기도 쉽습니다.

---

# 회사에서 뽀큐라니 죄송합니다. ㅠ.ㅠ
# 이 차트 그래프가 젤 괜찮아서...
# 정보를 전달하고 싶어서 양해하고 바주세요 ㅠ.ㅠ
# 핫 라인에 신고하지 말아 주세요 ㅠ.ㅠ
# 그냥 손가락 부상 당한 주먹이라 생각해주세요. ㅠ.ㅠ

![fit](https://pbs.twimg.com/media/CuF-eDJWgAE5h_U.jpg)

---

![fit](https://pbs.twimg.com/media/CuF-eDJWgAE5h_U.jpg)

^ 스칼라를 배우다 보면
좀더 정확히 이야기하면 함수형 언어를 배우다 보면 시련이 찾아오는데요.

^ 저도 아직 모르는 부분이 많아 계속 공부하고 노력하고 있구요
어느순간 그 지점을 극복하면 광명이 찾아옵니다.
어려운 시점이 왔으때 조금만 더 참고 공부하시면 될것입니다.

^ 사실 제가 여기서 발표하는것도 웃긴데요
카카오는 저보다 뛰어나신분들이 훨씬더 많은걸로 알고 있습니다.
많이 많이 배우고 싶습니다.


---


# Literal type - 42.type

값은 타입이 될수 없을까?

^ 이번에 조금 실험적인 내용인데요
리터럴 타입에 대해서 이야기 해보겠습니다.

^ 값은 타입이 될수 없을까?
이런 고민을 하게 됩니다.

---

# Literal type - 42.type

값은 타입이 될수 없을까? 됩니다. :ok_hand:

```scala
val t: 42 = 42
val x: "Jedi" = "Jedi"
```

^ 네 될수 있습니다.

---

# Literal type - 42.type

값은 타입이 될수 없을까? 됩니다. :ok_hand:

```scala
val t: 42 = 42
val x: "Jedi" = "Jedi"
```
함수의 반환타입은?

^ 함수의 반환타입은요?

---

# Literal type - 42.type

값은 타입이 될수 없을까? 됩니다. :ok_hand:

```scala
val t: 42 = 42
val x: "Jedi" = "Jedi"
```
함수의 반환타입은? 됩니다. 🙆‍♂️

```scala
def f(t: Double): t.type = t
val a: 1.2 = f(1.2)
```

^ 리터럴 타입은 타입에 미친 표현력의 날개를 달게 하는데요
기존에 Int, String, Boolean과 마찬가지로
42, jedi, true와 같은 값도 타입이 될수 있도록해줍니다.

---

# Literal type in scala

Scala에는 곧 추가될 예정 <sub>_http://docs.scala-lang.org/sips/pending/42.type.html_</sub>

Typelevel Scala에는 반영됨 <sub>_https://typelevel.org/scala/_</sub>

Dotty도 이미 구현됨 <sub>_http://dotty.epfl.ch/docs/reference/singleton-types.html_</sub>

미리 준비하고 익숙해지자.
Literal singleton type은 표준이 될것이고 이를 활용하는 library들은 점점더 많아질것이다.

^ scala에는 곧 추가가 될 예정이고
typelevel scala와 dotty에는 이미 구현되어 있습니다.

^ 아직은 shapeless와 같은 third party library 통해서만 리터럴 타입이 표현이 가능하지만
이제 곧 스칼라의 표준이 될것이고 많은 라이브러리에서 이를 활용하여 코드를 작성할것 입니다.

---

# true.type을 이용한 if 없는 condition

```scala
trait Cond[T] { type V ; val value: V }

implicit val condTrue = new Cond[true] { type V = String ; val value = "foo" }

implicit val condFalse = new Cond[false] { type V = Int ; val value = 23 }

def cond[T](implicit cond: Cond[T]): cond.V = cond.value

//  true is type! 👻
cond[true]        // "foo"

// flase is type! 😲
cond[false]       // 23
```

^ 간단한 샘플 코드를 준비했는데요
여기서 타입 파라메터 위치에 true가 타입의 형태로 위치하게 됩니다.
다들 아시겠지만 true는 1로 대변되는 참 값이죠. 하지만 싱글톤 타입이 되었습니다.
싱글톤 타입인 이유는 true는 프로그램 내에서 유일한 존재이기 때문입니다.

^ 코드를 보면 true일때는 "foo" false일때는 23을 반환하는 조건식을
if문 없이 작성할수 있습니다.

^ 리터럴 타입이 없다면 이코드는 불가능하겠죠.

---

# Path Dependent type - 경로 의존적인 제약

```scala
class A {
  class B
  var b: Option[B] = None
}
```

^ 이제 마지막 장인 Path dependent type에 대해서 알아보겠습니다.

^ A class에는 자바의 inner class와 유사한 B class가 있습니다.

---

# Path Dependent type - 경로 의존적인 제약

```scala
class A {
  class B
  var b: Option[B] = None
}
val a1: A = new A
val a2: A = new A

val b1: a1.B = new a1.B  // a1.B는 타입이다.
val b2: a2.B = new a2.B  // a1.B와 a2.B는 다른 타입이다.
b1 === b2 // 컴파일 에러
```

^ 이를 어떻게 활용하는지 보면
여기 두개의 a1, a2 변수가 있습니다.
그리고 a1과 a2 내부에는 B라는 타입이 존재합니다.

^ a1의 B와 a2의 B를 객체로 생성하면 그 객체의 타입은 a1.B와 a2.B입니다.

^ a1.B와 a2.B가 타입으로 선언된 변수 b1, b2를 확인할수 있습니다.
이 두개의 타입은 경로 의존적입니다.

^ a1.B와 a2.B는 다른 타입입니다.

---

# Path Dependent type - 경로 의존적인 제약

```scala
val b1: a1.B = new a1.B  // a1.B는 타입이다.
val b2: a2.B = new a2.B  // a1.B와 a2.B는 다른 타입이다.

a1.b = Some(b1)
a2.b = Some(b1) // does not compile
```

Dependent Type Programming

^ 서로 다른 타입이기 때문에
a2.b에는 a1.b인 b1의 할당 될수 없습니다.
이런 특징을 이용한 프로그래밍을 dependent type programming이라고 부릅니다.

^ 그러면 dependent type의 활용예를 살펴보겠습니다(다음페이지)

---

# Map - 타입 정보를 정확하게 유지한다.


```scala
val strIntMap: Map[String, Int] = Map("width" -> 120)
```

^ Map이란 자료 구조는
List와 함께 우리가 가장 많이 사용하는 자료 구조입니다.

^ 타입 정보도 정확하게 표현하고 있구요

---

# Map - 타입 정보를 정확하게 유지한다.

```scala
val strIntMap: Map[String, Int] = Map("width" -> 120)
```

Sting이 Map에 Value 타입으로 들어온다면?

^ 스트링 타입이 Map의 Value타입으로 들어오면
타입은 어떻게 바뀌나요?

---

# Map - 타입 정보를 정확하게 유지하지 못한다.

```scala
val strIntMap: Map[String, Int] = Map("width" -> 120)
```

Sting이 Map에 Value 타입으로 들어온다면? Any

```scala
val strAnyMap: Map[String, Any] = strIntMap + ("sort" -> "time")
```

^ 타입 정보가 사라졌기 때문에 우리는 안전한 코드를 작성하기 힘듭니다.

---

# Map - 타입 정보를 정확하게 유지하지 못한다.

```scala
val strIntMap: Map[String, Int] = Map("width" -> 120)
```

Sting이 Map에 Value 타입으로 들어온다면? Any

```scala
val strAnyMap: Map[String, Any] = strIntMap + ("sort" -> "time")

// 타입 정보는 어디로 갔나?
val width: Option[Any] = map2.get("width")
val sort: Option[Any] = map2.get("sort")
```

^ 저장된 값을 나중에 get을 하면 결과 타입을 알수가 없습니다.

---

# HMap - Heterogenous Map[^1]

Dependent type을 이용해 타입 정보를 보존해보자!

^ 그럼 dependent type이용해서 이종의 데이터 타입을 보존할수 있는
헤티로지노스 맵을 구현해보겠습니다.

[^1]: Dotty : https://www.slideshare.net/Odersky/from-dot-to-dotty

---

# HMap - Heterogenous Map[^1]

Dependent type을 이용해 타입 정보를 보존해보자!

```scala
trait Key { type Value }
trait HMap {
  def get(key: Key): Option[key.Value]  // key의 Value, dependent type!
  def add(key: Key)(value: key.Value): HMap
}
```

^ 코드는 이렇습니다.

^ 간단하죠?
이 코드는 스칼라의 창시자인 마틴 오더스키가
dotty로 불리는 scala 3를 소개하는 키노트에서 발췌했습니다.

^ 여기서 눈여겨 봐야할것은 Value가 Key의 의존적인 타입이라는 점입니다.
이를 이용해서 서로 다른 타입들에 대한 정보를 보전할수 있는데요.

^ 사용예를 보면 (다음페이지)

---

```scala
val sort = new Key { type Value = String }
val width = new Key { type Value = Int }
```


^ sort와 width Key를 만들때 결과값에 대한 타입을 지정하구요
sort는 String 타입
width는 Int 타입을 저장할수 있도록 했습니다.

---

```scala
val sort = new Key { type Value = String }
val width = new Key { type Value = Int }
```
저장할때 Key와 연관된 Value 타입만 저장가능

```scala
val hmap: HMap = HMap.empty
 .add(width)(120)
 .add(sort)("time")
 .add(width)(true)  // doesn't compile, width는 Int Value를 가진다.
```

^ 값을 저장할때는 width에 int가 정확하게 들어와야지만 저장이 되구요
아래 보시면 width에 boolean 타입의 true를 저장하려고 하면 에러가 발생하고
컴파일이 되지 않습니다.

---

```scala
val sort = new Key { type Value = String }
val width = new Key { type Value = Int }
```
저장할때 Key와 연관된 Value 타입만 저장가능

```scala
val hmap: HMap = HMap.empty
 .add(width)(120)
 .add(sort)("time")
 .add(width)(true)  // doesn't compile, width는 Int Value를 가진다.
```

값을 가져올때 Value 타입이 온전히 유지된다.

```scala
val optionInt: Option[Int] = hmap.get(width)
val optionString: Option[String] = hmap.get(sort)
```

^ 그리고 값을 가져올때는 Int, String의 타입을 정확하게 가져옵니다.
이를 통해서 unchecked casting을 하지 않아도 됩니다.

---

HMap 구현은?
Martin Ordersky 발표자료에 안나와있다. 😰

^ HMap의 구현은 마틴오더스키의 발표자료에 나와있지 않아서
제가 임의로 구현을 했습니다.



---

# HMap 똵! 9줄


```scala
trait HMap { self =>
  val underlying: Map[Any, Any]

  def get(key: Key): Option[key.Value] =
    underlying.get(key).map(_.asInstanceOf[key.Value])

  def add(key: Key)(value: key.Value): HMap =
    new HMap {
      val underlying = self.underlying + (key -> value)
    }
}
```

^ 생각 보다 코드가 복잡하지 않구요
get을 할때 key의 Value의 타입으로 casing 해주는 로직만 추가하였습니다.

^ 특정 라이브러리에 의존하지 않고 순순하게 스칼라 문법만 가지고 HMap을 구현할수 있습니다.

---

# [fit]Believing that: Life is Study![^4]

![inline](https://pbs.twimg.com/profile_banners/54490597/1404948426)

^ 이 이미지는 비동기, 분산 처리 라이브러리로 유명한 아카팀 리딩 개발자 콘레드의 트위터 프로필 사진인데요.

^ 많은 개발자들이 존경하고 컨퍼런스에서 발표도 많이 하시는 분인데

^ 이미 많은 조명을 받고 있는 뛰어난 개발자들도 계속 공부하면서 노력하고 있다합니다.

^ 우리도 파이팅합시다

[^4]: https://twitter.com/ktosopl

---

# [fit] Nil

Once your code compiles it usually works.

^ 자료가 부족해서 죄송합니다.
많은 분들을 모시고 좋은 내용을 전해 드리고 싶었는데


---

# [fit] Back up Slide

시간이 남았다면?

---

# [fit] Back up Slide

시간이 남았다면?

우리는 서비스를 하는데 너무 코드 이야기만 했나요?

---

# [fit] Back up Slide

시간이 남았다면?

우리는 서비스를 하는데 너무 코드 이야기만 했나요?

조금더 실용적인 이야기를 해볼까요?


---

# [fit] Back up Slide

시간이 남았다면?

우리는 서비스를 하는데 너무 코드 이야기만 했나요?

조금더 실용적인 이야기를 해볼까요?


---

# Scala in Real World.

Parallel Programming in Micro Service Architecture.[^5]

^ 얼마전에 마이크로 서비스와 병렬 프로그래밍에 대한
글을 썼는데 인기가 없어서 다시 홍보차 공유 드립니다.

^ 함수형 이야기 보다 마이크로 서비스가 더 흥미 진진할수 있을것 같네요.

[^5]: http://tech.kakao.com/2017/09/02/parallel-programming-and-applicative-in-scala/

---

![](http://i.imgur.com/iHPlf0U.png)

^ 마이크로 서비스는 모노리틱 서비스와 표면적으로 하나의 차이를 가지고 있습니다.

^ 작게 쪼개져 있습니다.
그리고 각각의 마이크로 서비스는 자신만의 저장소를 가지고 있습니다.

---

# 모노로틱 아키텍쳐에서 개발하기
대부분의 데이터는 한곳에 저장되어 있다.

```sql
SELECT *
FROM
  items, catalogs, wishes, categories, details, certificiations
WHERE
    items.id = ?
AND items.id = catalogs.itemId
AND items.id = wishes.itemId
...
```

^ 모노리틱 아키텍쳐에서는 한곳에 데이터가 저장되어 있는 경우가 많습니다.
꼭 그렇다는것은 아니지만 대부분의 경우에 그렇다고 생각합니다.

^ 그러면 우리가 정규화된 테이블에서 데이터를 가져오려면
테이블 join을 통해서 가져오는 경우가 많습니다.

---

# 마이크로 서비스에서 개발하기

데이터는 분산되어 있습니다.

```scala
val product: Future[ProductDto] = for {
   item <- itemReposotory.findByid(itemId)                       // 분리
   catalog <- catalogRepository.findById(item.catalogId)         // 되어
   brand <- brandRepository.findById(item.brandId)               // 있으니
   wish <- itemWishCountRepository.findByItemId(item.id)         // 독립적으로
   category <- categoryRepository.findOneByBrandId(item.brandId) // 데이터를
   detail <- itemDetailRepository.findByItemId(item.id)          // 가져
   cert <- itemCertificationRepository.findByItemId(item.id)     // 온다
} yield ProductFactory.of(item, brand, catalog, wish, category, detail, cert)
```

^ 데이터가 분산 되어 있기 때문에 별로의 API를 호출하여햐 합니다.
이 코드는 scala future의 flatMap을 이용해서 비동기를 연산을 수행하며
데이터를 가져옵니다.

---

# 순서는? 넘나 질서 정렬한것

![inline](http://i.imgur.com/jvMJ3nq.png)

^ item => catalog => brand => wish => category => detail => cert
아이템부터 순차적으로 데이터를 가져옵니다.

---

# 비동기로 동작하기만 병렬은 아니다.

![inline](http://i.imgur.com/AQt1rOb.png)

^ flatMap은 특성상 순차 연산을 하도록 설계가 되어 있습니다.

---

# 병렬 프로그래밍!!!  🚀


![inline](http://i.imgur.com/v3Frr4O.png)

^ 마이크로 서비스를 하면서 병렬 프로그래밍의 니즈는 더욱더 많아 집니다.
병렬 프로그래밍을 구현하면 훨씬더 빠른 응답을 줄수가 있습니다.

---

# 어떻게 병렬 프로그래밍을 할수 있을까요?

방법은 다양하지만 _Applicative_를 이용한 방법을 추천합니다.

---

# Applicative?

![](http://ppss.kr/wp-content/uploads/2017/03/4-10.jpg)

---

# Applicative - 그림 버전

![inline](http://adit.io/imgs/functors/applicative_just.png)

^ 그림에서 처럼 두개의 container를 한꺼번에 열어서 함수를 적용하고 닫는것입니다
두개를 한꺼번에 연다는것은 두개를 동시에 열수(parallel)도 있다는 뜻입니다.

---

# Applicative - 코드 버전

```scala
trait Applicative[F[_]] {
  def pure[A](a: A): F[A]

  def ap[A, B](fa: F[A])(ff: F[A => B]): F[B]
}
```

^ 생각보다 간단합니다. 여기서는 ap함수에만 집중하면 됩니다.
fa와 ff를 동시에 꺼내서 적용하고 fb를 반환하면 됩니다.

---

# Applicative - ap함수 구현하기

```scala
import scala.concurrent.Future

val futureAp = new Applicative[Future] {
  def pure[A](a: A) = Future.successful(a)

  def ap[A, B](fa: Future[A])(ff: Future[A => B]): Future[B] =
    ff.zip(fa).map { case (f, a) => f(a)}
}
```

^ scala future의 zip함수를 활용하면 두개의 future를 합칠수 있습니다.
이를 이용해서 ap함수를 구현하면(다음장)

---

# Applicative can be parallel

```scala
itemRepository.findById(itemId).flatMap { item =>
  (
    catalogRepository.findById(item.catalogId) |@|
    brandRepository.findById(item.brandId) |@|
    itemWishCountRepository.findByItemId(item.id) |@|
    categoryRepository.findOneByBrandId(item.brandId) |@|
    itemDetailRepository.findByItemId(item.id) |@|
    itemCertificationRepository.findByItemId(item.id)
  ).map { case (catalog, brand, wish, category, detail, cert) =>
    List(brand, catalog, wish, category, detail, cert)
  }
}
```

^ 여러개의 future를 쉽게 합칠수 있습니다.
실제 scala의 future인 zip을 활용해서 합치려고 하면 생각보다 문법이 복잡해지고 쉽게 합쳐지지 않습니다.

^ 좀더 자세한 내용은 카카오 tech 블로그에 있는 글을 참조 부탁드립니다.
감사합니다.

---

# [fit] 끝


---

---


# Backgroud - Phantom type :ghost:

Phantom(유령) type은 Compile time에만 존재하는 타입

Runtime에는 사라짐

```scala
trait Phantom                  // 빈 트레잇을 만듬
type MyInt = Int with Phantom  // Phantom type을 mixin한 타입을 만듬
```

^ 스칼라에는 phantom 타입이 있습니다.
Phantom 타입은 유령이란 이름에서 느껴지듯이
컴파일시에만 존재를 하고 컴파일 후에는 사라지는 타입입니다.

^ 여기에서 phantom 타입을 이용해서 Int의 하위 타입을 생성했습니다.


---

# 타입에 관한 문제점
Int has **no meaning**
A Int can contains **anything**

Int는 42억 9496만 7296개의 숫자로 이루어져 있습니다.

^ Int는 42억 9496만 7296개의 숫자로 이루어져 있습니다.
아주 크지 않은 어떤 숫자값이나 담을수 있죠.
Int나 String을 인자로 받는 함수는 언제나 순서에 신경을 써야합니다.

---

# 타입에 관한 문제점

```scala
def findItemsBy(brandId: Int, itemId: Int,
                status: Int, page: Int, size: Int = 20) =
  items.filter(_.brandId == brandId).drop(page * size).take(size)

// ItemService.scala
// 아무 문제없이 잘 동작하는 코드이다.
findItemsByBrandId(brandId, itemId, status, page, size)
```


^ 여기 코드에는 page와 size를 받는 간단한 함수가 있습니다.

---

# 코드가 변하면?


```scala
// catalogId가 추가된다, 보기 이쁘게 Id끼리 묶어놔야지
def findItemsBy(brandId: Int, itemId: Int,catalogId: Int,
                status: Int, page: Int, size: Int = 20) =
  items.filter(_.brandId == brandId).drop(page * size).take(size)


// BrandService.scala
// API를 변경한 사람은 신경써서 넣었다.
findItemsByBrandId(brandId, itemId, catalogId, status, page, size)

```

^ Int는 42억 9496만 7296개의 숫자로 이루어져 있습니다.
아주 크지 않은 어떤 숫자값이나 담을수 있죠.
Int나 String을 인자로 받는 함수는 언제나 순서에 신경을 써야합니다.
컴파일러가 체크를 해줄수 없기 때문이죠.
여기 코드에는 page와 size를 받는 간단한 함수가 있습니다.
이함수는 인자의 순서를 바꾸어도 잘동작 합니다.
다만 결과값은 저희가 의도한데로 나오지 않지만요.

---

# 하지만 과거의 코드는?


```scala
// catalogId가 추가된다, 보기 이쁘게 Id끼리 묶어놔야지
def findItemsBy(brandId: Int, itemId: Int,catalogId: Int,
                status: Int, page: Int, size: Int = 20) =
  items.filter(_.brandId == brandId).drop(page * size).take(size)


// BrandService.scala
// API를 변경한 사람은 신경써서 넣었다.
findItemsByBrandId(brandId, itemId, catalogId, status, page, size)

// ItemService.scala
// 그리고 기존에 호출하던 코드는 컴파일이 잘된다?
findItemsByBrandId(brandId, itemId, status, page, size)
```

^ Int는 42억 9496만 7296개의 숫자로 이루어져 있습니다.
아주 크지 않은 어떤 숫자값이나 담을수 있죠.
Int나 String을 인자로 받는 함수는 언제나 순서에 신경을 써야합니다.
컴파일러가 체크를 해줄수 없기 때문이죠.
여기 코드에는 page와 size를 받는 간단한 함수가 있습니다.
이함수는 인자의 순서를 바꾸어도 잘동작 합니다.
다만 결과값은 저희가 의도한데로 나오지 않지만요.

---

물론 유닛 테스트 열심히 짜면 다 해결할수 있습니다.

하지만 컴파일러도 뭔가 해줄수 있는게 있지 않을까요?


---

# User defined type

Compiler가 도움 줄수 있게 각각의 인자에 대한 타입을 만들자.

```scala
case class ItemId(value: Int) extends AnyVal
case class CatalogId(value: Int) extends AnyVal
case class BrandId(value: Int) extends AnyVal
case class Status(value: Int) extends AnyVal
case class Page(value: Int) extends AnyVal
case class Size(value: Int) extends AnyVal
```

^ 이를 해결할수 있는 방법중에 하나는
별도의 타입을 만드는것입니다.
그러면 이 함수를 사용하는 입장에서는 타입의 인자를 맞출수 밖에 없습니다.
네 좋습니다.

---

# 만든 타입을 이용해서 API바꾸자

```scala
def findItemsByBrandId(
  brandId: BrandId, itemId: ItemId, catalogId: CatalogId,
  status: Status, page: Page, size: Size = Size(20)
): List[Item] =
  items.filter(_.brandId == brandId.value)
    .drop(page.value * size.value)
    .take(size.value)

findItemsByBrandId(brandId, itemId, status, page, size) // doesn't compile, 실수 방지 😉
```

^ 이를 해결할수 있는 방법중에 하나는
별도의 타입을 만드는것입니다.
그러면 이 함수를 사용하는 입장에서는 타입의 인자를 맞출수 밖에 없습니다.
네 좋습니다.


---

# 그러나? 내부 구현이 다 바뀌었다. 🤔

```scala
// before
def findItemsBy(
  brandId: Int, itemId: Int,catalogId: Int,
  status: Int, page: Int, size: Int = 20): List[Item] =
  items
    .filter(_.brandId == brandId)
    .drop(page * size)
    .take(size)

// after
def findItemsByBrandId(
  brandId: BrandId, itemId: ItemId, catalogId: CatalogId,
  status: Status, page: Page, size: Size = Size(20)): List[Item] =
  items.filter(_.brandId == brandId.value)
    .drop(page.value * size.value)  // 여기
    .take(size.value)  // 여기도
```

좋은건가? :+1: 나쁜건가? :-1:

^ 우리가 기존에 만들었던 함수의 모양이 변형되었습니다.
좋은 걸까요? 나쁜걸까요?

^ 좋을수도 있고 나쁠수도 있는데요
단점에 대해서 살펴 보면

---

# Page type의 단점
값을 Int 하나만 가지고 있는 타입이지만 Int와 호환되지 않는다.

```scala
case class Page(value: Int) extends AnyVal
```
만약 Page가 Int의 하위 타입이 된다면? _*Page extends Int*_

Page는 Int로 상위 타입으로 변환이 될수 있다.

```scala
val page: Int = Page(1) // 상위 타입변환이 될것이다.
```

^ 이렇게 된다면 Page는 Int의 특별한 부분 집합이 될것이다.
어떻게 하면 이럴게 구현할수 있을까요?

---

# shapeless 도움 살짝만 받겠습니다.


```scala
import shapeless._
val shapely =
  "Dependent type programming"
 :: "Generic type programming" :: HNil
```


https://github.com/milessabin/shapeless

<sub>이번이 처음이자 마지막으로 도움입니다.</sub>

^ shapeless 살짝 도움 받겠습니다.
사실 shapeless가 중요한건 아닌데
혹시라도 신경 쓰시는 분이 있을까봐 말씀드립니다.

---

# Tagged type :ghost:

Phantom(유령) type은 Compile time에만 존재하는 타입(복습)
TypeTag @@은 Phantom type 타입 생성자

```scala
import shapeless.tag.@@
type Page = Int @@ PageTag  // Page는 Int와 PageTag 상속 받은 타입

val page: Page = tag[PageTag][Int](1) // 생성하는건 좀 구리지만, 완전 나쁘진 않다

```

^ 스칼라에는 phantom 타입이 있습니다.
Phantom 타입은 유령이란 이름에서 느껴지듯이
컴파일시에만 존재를 하고 컴파일 후에는 사라지는 타입입니다.

^ PageTag는 컴파일 타임에는 타입 제약을 하고
컴파일이 완료가 되면 유령처럼 흔적없이 사라집니다.


---

# Tagged type :ghost:

Phantom(유령) type은 Compile time에만 존재하는 타입
TypeTag @@은 Phantom type 타입 생성자

```scala
import shapeless.tag.@@
type Page = Int @@ PageTag  // Page는 Int와 PageTag 상속 받은 타입

val page: Page = tag[PageTag][Int](1) // 생성하는건 좀 구리지만, 완전 나쁘진 않다

// 실행시에는 PageTag는 사라지고 Int만 남음
page.getClass   // => class java.lang.Integer

// 그리고 Int로 변형된다.
val int : Int = page
```

^ 스칼라에는 phantom 타입이 있습니다.
Phantom 타입은 유령이란 이름에서 느껴지듯이
컴파일시에만 존재를 하고 컴파일 후에는 사라지는 타입입니다.

^ PageTag는 컴파일 타임에는 타입 제약을 하고
컴파일이 완료가 되면 유령처럼 흔적없이 사라집니다.

^ 코드를 보면

---

# Phantom type을 이용하면

* 특정 타입의 하위 타입을 손쉽게 만들수 있습니다.
* 상속이 불가능한 타입(final)의 하위 타입도 만들수 있습니다.
* API나 로직의 변경 없이 호환되는 코드를 작성 할수 있습니다.
* 런타임에는 사라지기 때문에 성능에 대한 추가 이슈도 없습니다.

---

```scala
import shapeless.tag.@@
trait BrandIdTag; trait ItemIdTag
trait BrandTag; trait ItemTag
trait PageTag; trait SizeTag
object Pagable {
  type Page = Int @@ PageTag  // Page는 Int의 하위 타입
  type Size = Int @@ SizeTag  // Size도 Int의 하위 타입
}

def findItemsByBrandId(brandId: Int, page: Page, size: Size): List[Item] =
  items.filter(_.brandId == brandId)
    .drop(page * size) // Page => Int 로 바로 변환이 된다! 😎
    .take(size)        // 바뀌지 않음

val page: Page = tag[PageTag][Int](1)
val size: Size = tag[SizeTag][Int](20)

findItemsByBrandId(1, page, size)    // works
// 이제 page와 size의 순서가 바뀔일은 없다.
findItemsByBrandId(1, size, page)    // doesn't compile

```

^ phantom 타입을 이용해서 만든 코드는 Int와 완벽하게 호환이 되기 때문에
우리가 처음 만들었던 코드와의 같은 로직을 쓸수 있습니다.

^ 직접 클래스를 만드는 것보다 Phantom type을 만드는것이 더 좋다고 말하는것은 아니구요
두개의 방식이 각각 장단점이 있고 타입의 제약이 필요한 상황에 적절히 활용하면 좋을것 같습니다.

^ 이번엔 builder pattern에 대해 한번 이야기 해보겠습니다.
