## Scala Stream 이야기

Scala 진영에서 stream 을 처리 할수 있는 라이브러리는 크게 3가지로 볼수 있을것 같습니다.
akka streams, fs2, monix의 observable 입니다.
이들 라이브러리에 대한 비교는 구글링 해보면 글이 좀있습니다.


이중에 사용예는 많은량의 데이터에 대한 처리에 대해서 간단하게 말씀드릴께요.

많은량의 데이터에 대한 처리
1억 row의 데이터를 한번에 메모리에 가져와서 처리하려고 하면 memory overflow가 발생합니다.
이를 해결하기 위해서 paginate를 활용해서처리 할수가 있는데요.

memory overflow를 피하기 위한 본능적인 접근
```scala
val PAGE_SIZE = 100
var currentPos = 0
rows = []
while (rows.nonEmpty) {
  rows = select * from big_table where id > currentPos and id <= currentPos + PAGE_SIZE
  rows.map { ... }
  currentPos = currentPos + PAGE_SIZE
}
```
를 하게 되면 필요한 로직보다는 paging하는데 더 많은 코드가 들어가게 되고 조금 실수를 해서 rows에 update를 안하면 무한 루프에 빠질수도 있습니다.

이런부분은 stream의 lazy 연산을 통해서 필요한 부분 만큼만 조금씩 데이터를 가져오는걸 할수 있습니다.

그걸 위해서 조금 배경지식인 reactive-streams에 대해서 말씀드리겠습니다.

### reactive-streams
reactives는 reactive programming을 위한 spec입니다. 그리고 jvm에 대한 스펙은 여기에 잘 나와 있습니다.
이 표준 reactive streams를 스펙을 구현한 publisher는 이를 지원하는 스트림으로 변형이 가능합니다.

즉 akka streams, fs2, monix obersable은 모두 reactive streams의 Publisher를 stream의 형태로 변경이 가능합니다.
이렇게 변경하는 이유는 publisher는 low level api이기 때문에 observable이나 stream으로 변형을 하고 rich한 api를 사용하기 위해서 입니다.


### slick의 stream활용하기
slick의 stream은 Publisher를 상속 받은 DataPublisher를 반환 합니다. publisher는 연산자가 거의 없기(1개)때문에 바로 사용하기 쉽지 않습니다.
이를 stream을 라이브러리로 변형을 하면 filter, map, flatMap과 같은 high level 연산을 사용할수 있습니다.

#### publihser to stream
publihser를 스트림으로 변형하는 방법은 아래의 라이브러리의 factory 함수를 활용하시면 됩니다.
* Monix : `Observable.fromPublisher`
* Akka Streams : `Source.fromPublisher`
* fs2 Stream : fs2 reactive streams 라이브러리 활용
```scala
import fs2.interop.reactivestreams._
val stream = publisher.toStream[IO]
```

### 주의점
다만 slick의 stream은 그냥 사용하면 memory overflow가 생겨서 몇가지 코드를 설정을 넣어줘야 하는데 mysql 기준으로
enableJbcStreaming과 fetchSize를 Int.MinValue를 넣어주면 정상적으로 잘 동작합니다. 
[doobie 참조](https://github.com/tpolecat/doobie/issues/715) 
[slick 참조](https://github.com/slick/slick/issues/1218)

```scala
trait SlickStreaming {

  val enableJdbcStreaming: (java.sql.Statement) => Unit = { statement =>
    if (statement.isWrapperFor(classOf[com.mysql.jdbc.StatementImpl])) {
      statement
        .unwrap(classOf[com.mysql.jdbc.StatementImpl])
        .enableStreamingResults()
    }
  }

  implicit class JdbcStreamOps(db: Database) {
    def jdbcStream[A](dbio: StreamingDBIO[Seq[A], A], fetchSize: Int = Int.MinValue): DatabasePublisher[A] =
      db.stream(
        dbio
          .withStatementParameters(statementInit = enableJdbcStreaming, fetchSize = fetchSize)
      )
  }
}

object SlickStreaming extends SlickStreaming
```
활용코드
이와 같은 형태로 쓸수 있으며 이제 위의 while loop의 코드는

```scala
import SlickStreaming._
import fs2.interop.reactivestreams._
def findAll: DataPublihser[Table] = db.jdbcStream(table.result) // 테이블의 데이터 다가져옴 
val rows = findAll.toStream[IO]
rows
  .map { ... }
  .filter { ...  }
  .flatMap { ... }
```
와 같이 쓸수 있습니다.

이외에도 제가 몇가지 사용예는
ListT monad transformer 대신 사용, websocket, http subsrcription 와 같은곳에도 stream은 가장 적합한 데이터 모델 같습니다.