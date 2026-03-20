# 🛒 E-Commerce API Server

상품, 좋아요, 주문 기능을 제공하는 백엔드 API 서버

---

## 📌 프로젝트 개요

- **주제**: 상품, 좋아요, 주문 API 서버 구축  
- **기술 스택**:
  - Java 17
  - Spring Boot 3.x
  - Spring Data JPA
  - MySQL
- **핵심 성과**:
  - 비관적 락(Pessimistic Lock)을 활용한 재고 동시성 문제 해결
  - DB 인덱스를 통한 조회 성능 최적화
  - 실무 수준의 트랜잭션 설계 및 테스트 검증

---

## 🤖 AI 활용 내역 (핵심)

본 프로젝트는 단순 코드 생성이 아닌 **AI 협업 기반 개발 프로세스**를 적용함

### 1. 설계 단계
- Gemini를 활용하여 다음 항목에 대해 논의
  - ERD 설계
  - 도메인 구조
  - 동시성 제어 전략 (Optimistic vs Pessimistic 비교)

### 2. 구현 단계
- ChatGPT에 **정교한 프롬프트 엔지니어링**을 적용하여
  - Entity
  - Service
  - Controller
  - 테스트 코드
  초안 자동 생성

### 3. 코드 리뷰 및 개선
생성된 코드를 그대로 사용하지 않고 직접 검증 및 개선

- ❗ **N+1 문제 방지**
  - `@ManyToOne(fetch = LAZY)` 적용

- ❗ **조회 성능 최적화**
  - 비관적 락 적용 쿼리와 일반 조회 쿼리 분리

- ❗ **도메인 책임 강화**
  - 재고 감소, 좋아요 증가 로직을 Entity 내부로 캡슐화

👉 단순 AI 사용이 아닌 **설계 → 생성 → 검증 → 개선의 반복 구조**로 품질 확보

---

## ⚙️ 동시성 제어 해결 전략

### 문제
- 수백 명의 사용자가 동시에 동일 상품 주문 시
- 재고가 음수가 되는 **Race Condition 발생**

---

### 해결 방법: Pessimistic Lock (비관적 락)

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)

DB 레벨에서 row lock 획득 (SELECT ... FOR UPDATE)

하나의 트랜잭션이 작업 완료 전까지 다른 트랜잭션 접근 차단

처리 흐름

트랜잭션 시작

상품 조회 (PESSIMISTIC_WRITE)

재고 검증

재고 감소

주문 생성

트랜잭션 커밋 (락 해제)

검증 (멀티스레드 테스트)

100명 동시 주문 시나리오

ExecutorService + CountDownLatch 활용

결과

재고 정확히 0

음수 발생 ❌

데이터 정합성 100% 보장 ✅

🚀 성능 최적화
1. 인덱스 적용

상품 조회 성능 개선을 위해 정렬 필드에 인덱스 적용

필드	목적
price	가격 오름차순
likesCount	좋아요 내림차순
createdAt	최신순
@Table(indexes = {
    @Index(name = "idx_price", columnList = "price"),
    @Index(name = "idx_likes_count", columnList = "likesCount"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
2. OSIV 비활성화
spring:
  jpa:
    open-in-view: false

커넥션 점유 시간 최소화

트랜잭션 범위 명확화

실무 환경에 적합한 설정

📡 API 명세
1. 회원 (Users)
회원가입
POST /api/v1/users

Request

{
  "loginId": "user1",
  "loginPw": "1234",
  "name": "홍길동",
  "role": "USER"
}
2. 상품 (Products)
상품 등록 (ADMIN only)
POST /api/v1/products

Header

X-Huuim-LoginId
X-Huuim-LoginPw
상품 목록 조회
GET /api/v1/products

Query Params

sort: latest | price_asc | likes_desc

page: default 0

size: default 20

3. 좋아요 (Likes)
좋아요 등록
POST /api/v1/products/{productId}/likes

중복 좋아요 방지 (Unique Constraint + 사전 검증)

likesCount 증가

4. 주문 (Orders)
주문 요청
POST /api/v1/orders

Request

{
  "productId": 1,
  "quantity": 2
}

비관적 락 기반 재고 차감

동시성 100% 보장

🧠 핵심 설계 요약

동시성

PESSIMISTIC_WRITE로 DB 레벨 완전 제어

성능

인덱스 + OSIV OFF

안정성

트랜잭션 기반 처리

멀티스레드 테스트 검증 완료

아키텍처

Controller → Service → Repository

인증 로직 중앙화

📌 결론

이 프로젝트는 단순 CRUD 구현을 넘어

동시성 문제 해결

DB 레벨 성능 최적화

AI 기반 개발 프로세스 활용

까지 포함한 실무 지향 백엔드 설계 및 구현 경험을 목표로 함
