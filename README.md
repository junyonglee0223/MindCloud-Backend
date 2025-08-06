
# Mind Cloud

태그 기반 북마크 저장·관리 프로그램

---

## 🚀 프로젝트 개요

**Mind Cloud**는 현대인의 정보 저장 및 관리 과정에서 발생하는 비효율을 해결하기 위해 개발된 태그 기반 북마크 솔루션입니다.  
Chrome Extension을 통해 웹 환경에서 클릭 몇 번만으로 북마크를 저장하고, Spring Boot + React 기반의 웹 UI에서 손쉽게 조회·관리할 수 있습니다.  
또한, GPT API 연계를 통해 자동 태그 추천 및 추천 사이트 기능을 제공하여 사용자의 정보 분류와 검색 효율을 극대화합니다.

---

## 🎯 주요 기능

- **Chrome Extension**  
  - 웹페이지 전체 및 특정 텍스트·이미지 선택 후 저장  
  - 구글 계정 OAuth2 로그인 지원  
- **태그 기반 관리**  
  - GPT API로 자동 생성되는 태그(6가지 카테고리)  
  - 사용자가 직접 태그 추가·삭제 가능  
  - 태그별 필터링·정렬 및 썸네일 미리보기  
- **백엔드 API**  
  - Spring Boot + JPA 기반 CRUD(북마크·텍스트·이미지·태그)  
  - MySQL(RDS) 연동  
- **프론트엔드**  
  - React로 사용자 친화적 UI 구현  
  - 태그 클릭 시 해당 북마크 목록 실시간 렌더링  
- **인프라**  
  - AWS EC2(서버) / RDS(MySQL) / S3(썸네일) 배포  

---

## 🧩 기술 스택

| 구분       | 기술                  |
| ---------- | --------------------- |
| 언어       | Java 17, JavaScript   |
| 백엔드     | Spring Boot, JPA, MySQL |
| 프론트엔드 | React                 |
| 확장 기능  | OpenAI GPT API        |
| 배포       | AWS EC2, RDS, S3, Docker |

---

## 🔧 설치 및 실행

1. 리포지토리 클론  
   ```bash
   git clone https://github.com/junyonglee0223/MindCloud-Backend.git
   git clone https://github.com/junyonglee0223/MindCloud-Frontend.git

2. Chrome Extension 설치

   * `extension/` 디렉터리를 Chrome 확장 프로그램 로드(개발자 모드)
   * OAuth 클라이언트 ID 설정 후 빌드

---

## 📂 프로젝트 구조

```
MindCloud-Backend/
├─ src/main/java/...
├─ src/main/resources/
│  └─ application.properties
└─ Dockerfile

MindCloud-Frontend/
├─ public/
├─ src/
│  ├─ components/
│  ├─ pages/
│  └─ api/
└─ tailwind.config.js

extension/
├─ manifest.json
├─ popup.html
├─ popup.js
└─ background.js
```

---



## 📈 기대 효과

* 태그 기반 분류로 **빠른 정보 검색**
* GPT 연계 자동 태그 추천으로 **관리 편의성 증대**

---

## 👥 팀원

* **조규준** (12161655)
* **신나윤** (12201071)
* **이준용** (12191694)


