# Inqueue
- 트래픽이 갑자기 높아졌을 때, 순차적으로 트래픽을 처리하기 위한  `대기열`을 제공합니다.
- 다른 시스템에 연동하여 사용 할 수 있는 플랫폼 서비스입니다.

## 서버 구조
<img width="600" alt="architecture" src="https://github.com/f-lab-edu/inqueue/assets/33423123/8a0284ca-9177-4a8f-9c68-9703e19710ce">

## 프로젝트 주요 관심사
- 객체 지향 원리를 토대로 읽기 쉬운 코드를 작성
- 적절한 테스트를 통해 시스템 안정성 향상
- 대용량 트래픽을 고려햐여 성능 개선

## Issue 해결 과제

- [Spring Security를 이용한 다양햔(Hmac, Jwt) 인증 통합](https://mark-tech-talk.tistory.com/3)
- Redis SortedSet내 Element(Value)에 TTL(Time to Live) 적용 효과 구현
- Redis Pipeline을 통한 다중 Insert 성능 개선
- TestContainer를 사용하여 실 환경과 유사한 테스트 환경 구축

## Use Case

https://github.com/f-lab-edu/inqueue/wiki/UseCase

## 대기열 동작 방식

### 1. 대기열 진입을 위한 토큰 발급 & 인증

![issue_jwt_token_for_entering_queue](https://github.com/f-lab-edu/inqueue/assets/33423123/e71acbb7-fc6a-4736-81e1-e99428f2a0dd)

- Inqueue 서버 접근을 위한 식별자(ClientId, ClientSecret)을 가진 `고객사 서버`를 통해서 JWT 토큰을 발급
- JWT토큰의 Claim에는 고객사를 식별하기 위한 ClientId, 해당 유저를 식별하기 위해 Inqueue서버에서 UUID로 만든 UserID를 포함

### 2. 대기열 & 작업열

- 대기열: 작업을 기다리며 대기중인 고객사 유저를 위한 열
- 작업열: 대기열을 빠져 나와 작업이 가능한 고객사 유저를 위한 열

![wait_queue_process](https://github.com/f-lab-edu/inqueue/assets/33423123/7ccea911-4490-448e-a1b7-1cc626d0f751)

### 3. 대기열 -> 작압열 이동 스케쥴러

![scheduler_process](https://github.com/f-lab-edu/inqueue/assets/33423123/61734e01-05fd-480e-a6cb-f50c3e723c34)
<br/>
![스케쥴러 동작](https://user-images.githubusercontent.com/33423123/233954695-c93a934d-af62-490e-ae5b-e3028b24e61c.png)