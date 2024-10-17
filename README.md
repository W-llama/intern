# Java Internship Project

## 1. JWT를 이용한 로그인/회원가입 기능

### 회원가입 기능
- **아이디**: 최소 4자 이상, 10자 이하 (알파벳 소문자 a-z, 숫자 0-9)
- **비밀번호**: 최소 8자 이상, 15자 이하 (알파벳 대소문자 a-z, A-Z, 숫자 0-9, 특수문자)

**요청 메시지 (Request Message)**
```json
{
    "username": "TestUser",
    "password": "TestUser1!",
    "nickname": "TestUser"
}
```

**응답 메시지 (Response Message)**
```json
{
    "username": "TestUser",
    "nickname": "TestUser",
    "authorities": [
        {
            "authorityName": "ROLE_USER"
        }
    ]
}
```

### 로그인 기능
**요청 메시지 (Request Message)**
```json
{
    "username": "TestUser",
    "password": "TestUser1!"
}
```

**응답 메시지 (Response Message)**
```json
{
    "token": "your_generated_jwt_token"
}
```
## 2. Swagger UI
- Swagger UI 접속 기능을 통해 API를 손쉽게 테스트하고 문서화된 정보를 제공합니다.

## 3. Test Code
- Swagger UI 접속 기능을 통해 API를 손쉽게 테스트하고 문서화된 정보를 제공합니다.
회원가입 및 로그인 기능에 대한 테스트 코드 구현 완료.
JUnit5와 Spring Security Test를 사용하여 기능 테스트 수행.

## 4. 환경변수 설정
```
DATABASE_NAME=your_database_name
DATABASE_USERNAME=your_database_username
DATABASE_PASSWORD=your_database_password
DATABASE_URL=your_database_url
JWT_SECRET_KEY=your_jwt_secret_key
```
- DATABASE_NAME: 사용할 데이터베이스 이름
- DATABASE_USERNAME: 데이터베이스 접속 ID
- DATABASE_PASSWORD: 데이터베이스 접속 비밀번호
- DATABASE_URL: 데이터베이스 접속 URL
- JWT_SECRET_KEY: JWT 토큰 발급에 사용할 시크릿 키

## 5. Swagger UI
![스크린샷 2024-10-09 122912](https://github.com/user-attachments/assets/90c33045-48fa-488d-88d9-1e293a75517d)



   
