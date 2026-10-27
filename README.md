# OTT 구독 관리 시스템

## 1. 프로젝트 개요

### 주제 선정 배경 및 기획 의도

여러 OTT 서비스를 이용하면서 서비스별 플랜과 만료일을 개별적으로 관리해야 하는 불편함을 줄이기 위해, 회원별 구독 정보를 한곳에서 관리하는 콘솔 프로그램을 기획했습니다. Java의 객체 지향 구조와 JDBC를 활용하여 입력, 데이터 저장, 조회, 수정, 삭제의 전체 흐름을 구현하는 것을 목표로 합니다. 추가적으로 깃을 통한 협업을 익히는데 목적을 둡니다.

- 프로젝트명: OTT 구독 관리 시스템
- 개발 기간: 
- 팀명: BearT
- 주요 내용: 회원 관리, 개인별 OTT 구독 관리, OTT별 플랜 이력 조회
- 범위: 구독 정보 관리이며 실제 OTT 연동·결제 기능은 포함하지 않음

### 구조

```text
solution/
├─ Answer.java                    실행 진입점
├─ run/Application.java           콘솔 메뉴 및 사용자 입력
├─ controller/MemberController.java 입력 검증, 로그인 상태, 기능 연결
├─ model/                         Member, Membership 데이터 모델
├─ repository/                    회원·구독 JDBC 처리 및 DB 연결
├─ mapper/                        회원·구독 SQL XML
└─ view/MemberView.java            조회 결과 및 메시지 출력
```

사용자 입력 → Application → MemberController → Repository → MySQL 순서로 처리하며, 결과는 MemberView를 통해 출력합니다. SQL은 XML Properties 파일로 분리하고 PreparedStatement로 실행합니다.

| 테이블 | 주요 항목 | 관계 및 제약 |
| --- | --- | --- |
| member_tbl | 회원 ID, 이름, 나이, 휴대폰 번호 | 휴대폰 번호 UNIQUE |
| membership_tbl | 구독 ID, 회원 ID, OTT, 플랜, 가입일, 만료일, 활성 여부 | 회원을 외래 키로 참조, 회원과 1:N 관계 |

### 개발환경

| 구분 | 기술 |
| --- | --- |
| 언어 | Java 17 |
| 실행 방식 | 콘솔 애플리케이션 |
| 빌드 | Gradle Wrapper 9.3.0 |
| 데이터베이스 | MySQL — 서버 버전 확인 필요 |
| DB 접근 | JDBC, MySQL Connector/J 9.7.0 |
| 보조 라이브러리 | Lombok 1.18.46 |
| 협업 도구 | Git, GitHub |
| IDE | [작성 필요] |


## 2. 팀 R&R

실제 담당 업무에 맞게 작성합니다.

| 팀원 | 역할 | 담당 업무 |
| --- | --- | --- |
| [이름] | [팀장/팀원] | [기획 및 담당 기능] |
| [이름] | [팀원] | [DB 및 담당 기능] |
| [이름] | [팀원] | [화면 및 담당 기능] |

## 3. 수행 절차 및 방법

### 요구사항 명세 및 개발 우선순위


| ID | 요구사항 | 처리 기준 | 제안 우선순위 | 코드 기준 현황 |
| --- | --- | --- | --- | --- |
| REQ-01 | 회원가입 | 이름·나이·휴대폰 번호 저장, 빈 값·나이·중복 번호 검사 | P1 | 구현 |
| REQ-02 | 로그인·로그아웃 | 휴대폰 번호 조회, 로그인 상태 설정·해제 | P1 | 구현 |
| REQ-03 | 구독 추가 | 본인에게 OTT·플랜·구독 일수를 연결하여 저장 | P1 | 구현 |
| REQ-04 | 현재 구독 조회 | 본인의 활성·미만료 구독 조회 | P1 | 구현 |
| REQ-05 | 전체 구독 기록 조회 | 연장·삭제 전에 ID와 상태 출력 | P2 | 구현 |
| REQ-06 | 구독 연장 | 본인 구독만 연장, 기존 만료일과 현재 시각 중 늦은 시각 기준 | P2 | 구현 |
| REQ-07 | 구독 삭제 | 본인 구독만 삭제, 확인 및 취소 제공 | P2 | 구현 |
| REQ-08 | OTT별 플랜 이력 | OTT·플랜 중복 제거 후 그룹별 출력 | P3 | 구현 |
| REQ-09 | 회원 탈퇴 | 본인 회원 삭제 후 로그인 상태 해제 | P2 | 연관 구독 처리 검증 필요 |
| REQ-10 | 입력 검증 | 숫자 재입력, 잘못된 메뉴 안내, 양수 검사 | P1 | 구현 |

P1은 기본 사용 흐름, P2는 관리 기능 확장, P3는 조회 편의 기능입니다.

### 구현 방법

1. 회원·구독 테이블 및 관계 설계
2. 회원·구독 SQL을 XML로 분리
3. Repository에서 JDBC 데이터 처리 구현
4. Controller에서 입력 검증 및 로그인 회원 ID 전달
5. 콘솔 메뉴와 결과 출력 연결
6. 기능별 정상·오류 시나리오 점검 — 실제 점검 결과 작성 필요

구독 연장·삭제에는 구독 ID와 회원 ID를 함께 조건으로 사용합니다. Repository는 try-with-resources로 자원을 정리하고 SQL 예외를 원인이 포함된 예외로 변환합니다.

기존 README에는 커밋 형식, 이슈 번호 기반 브랜치, Issue → 개발 → PR → 리뷰 및 병합 규칙이 정의되어 있습니다. 실제 적용 사례와 개발 일정은 [작성 필요]입니다.

## 4. 수행 결과

### 기능별 구현 결과

| 기능 | 코드에 구현된 동작 | 실행 검증 |
| --- | --- | --- |
| 회원가입 | 입력 검증 및 중복 번호 검사 후 회원 저장 | 확인 필요 |
| 로그인·로그아웃 | 휴대폰 번호 기반 조회 및 로그인 상태 관리 | 확인 필요 |
| 구독 추가 | 입력한 일수에 따라 DB에서 만료일 계산 | 확인 필요 |
| 현재 구독 조회 | 활성·미만료 구독을 만료일 순으로 출력 | 확인 필요 |
| 구독 연장 | 만료 구독은 현재 시각부터 연장, 활성 상태로 변경 | 확인 필요 |
| 구독 삭제 | 전체 기록 출력 후 ID 선택, y 확인 및 0 취소 | 확인 필요 |
| 플랜 이력 | OTT별 중복 없는 플랜 이름 출력 | 확인 필요 |
| 회원 탈퇴 | 회원 삭제 후 로그인 상태 해제 | DB 제약 확인 필요 |

플랜 이력은 DB에 남아 있는 구독 기록을 기준으로 하며, 삭제한 구독의 이력을 별도로 보존하지 않습니다.

### 화면 캡처

아래 위치에 실제 실행 이미지를 삽입합니다.

- 시작 메뉴 및 회원가입:
<img width="606" height="398" alt="image" src="https://github.com/user-attachments/assets/a4f97d55-e2d4-437d-a123-45cea66c1708" />

- 로그인 및 현재 구독 목록:
<img width="606" height="326" alt="image" src="https://github.com/user-attachments/assets/fad2b8d1-c07f-4d23-8a69-10fdf297e799" />

- 구독 추가: 
<img width="606" height="398" alt="image" src="https://github.com/user-attachments/assets/ea3b031e-7e3a-43c7-a21c-9b2e2407cf86" />

- 구독 연장 전후 만료일:
<img width="1328" height="176" alt="image" src="https://github.com/user-attachments/assets/dea91f08-ac40-4398-a792-b287287a48e8" />

- 구독 삭제 확인 및 결과:
<img width="1316" height="190" alt="image" src="https://github.com/user-attachments/assets/903f491f-7922-4cba-acdc-e4924d40081a" />

- 회원 탈퇴: [DB 제약 확인 후 캡처 필요]
<img width="678" height="405" alt="image" src="https://github.com/user-attachments/assets/2ceafdf8-45bc-420f-8bb7-062e6f2f6e7b" />

### 실행 방법

1. Java 21과 MySQL을 준비합니다.
2. 새 실습용 DB에서 src/main/resources/db/schema.sql을 실행합니다. 기존 회원·구독 테이블을 삭제하고 재생성하는 스크립트이므로 기존 데이터 보존 여부를 확인합니다.
3. 예제 데이터가 필요하면 같은 폴더의 insert.sql을 실행합니다.
4. src/main/resources/db.properties를 생성하고 실제 접속 정보를 입력합니다.

```properties
 driver=com.mysql.cj.jdbc.Driver
 url=jdbc:mysql://localhost:3306/smsdb
 user=YOUR_DB_USER
 password=YOUR_DB_PASSWORD
```

5. 프로젝트 루트에서 실행합니다.


## 5. 회고

### 기획 의도와의 부합 정도
- [작성 필요]

### 달성도 및 완성도
- 실제 목표 대비 달성도: [작성 필요]
- 검증을 완료한 기능: [작성 필요]
- 미완료 기능과 개선점: [작성 필요]

### 팀원별 평가 및 느낀 점

| 팀원 | 배운 점 및 잘된 점 | 어려움과 해결 과정 | 아쉬운 점 |
| --- | --- | --- | --- |
| [이름] | [작성 필요] | [작성 필요] | [작성 필요] |
| [이름] | [작성 필요] | [작성 필요] | [작성 필요] |
| [이름] | [작성 필요] | [작성 필요] | [작성 필요] |

---
