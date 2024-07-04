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

