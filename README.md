# 마일스톤
- 링크 : https://www.edrawmax.com/online/share.html?code=1e8cc8663a2811efa9330a54be41f961

# 시퀀스 다이어 그램
<details>
<summary>잔액 조회</summary>

### 잔액 조회
```mermaid
sequenceDiagram
    title 잔액 조회
    participant 사용자
    participant API
    participant 지갑
    participant 지갑DB as 지갑 DB
    participant 유저
    participant 유저DB as 유저 DB

    사용자 ->> API: 잔액 조회 요청
    API ->> 지갑: 지갑 및 사용자 정보 기반 요청

    지갑 ->> 지갑DB: 지갑 조회
    지갑DB -->> 지갑: 지갑 조회 결과
    지갑 -->> 사용자: 유효하지 않은 지갑
    지갑 ->> 유저: 유저 조회
    유저 ->> 유저DB: 유저 조회
    유저DB -->> 유저: 유저 조회 결과
    유저 -->> 사용자: 유효하지 않은 사용자
    유저 -->> 지갑: 유저 정보
    지갑 -->> 사용자: 지갑과 사용자의 정보가 일치하지 않음

    지갑 -->> API: 지갑 정보
    API -->> 사용자: 잔액 정보
```
</details>

<details>
<summary>잔액 충전</summary>

### 잔액 충전
```mermaid
sequenceDiagram
    title 잔액 충전
    participant 사용자
    participant API
    participant 지갑
    participant 지갑DB as 지갑 DB
    participant 유저
    participant 유저DB as 유저 DB

    사용자 ->> API: 잔액 충전 요청
    API ->> 지갑: 지갑 및 사용자 정보, 충전 금액 기반 요청

    지갑 -->> 사용자: 유효하지 않은 충전 금액 (음수)
    지갑 ->> 지갑DB: 지갑 조회
    지갑DB -->> 지갑: 지갑 조회 결과
    지갑 -->> 사용자: 유효하지 않은 지갑
    지갑 ->> 유저: 유저 조회
    유저 ->> 유저DB: 유저 조회
    유저DB -->> 유저: 유저 조회 결과
    유저 -->> 사용자: 유효하지 않은 사용자
    유저 -->> 지갑: 유저 정보
    지갑 -->> 사용자: 지갑과 사용자의 정보가 일치하지 않음

    지갑 ->> 지갑: 지갑 잔액 수정
    지갑 ->> 지갑DB: 지갑 잔액 업데이트
    지갑DB -->> 지갑: 업데이트 결과

    지갑 -->> API: 업데이트된 지갑 정보
    API -->> 사용자: 지갑 정보
```
</details>


<details>
<summary>토큰 발급</summary>

### 토큰 발급
```mermaid
sequenceDiagram
    title 토큰 발급
    participant 사용자
    participant API
    participant 토큰
    participant 유저
    participant 유저DB as 유저 DB
    participant 토큰DB as 토큰 DB

    사용자 ->> API: 토큰 발급 요청
    API ->> 토큰: 토큰 발급 요청

    토큰 ->> 유저: 유저 조회
    유저 ->> 유저DB: 유저 조회
    유저DB -->> 유저: 유저 조회 결과
    유저 -->> 사용자: 유효하지 않은 사용자

    토큰 ->> 토큰: 토큰 객체 생성
    토큰 ->> 토큰DB: 토큰 데이터 생성
    토큰DB -->> 토큰: 토큰 데이터 생성 결과

    토큰 -->> API: 토큰 정보
    API -->> 사용자: 토큰 정보
```
</details>

<details>
<summary>토큰 정보 조회</summary>

### 토큰 정보 조회
```mermaid
sequenceDiagram
    title 토큰 정보 조회
    participant 사용자
    participant API
    participant 토큰
    participant 토큰DB as 토큰 DB

    사용자 ->> API: 토큰 정보 조회
    API ->> 토큰: 토큰 기반 정보 조회

    토큰 ->> 토큰DB: 토큰 조회
    토큰DB -->> 토큰: 토큰 조회 결과
    토큰 -->> 사용자: 유효하지 않은 토큰

    토큰 -->> API: 토큰 정보
    API -->> 사용자: 토큰 정보 (남은시간, 유저 정보 등)
```
</details>

<details>
<summary>예약 가능 날짜 조회</summary>

### 예약 가능 날짜 조회
```mermaid
sequenceDiagram
    title 예약 가능 날짜 조회
    participant 사용자
    participant API
    participant 콘서트상세 as 콘서트 상세
    participant 토큰
    participant 토큰DB as 토큰 DB
    participant 콘서트상세DB as 콘서트 상세 DB

    사용자 ->> API: 예약 가능 날짜 조회
    API ->> 콘서트상세: 토큰 기반 예약 정보 요청

    콘서트상세 ->> 토큰: 토큰 조회
    토큰 ->> 토큰DB: 토큰 조회
    토큰DB -->> 토큰: 토큰 조회 결과
    토큰 -->> 사용자: 유효하지 않은 토큰
    토큰 -->> 사용자: 해당 토큰이 대기열에 포함되어있지 않음
    콘서트상세 ->> 콘서트상세DB: 예약 가능 콘서트 상세 리스트 조회
    콘서트상세DB -->> 콘서트상세: 예약 가능 콘서트 상세 리스트 결과

    콘서트상세 -->> API: 예약 가능 콘서트 상세 리스트
    API -->> 사용자: 예약 가능 콘서트 상세 리스트
```
</details>

<details>
<summary>예약 가능 좌석 조회</summary>

### 예약 가능 좌석 조회
```mermaid
sequenceDiagram
    title 예약 가능 좌석 조회
    participant 사용자
    participant API
    participant 좌석
    participant 토큰
    participant 토큰DB as 토큰 DB
    participant 콘서트상세 as 콘서트 상세
    participant 콘서트상세DB as 콘서트 상세 DB
    participant 좌석DB as 좌석 DB

    사용자 ->> API: 예약 가능 좌석 조회
    API ->> 좌석: 토큰, 콘서트 정보 기반 좌석 정보 요청

    좌석 ->> 토큰: 토큰 조회
    토큰 ->> 토큰DB: 토큰 조회
    토큰DB -->> 토큰: 토큰 조회 결과
    토큰 -->> 사용자: 유효하지 않은 토큰
    토큰 -->> 사용자: 해당 토큰이 대기열에 포함되어있지 않음
    좌석 ->> 콘서트상세: 콘서트 상세 조회
    콘서트상세 ->> 콘서트상세DB: 콘서트 상세 조회
    콘서트상세DB -->> 콘서트상세: 콘서트 상세 조회 결과
    콘서트상세 -->> 사용자: 유효하지 않은 콘서트 상세
    콘서트상세 -->> 사용자: 예약이 가능한 날짜가 아닌 경우
    좌석 ->> 좌석DB: 좌석 리스트 조회
    좌석DB -->> 좌석: 좌석 리스트 조회 결과

    좌석 -->> API: 좌석 전체 리스트
    API -->> 사용자: 좌석 리스트 (좌석 예약 가능/불가능 구분하여 표시)
```
</details>

<details>
<summary>좌석 임시 예약 요청</summary>

### 좌석 임시 예약 요청
```mermaid
sequenceDiagram
    title 좌석 임시 예약 요청
    participant 사용자
    participant API
    participant 좌석
    participant 토큰
    participant 토큰DB as 토큰 DB
    participant 콘서트상세 as 콘서트 상세
    participant 콘서트상세DB as 콘서트 상세 DB
    participant 좌석DB as 좌석 DB

    사용자 ->> API: 좌석 임시 예약 요청
    API ->> 좌석: 토큰, 좌석 기반 좌석 임시 예약 요청

    좌석 ->> 토큰: 토큰 조회
    토큰 ->> 토큰DB: 토큰 조회
    토큰DB -->> 토큰: 토큰 조회 결과
    토큰 -->> 사용자: 유효하지 않은 토큰
    토큰 -->> 사용자: 해당 토큰이 대기열에 포함되어있지 않음
    좌석 ->> 좌석DB: 좌석 조회
    좌석DB -->> 좌석: 좌석 조회 결과
    좌석 -->> 사용자: 유효하지 않은 좌석
    좌석 -->> 사용자: 이미 예약된 좌석
    좌석 ->> 콘서트상세: 콘서트 상세 조회
    콘서트상세 ->> 콘서트상세DB: 콘서트 상세 조회
    콘서트상세DB -->> 콘서트상세: 콘서트 상세 조회 결과
    콘서트상세 -->> 사용자: 유효하지 않은 콘서트 상세
    콘서트상세 -->> 사용자: 예약할 수 있는 기간이 아닌 경우

    좌석 ->> 좌석: 좌석 객체 생성
    좌석 ->> 좌석DB: 좌석 데이터 생성
    좌석DB -->> 좌석: 좌석 데이터 생성 결과

    좌석 -->> API: 생성된 좌석 정보
    API -->> 사용자: 좌석 정보
```

</details>


<details>
<summary>결제 요청</summary>

### 결제 요청
```mermaid
sequenceDiagram
    title 결제 요청
    participant 사용자
    participant API
    participant 결제
    participant 토큰
    participant 토큰DB as 토큰 DB
    participant 좌석
    participant 좌석DB as 좌석 DB
    participant 결제내역 as 결제 내역
    participant 결제내역DB as 결제 내역 DB

    사용자 ->> API: 좌석 임시 예약 요청
    API ->> 결제: 토큰, 좌석 기반 좌석 결제 요청

    결제 ->> 토큰: 토큰 조회
    토큰 ->> 토큰DB: 토큰 조회
    토큰DB -->> 토큰: 토큰 조회 결과
    토큰 -->> 사용자: 유효하지 않은 토큰
    토큰 -->> 사용자: 해당 토큰이 대기열에 포함되어있지 않음
    결제 ->> 좌석: 좌석 조회
    좌석 ->> 좌석DB: 좌석 조회
    좌석DB -->> 좌석: 좌석 조회 결과
    좌석 -->> 사용자: 유효하지 않은 좌석
    좌석 -->> 결제: 좌석 정보
    결제 -->> 사용자: 임시 예약되지 않은 좌석 (임시 예약 후 5분(특정시간)이 지난 경우도 동일)
    결제 -->> 사용자: 토큰의 사용자 정보와 임시 예약한 사용자 정보 불일치

    결제 ->> 결제: 결제 처리
    결제 ->> 결제내역: 결제 정보
    결제내역 ->> 결제내역: 결제 내역 객체 생성
    결제내역 ->> 결제내역DB: 결제 내역 데이터 생성
    결제내역DB -->> 결제내역: 결제 내역 데이터 생성 결과
    결제 ->> 토큰: 토큰 대기열 완료 처리
    토큰 ->> 토큰: 토큰 대기열 정보 업데이트
    토큰 ->> 토큰DB: 토큰 대기열 데이터 업데이트
    토큰DB -->> 토큰: 토큰 대기열 데이터 업데이트 결과

    결제 -->> API: 결제 완료
    API -->> 사용자: 결제 완료
```
</details>

# ERD 설계
- 링크 : https://dbdiagram.io/d/콘서트-예약-6684e7659939893daee5ee0f


# 동시성 이슈
<details>
<summary>프로젝트내에서 동시성 문제가 발생할 수 있는 케이스</summary>

### 프로젝트내에서 동시성 문제가 발생할 수 있는 케이스
- 좌석 임시 예약 (좌석 -> 분산락(Simple Lock) + 비관적락(S-Lock))
  - 여러명의 사용자가 동일한 좌석을 선택하여 예약하려고 하는 경우 분산락을 통해 한 명의 사용자에게만 Lock을 제공하며 빠르게 에러 처리
    - redis에 문제가 발생하는 경우 별도의 처리 필요...
  - 비관적락(S-Lock)을 사용하여, 다른 트랜잭션에서의 조회에 영향을 주지 않게 하여 비관적락 (X-Lock)에 비해 성능 향상
- 좌석 결제 (좌석 -> 낙관적 락, 지갑 -> 비관적 락 (X-Lock))
  - 좌석 결제는 한명의 사용자가 임시 예약한 좌석을 대상으로 결제를 하는 로직이기에 동시성 이슈가 많이 발생하지 않기에 낙관적 락 사용
  - 좌석에 대해 DB Lock을 사용하지 않고 있기에, DB Lock을 적용하는것에 비해 성능 향상
- 잔액 충전 (지갑 -> 비관적 락 (X-Lock))
  - 지갑의 경우 잔액에 대한 무결성을 지킬 수 있음
  - 여러명의 사용자가 동일한 계정으로 충전 및 결제를 하는 경우 모두 무결성을 지키며 처리 가능
</details>

<details>
<summary>비교한 락의 종류</summary>

### 비교한 락의 종류
- 낙관적 락
  - DB 락이 아닌 비지니스 로직에 대한 Lock
  - 트랜잭션 내에서 저장시 @version이 조회시점과 동일한지 확인 후 그렇지 않다면 Exception 발생
  - 장점
    - DB에 직접 Lock을 요청하는 것이 아닌, 저장 시점에 Version을 비교하는것이기에 속도가 다른 락에 비해 빠르다
  - 단점
    - 충돌이 많이 발생하는경우 오히려 비관적 락 보다 효율이 떨어질 수 있다.
    - 충돌로 인하여 재시도를 해야하는 경우 재시도 로직을 추가해야한다. 
    - Entity에 낙관적 락을 위한 필드가 추가되어야 한다. (ex. 번호 or 타임스탬프 ...)
- 비관적 락 (DB 락)
  - S-Lock (PESSIMISTIC_READ)
    - 다른 트랜잭션에서도 읽기는 가능하나, 쓰기가 불가능 한 Lock
    - 장점
      - 트랜잭션에서 S-Lock을 사용하는 동안 일관성 있는 데이터를 조회 할 수 있다. 
      - 여러 트랜잭션에서도 동일한 데이터를 조회할 수 있다.
    - 단점
      - 쓰기 작업이 들어가있는 경우 X-Lock 으로 승격되며, 여러 트랜잭션이 X-Lock 으로 승격할 시 Deadlock 이 발생한다.
        - 쓰기 작업을 진행하기 위해 S-Lock 이 모두 종료될 때까지 기다려야 하지만, 다른 트랜잭션에서도 수정을 하기 위해 대기하기때문에 Deadlock 발생
  - X-Lock (PESSIMISTIC_WRITE)
    - 다른 트랜잭션에서 읽기와 쓰기가 모두 불가능한 Lock
    - 장점
      - Lock을 획득할때 까지 대기하며, 모든 처리가 진행된다. (대기 시간이 길어질 경우 실패)
      - 다른 트랜잭션에서 읽기와 쓰기가 불가능하기에, 데이터에대한 무결성이 보장된다.
    - 단점
      - 한 트랜잭션이 Lock을 가지고 있으면, 다른 트랜잭션은 아무런 작업을 할 수 없어 서버 부하를 야기시킨다.
      - 트랜잭션의 범위가 클 경우 Deadlock 발생 확률이 올라간다.
- Redis를 이용한 분산 락 (다른 락과 함께 사용할 수 있다)
  - SimpleLock
    - key를 선점하여 Lock을 얻는 방식
    - 장점
      - 구현이 간단하며 속도가 빠르다.
    - 단점
      - Redis에 문제 발생하는 경우 정상적인 처리가 어렵다.
  - RedLock (Redisson 라이브러리 사용)
    - SimpleLock과 동일하게 key를 선점하지만, 여러개의 Redis를 사용하여 과반수 이상에서 락을 획득해 사용한다.
    - 장점
      - 여러개의 Redis를 사용하기에, 단일 Redis의 문제가 발생하는 경우 정상적인 처리가 가능하다
    - 단점
      - 최소 3개 이상의 Redis 인스턴스가 필요하다.
  - Spin Lock
    - 해당 쓰레드가 Lock을 얻을때 까지 계속해서 요청하는 방식
    - 장점
      - ???... 잘모르겠습니다 ㅜㅜ
    - 단점
      - 계속해서 요청을 하기에 자원을 낭비하게 되며 비효율 적이다.
</details>   


<details>
<summary>락 적용 시 보여지는 시퀀스 다이어그램</summary>

### 락 적용 시 보여지는 시퀀스 다이어그램
#### 좌석 임시 예약 (좌석 -> 분산락(Simple Lock) + 비관적락(S-Lock))
![스크린샷 2024-07-26 오전 3 27 08](https://github.com/user-attachments/assets/00436409-6cbd-4c41-833e-f2bd7550864d)
#### 좌석 결제 (좌석 -> 낙관적 락, 지갑 -> 비관적 락 (X-Lock))
![스크린샷 2024-07-26 오전 3 27 37](https://github.com/user-attachments/assets/ee220e28-869d-44bc-8a3f-11c23ddc5bd8)
#### 잔액 충전 (지갑 -> 비관적 락 (X-Lock))
![스크린샷 2024-07-26 오전 3 24 31](https://github.com/user-attachments/assets/71790cf1-a41e-4562-b68c-524f723612c9)
  

</details>