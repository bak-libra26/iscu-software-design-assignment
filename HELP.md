# 📘 User Guide

## ❓ 자주 묻는 질문 (FAQ)

### Q. 데이터베이스 연결 오류가 발생합니다.
A. `src/main/resources/application.yml` 파일의 데이터베이스 설정(URL, Username, Password)이 정확한지 확인하세요. MySQL 서버가 실행 중인지도 확인해야 합니다.

### Q. 빌드가 실패합니다.
A. Java 11 이상이 설치되어 있는지 확인하세요. `java -version` 명령어로 확인할 수 있습니다.

### Q. 한글이 깨져서 나옵니다.
A. 데이터베이스 생성 시 `CHARACTER SET utf8mb4` 옵션을 사용했는지 확인하세요.

## 🛠 문제 해결

### 포트 충돌
이미 8080 포트를 사용 중인 경우, `application.yml`에서 포트를 변경할 수 있습니다:

```yaml
server:
  port: 8081
```

### 로그 레벨 변경
디버깅을 위해 로그 레벨을 변경하려면 `application.yml`을 수정하세요:

```yaml
logging:
  level:
    kr.co.iscu.assignment: DEBUG
```

## 🌐 배포 가이드

### JAR 파일 생성
```bash
./mvnw clean package
```

### 실행
```bash
java -jar target/inventory-management-1.0.0.jar
```

### 프로파일 사용
개발(dev)과 운영(prod) 환경을 분리하려면 프로파일을 사용할 수 있습니다.
`application-prod.yml` 파일을 생성하고 다음과 같이 실행하세요:

```bash
java -jar -Dspring.profiles.active=prod target/inventory-management-1.0.0.jar
```
