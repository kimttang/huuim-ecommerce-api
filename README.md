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

### 2. 구현 단계 (구조화된 프롬프트 엔지니어링)
단순한 코드 생성을 넘어, AI가 실무 표준에 맞는 코드를 작성하도록 **역할(Role), 맥락(Context), 제약 조건(Constraints)**을 명확히 설정한 프롬프트를 설계했습니다.

**[프롬프트 지침(Guidelines) 설정 예시]**
- **역할 및 맥락 부여**: "당신은 대규모 트래픽을 다루는 이커머스 플랫폼의 시니어 백엔드 개발자입니다. 주어진 명세서를 바탕으로 Spring Boot 3.x 환경의 코드를 작성하세요."
- **엄격한 제약 조건(Constraints) 세팅**:
  1. **DTO 설계**: "모든 Request/Response DTO는 Java 17의 `record`를 사용하여 불변 객체로 구현할 것"
  2. **안전성 확보**: "Controller의 모든 입력값에는 `@Valid`와 커스텀 에러 메시지를 적용하여 예외를 방어할 것"
  3. **JPA 최적화**: "모든 `@ManyToOne` 연관관계는 반드시 `FetchType.LAZY`를 명시하여 N+1 문제를 원천 차단할 것"
- **단계별 추론 (Chain of Thought)**: 한 번에 전체 시스템을 요구하지 않고, `도메인 설계 → 동시성 처리 로직(Repository) → 비즈니스 로직(Service)` 순서로 단계를 쪼개어 요청하여 AI의 할루시네이션(오류)을 최소화함.

### 3. 코드 리뷰 및 개선
생성된 코드를 그대로 사용하지 않고 직접 검증 및 개선

- ❗ **N+1 문제 방지**
  - `@ManyToOne(fetch = LAZY)` 적용

- ❗ **조회 성능 최적화**
  - 비관적 락 적용 쿼리와 일반 조회 쿼리 분리

- ❗ **도메인 책임 강화**
  - 재고 감소, 좋아요 증가 로직을 Entity 내부로 캡슐화
  
- ❗ **조회 성능 최적화 및 데드락 방지 (Cross-Validation)**
  - 비관적 락 적용 쿼리와 일반 조회 쿼리 분리
  - **AI 교차 검증을 통해 다건 주문 시 발생할 수 있는 데드락(Deadlock) 위험을 식별하고, DB 락 획득 순서를 강제하는 로직 추가**

👉 단순 AI 사용이 아닌 **설계 → 생성 → 검증 → 개선의 반복 구조**로 품질 확보

---

## ⚙️ 동시성 제어 해결 전략

### 문제 1
- 수백 명의 사용자가 동시에 동일 상품 주문 시
- 재고가 음수가 되는 **Race Condition 발생**

---

### 해결 방법: Pessimistic Lock (비관적 락)

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

- DB 레벨에서 row lock 획득 (SELECT ... FOR UPDATE)

- 하나의 트랜잭션이 작업 완료 전까지 다른 트랜잭션 접근 차단

---

### 문제 2: 다건 상품 주문 시 Deadlock (교착 상태)
- 여러 상품을 한 번에 주문할 때, 트랜잭션마다 락을 획득하는 순서가 다르면 서로 락 해제를 기다리는 **데드락 발생 위험**

---

#### 해결 방법: 락 획득 순서 강제 (Sorting)
- 애플리케이션 계층에서 파라미터로 넘어온 상품 ID들을 오름차순으로 정렬
- DB 조회 시 `ORDER BY p.id ASC`를 강제하여 모든 트랜잭션이 일관된 순서로 락을 획득하도록 제어하여 순환 대기 차단
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

## 🚀 성능 최적화

### 1. 인덱스 적용
상품 조회 성능 개선을 위해 정렬 필드에 인덱스 적용

| 필드 | 목적 |
|---|---|
| `price` | 가격 오름차순 조회 |
| `likesCount` | 좋아요 내림차순 조회 |
| `createdAt` | 최신순 조회 |

```java
@Table(indexes = {
    @Index(name = "idx_price", columnList = "price"),
    @Index(name = "idx_likes_count", columnList = "likesCount"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
```
2. OSIV 비활성화
   
```YAML
spring:
  jpa:
    open-in-view: false
```

커넥션 점유 시간 최소화

트랜잭션 범위 명확화

실무 환경에 적합한 설정

📡 API 명세

## 1. 회원 (Users)
- **회원가입** (`POST /api/v1/users`)
- **내 정보 조회** (`GET /api/v1/users/me`) : 헤더 인증을 통한 본인 정보 조회
- **비밀번호 변경** (`PATCH /api/v1/users/me/password`) : 기존 비밀번호 확인 후 변경
  
- 회원가입 (POST /api/v1/users)

- 내 정보 조회 (GET /api/v1/users/me) : 헤더 인증을 통한 본인 정보 조회

- 비밀번호 변경 (PATCH /api/v1/users/me/password) : 기존 비밀번호 확인 후 변경

```JSON
{
  "loginId": "user1",
  "loginPw": "1234",
  "name": "홍길동",
  "role": "USER"
}
```

## 2. 상품 (Products)
   
- 상품 등록 (POST /api/v1/products) - ADMIN 전용

- 상품 목록 조회 (GET /api/v1/products) - 인증 없음

Query Params: sort (latest | price_asc | likes_desc), page (default 0), size (default 20)

## 3. 좋아요 (Likes)
   
- 좋아요 등록 (POST /api/v1/products/{productId}/likes)

- 좋아요 취소 (DELETE /api/v1/products/{productId}/likes)

- 좋아요 목록 조회 (GET /api/v1/likes)

Unique Constraint 및 사전 검증으로 중복 차단, N+1 방지 로직 적용

## 4. 주문 (Orders)
- **다건 주문 요청** (`POST /api/v1/orders`)
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```
- 비관적 락 & 데드락 방지 정렬 기반 재고 차감

- 동시성 100% 보장

## 🧠 핵심 설계 요약

- 동시성: PESSIMISTIC_WRITE로 DB 레벨 완전 제어
- 성능: 인덱스 + OSIV OFF
- 안정성: 트랜잭션 기반 처리 및 멀티스레드 테스트 검증 완료
- 아키텍처: Controller → Service → Repository
- 인증: 인증 로직 중앙화

### ✨ 추가 고도화 (Advanced Features)

- **글로벌 예외 처리 (Global Exception Handling)**
  - `@RestControllerAdvice`를 활용하여 예외 처리 중앙화
  - 클라이언트에게는 통일된 에러 응답(ErrorResponse) 규격을 제공하고, 서버 내부 에러(500)는 로깅하여 보안 및 유지보수성 향상
- **데이터 무결성 검증 (Validation)**
  - `@Valid` 및 Validation 어노테이션(`@Min`, `@NotBlank` 등)을 적용
  - Controller 계층에서 잘못된 요청(예: 음수 수량, 빈 문자열)을 비즈니스 로직 도달 전에 사전 차단하여 안전성 확보

---

## 📌 결론

이 프로젝트는 단순 CRUD 구현을 넘어, **동시성 문제 해결, DB 레벨 성능 최적화, 고도화된 예외 처리 및 방어 로직, 그리고 AI 기반 개발 프로세스 활용**까지 포함한 실무 지향 백엔드 설계 및 구현 경험을 목표로 구축되었습니다.
