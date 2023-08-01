# Inqueue
- 트래픽이 갑자기 높아졌을 때, 순차적으로 트래픽을 처리하기 위한  `대기열`을 제공합니다.
- 다른 시스템에 연동하여 사용 할 수 있는 플랫폼 서비스입니다.

## 서버 구조
![architecture](https://github.com/f-lab-edu/inqueue/assets/33423123/8a0284ca-9177-4a8f-9c68-9703e19710ce)

## 프로젝트 주요 관심사
- 객체 지향 원리를 토대로 읽기 쉬운 코드를 작성
- 적절한 테스트를 통해 시스템 안정성 향상
- 대용량 트래픽을 고려햐여 성능 개선

## Issue 해결 과제
- [Spring Security를 이용한 다양햔(Hmac, Jwt) 인증 통합](https://mark-tech-talk.tistory.com/3)

- Redis SortedSet내 Element(Value)에 TTL 적용 효과 구현
- Redis Pipeline을 통한 다중 Insert 성능 개선
- TestContainer를 사용하여 실 환경과 유사한 테스트 환경 구축

## Use Case
https://github.com/f-lab-edu/inqueue/wiki/UseCase

## 대기열 동작 방식
https://github.com/f-lab-edu/inqueue/wiki/Process
