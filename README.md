# 재고관리 시스템 (Inventory Management System)

Spring Boot 기반의 재고관리 시스템입니다. 상품 관리, 재고 입출고, 재고 이력 관리, 재고 통계 기능을 제공합니다.

## 📋 목차

- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시작하기](#시작하기)
- [API 문서](#api-문서)
- [프로젝트 구조](#프로젝트-구조)
- [테스트](#테스트)
- [문서](#문서)

## 🚀 주요 기능

### 1. 상품 관리
- 상품 등록, 조회, 수정, 삭제
- 상품 기본 정보 관리 (상품명, 카테고리, 단가, 안전재고)
- 재고가 있는 상품 삭제 방지

### 2. 재고 관리
- 재고 입고/출고 처리
- 현재 재고 수량 관리
- 재고 부족 알림 (안전재고 미만 체크)
- 재고 현황 조회

### 3. 재고 이력 관리
- 입고/출고 이력 기록 및 조회
- 기간별 거래 이력 통계
- 최신순 이력 정렬

### 4. 재고 통계
- 기간별 입고/출고 통계
- 재고 회전율 계산
- 안전재고 미만 상품 목록 조회

## 🛠 기술 스택

- **Java**: 11
- **Spring Boot**: 2.7.18
- **Database**: MySQL
- **ORM**: MyBatis 2.2.2
- **Build Tool**: Maven
- **Test Framework**: JUnit 5, AssertJ

## 📦 시작하기

### 사전 요구사항

- Java 11 이상
- Maven 3.6 이상
- MySQL 8.0 이상

### 설치 및 실행

1. **저장소 클론**
```bash
git clone https://github.com/your-username/inventory-management.git
cd inventory-management
```

2. **데이터베이스 설정**

MySQL 데이터베이스를 생성하고 `application.yml.example` 파일을 `application.yml`로 복사한 후 수정하세요:

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

`src/main/resources/application.yml` 파일을 열어 데이터베이스 정보를 입력합니다:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/inventory_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

3. **데이터베이스 생성**
```sql
CREATE DATABASE inventory_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

4. **애플리케이션 실행**

Maven Wrapper를 사용하여 실행:
```bash
./mvnw spring-boot:run
```

또는 JAR 파일로 빌드 후 실행:
```bash
./mvnw clean package
java -jar target/inventory-management-1.0.0.jar
```

5. **애플리케이션 확인**

애플리케이션이 실행되면 기본적으로 `http://localhost:8080`에서 접근할 수 있습니다.

## 📚 API 문서

### 상품 관리 API (`/api/products`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | 상품 등록 |
| GET | `/api/products` | 전체 상품 목록 조회 |
| GET | `/api/products/{id}` | 상품 상세 조회 |
| PUT | `/api/products/{id}` | 상품 정보 수정 |
| DELETE | `/api/products/{id}` | 상품 삭제 |

### 재고 관리 API (`/api/stocks`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/stocks/{productId}/inbound` | 상품 입고 |
| POST | `/api/stocks/{productId}/outbound` | 상품 출고 |
| GET | `/api/stocks/{productId}/histories` | 재고 이력 조회 |
| GET | `/api/stocks/status` | 전체 재고 현황 조회 |
| GET | `/api/stocks/status/below-safety` | 안전재고 미만 상품 목록 |
| GET | `/api/stocks/{productId}/statistics` | 기간별 재고 통계 조회 |

### API 사용 예시

#### 상품 등록
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "노트북",
    "category": "전자제품",
    "unitPrice": 1500000.00,
    "safetyStock": 10
  }'
```

#### 재고 입고
```bash
curl -X POST http://localhost:8080/api/stocks/1/inbound \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 50
  }'
```

#### 재고 출고
```bash
curl -X POST http://localhost:8080/api/stocks/1/outbound \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 20
  }'
```

**Postman 컬렉션**: 프로젝트 루트의 `postman/Inventory-Management.postman_collection.json` 파일을 Postman에 import하여 사용할 수 있습니다.

## 📁 프로젝트 구조

```
inventory-management/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── kr/co/iscu/assignment/
│   │   │       ├── config/          # 설정 클래스
│   │   │       ├── controller/      # REST API 컨트롤러
│   │   │       ├── domain/          # 도메인 모델
│   │   │       ├── repository/      # 데이터 접근 계층
│   │   │       └── service/         # 비즈니스 로직 계층
│   │   └── resources/
│   │       ├── application.yml      # 애플리케이션 설정
│   │       ├── schema.sql           # 데이터베이스 스키마
│   │       └── mappers/             # MyBatis 매퍼 XML
│   └── test/
│       └── java/
│           └── kr/co/iscu/assignment/
│               ├── repository/      # 리포지토리 테스트
│               └── service/          # 서비스 테스트
├── postman/                         # Postman 컬렉션
├── pom.xml                          # Maven 설정
└── README.md                        # 프로젝트 문서
```

## 🧪 테스트

### 테스트 실행

```bash
./mvnw test
```

### 테스트 결과

```
[INFO] Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**테스트 커버리지:**
- ProductRepositoryTest: 4개 테스트
- StockRepositoryTest: 3개 테스트
- StockHistoryRepositoryTest: 3개 테스트
- ProductServiceTest: 5개 테스트
- StockServiceTest: 10개 테스트
- InventoryManagementApplicationTests: 1개 테스트

## 📖 문서

- [과제요구사항.md](./과제요구사항.md) - 프로젝트 요구사항 및 설계 문서
- [HELP.md](./HELP.md) - Spring Boot 참고 문서

## 📝 라이선스

이 프로젝트는 교육 목적으로 작성되었습니다.

## 👤 작성자

- **Your Name** - [Your GitHub](https://github.com/your-username)


---

**버전**: 1.0.0  
**최종 업데이트**: 2024년

