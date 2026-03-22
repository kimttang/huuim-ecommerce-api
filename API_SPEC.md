# 📡 E-Commerce API 명세서

모든 API는 임시 인증을 위해 아래 헤더를 공통으로 사용합니다. (회원가입, 상품 목록 조회 제외)
- `X-Huuim-LoginId`: 유저 아이디
- `X-Huuim-LoginPw`: 유저 비밀번호

---

## 1. 회원 (Users)

### 1.1 회원가입
- **Method & URL**: `POST /api/v1/users`
- **Request Body**:
```json
{
  "loginId": "user1",
  "loginPw": "1234",
  "name": "홍길동",
  "role": "USER" // 또는 "ADMIN"
}
```
Response (200 OK): 생성된 유저 ID 반환

JSON
```
1
```
1.2 내 정보 조회
Method & URL: GET /api/v1/users/me

Headers: X-Huuim-LoginId, X-Huuim-LoginPw

Response (200 OK):

JSON
```
{
  "id": 1,
  "loginId": "user1",
  "name": "홍길동",
  "role": "USER"
}
```
1.3 비밀번호 변경
Method & URL: PATCH /api/v1/users/me/password

Headers: X-Huuim-LoginId, X-Huuim-LoginPw (기존 비밀번호)

Request Body:

JSON
```
{
  "newPassword": "newPassword5678"
}
```
Response (200 OK): 본문 없음

2. 상품 (Products)
2.1 상품 등록 (관리자 전용)
Method & URL: POST /api/v1/products

Headers: X-Huuim-LoginId, X-Huuim-LoginPw (ADMIN 권한 계정)

Request Body:

```
JSON
{
  "name": "한정판 키보드",
  "price": 150000,
  "stock": 100
}
```
Response (200 OK): 생성된 상품 ID 반환

JSON
```
1
```
2.2 상품 목록 조회 (페이징 및 정렬)
Method & URL: GET /api/v1/products

Query Parameters:

sort (선택): latest (최신순), price_asc (가격낮은순), likes_desc (인기순)

page (선택): 페이지 번호 (기본값 0)

size (선택): 페이지 크기 (기본값 20)

Response (200 OK):

JSON
```
{
  "content": [
    {
      "id": 1,
      "name": "한정판 키보드",
      "price": 150000,
      "stock": 100,
      "likesCount": 5
    }
  ],
  "pageable": { ... },
  "totalElements": 1
}
```
3. 좋아요 (Likes)
3.1 상품 좋아요 등록
Method & URL: POST /api/v1/products/{productId}/likes

Headers: X-Huuim-LoginId, X-Huuim-LoginPw

Response (200 OK): 본문 없음 (성공 시 상품의 likesCount 1 증가)

4. 주문 (Orders)
4.1 다건 상품 주문 (데드락 방지 및 재고 차감)
Method & URL: POST /api/v1/orders

Headers: X-Huuim-LoginId, X-Huuim-LoginPw

Request Body:
```
JSON
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
Response (200 OK): 생성된 주문(Order) ID 반환

JSON
```
15
```
Response (400 Bad Request - 예외 발생 시):
```
JSON
{
  "status": 400,
  "message": "존재하지 않는 상품이 포함되어 있습니다: 2"
}
```