# 🚀 TEAM.MATE (Backend Repository)

> 당신의 팀에, 보이지 않는 한 명을 더.
> 대학생 팀 프로젝트를 효율적으로 관리하고 기록해주는 (AI) 프로젝트 매니저 서비스의 백엔드 서버입니다.

---

## 💻 기술 스택 (Tech Stack)

* **Language:** `Java 17`
* **Framework:** `Spring Boot 3.x`
* **Build Tool:** `Gradle`
* **Database:** `MySQL 8.0` (Docker)
* **Data Access:** `Spring Data JPA`
* **Security:** `Spring Security` (로그인/권한)
* **Template Engine:** `Thymeleaf` (서버 사이드 렌더링)

---

## 🚀 로컬 환경에서 실행하기

이 프로젝트를 님의 컴퓨터에서 실행하려면, **Docker Desktop**과 **Java 17**이 설치되어 있어야 합니다.

1.  **DB (MySQL) 띄우기**
    프로젝트의 `docker-compose.dev.yml` 파일을 이용해 개발용 DB를 실행합니다.
    ```bash
    docker compose -f docker-compose.dev.yml up -d
    ```

2.  **Spring Boot 서버 실행하기**
    IntelliJ 터미널에서 Gradle Wrapper를 이용해 서버를 실행합니다.
    ```bash
    ./gradlew bootRun
    ```

3.  **서버 접속**
    서버가 정상적으로 실행되면 `http://localhost:8080`으로 접속할 수 있습니다.

---

