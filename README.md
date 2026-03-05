# BarKit 🧾  
> 내 모든 멤버십을 한번에 담아두고,
> 빠르게 꺼내 쓸 수 있는 지도형 통합 멤버십 지갑 서비스

<img width="1039" height="588" alt="스크린샷 2025-12-28 오후 9 43 11" src="https://github.com/user-attachments/assets/1509caec-defc-47f3-b148-246932831da6" />

---

## 🏗 시스템 아키텍처

```
                        ┌─────────────────────────────────┐
                        │        Client (React.js)        │
                        └─────────────────┬───────────────┘
                                          │ HTTPS
                        ┌─────────────────▼───────────────┐
                        │         AWS EC2 (Spring Boot)   │
                        │  ┌──────────────────────────┐   │
                        │  │  Controller → Service    │   │
                        │  │  → Repository (QueryDSL) │   │
                        │  └──────────────────────────┘   │
                        └──────┬──────────────┬───────────┘
                               │              │
               ┌───────────────▼──┐     ┌─────▼──────────────┐
               │   AWS RDS (MySQL)│     │  Redis (Docker)    │
               │   (메인 DB)       │     │  (캐싱)             │
               └──────────────────┘     └────────────────────┘
                                                │
                        ┌───────────────────────▼────┐
                        │           AWS S3           │
                        └────────────────────────────┘
```

---

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| **Language** | Java 17 |
| **Framework** | Spring Boot |
| **ORM** | Spring Data JPA + QueryDSL |
| **Database** | MySQL (AWS RDS) |
| **Cache** | Redis (Docker) |
| **Auth** | JWT |
| **Storage** | AWS S3 |
| **Infra** | AWS EC2 |
| **API Docs** | Swagger |
| **Build** | Gradle |

---

## 📁 프로젝트 구조

```
# 도메인 기반 패키지 구조

BarKit
├── src
│   ├── main
│   │   ├── java/com/umc/barkit
│   │   │   ├── BarkitApplication.java
│   │   │   │
│   │   │   ├── domain  
│   │   │   │   ├── favorite 
│   │   │   │   ├── home  
│   │   │   │   ├── membership 
│   │   │   │   ├── profile   
│   │   │   │   ├── store 
│   │   │   │   ├── user                    
│   │   │   │   └── uuid                    
│   │   │   │
│   │   │   └── global                      
│   │   │       ├── annotation       
│   │   │       ├── apiPayload         
│   │   │       ├── auth                
│   │   │       ├── aws/s3              
│   │   │       ├── config  
│   │   │       ├── entity             
│   │   │       └── validator     
│   │   │
│   │   └── resources
│   │       ├── application.yaml
│   │       ├── application-local.yaml
│   │       └── application-prod.yaml
│   │
│   └── generated      
│
└── test
    └── BarkitApplicationTests.java

```

---
