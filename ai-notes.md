## 🤖 사용한 AI/도구

- **ChatGPT**

## 📝 주요 프롬프트 또는 활용 방식

### 1. Swagger 문서 작성

#### 목표

- Swagger 문서 작성 과정의 반복을 줄이고, 효율적으로 API 문서를 작성할 수 있는 패턴 확보

#### 방법(프롬프트)

- 하나의 API 코드(Controller -> Service)를 보여주고 AI에게 Swagger 문서를 작성하도록 지시
- 프롬프트에서는 Swagger 문서에 `@Operation(summary,description)`과 `@ApiResponses`를 포함하도록 요구했으며, 정상 응답뿐만 아니라 **예외 상황에 대한 응답도 함께 기재하도록 명시**
- 작성된 문서를 기반으로 AI가 생성한 Swagger 어노테이션을 다듬고, 동일한 방식으로 나머지 API에도 적용할 수 있도록 활용


### 2. 로그인 구조

#### 목표

- 토이 프로젝트 수준의 로그인 기능 구현

#### 방법(프롬프트)

- Spring Security를 사용하지 않고, 토이 프로젝트 수준으로 **세션 방식**을 활용한 로그인 구조 설계
- `HttpSession`을 사용하여 로그인 성공 시 사용자 정보를 세션에 저장
- `/bookmark` 경로 API 요청 시 **세션 검증을 통해 인증 처리**

### 3. 페이지네이션 기능

#### 목표

- 북마크 조회 시 `tag`, `title`, `url` 기준으로 필터링 가능하도록 하고, **페이지 단위로 조회 가능하게 설계**

#### 방법(프롬프트)

- `tag`, `title`, `url` 조건을 클라이언트로부터 받아 조건이 만족하는 경우만 필터링
- `QueryDSL` 을 활용하여 **동적 쿼리 작성**
- 조회 결과는 `Page<Bookmark>` 형태로 반환하며, 총 페이지 수와 전체 요소 수 등 메타 정보를 포함

### 4. 예외 처리 방식

#### 목표

- 비지니스 로직 등 클라이언트의 잘못된 요청으로 발생할 수 있는 **예외를 공통으로 처리하고자 함**
- DTO 필드에 대한 유효성 검사(`@NotBlank`) 실패 시 일관된 방식으로 응답 제공

#### 방법(프롬프트)

- `@ControllerAdvice`와 `@ExceptionHandler`를 활용하여 예외 처리
- `MethodArgumentNotValidException`: `@Valid` 적용 시 DTO 검증 처리
- 예외 발생 시 **검증 실패 필드와 메시지를 포함**

## 🛠️ 생성 코드가 반영된 파일 또는 영역

이 항목은 위의 "주요 프롬프트 또는 활용 방식"에서 설명한 기능별 구현이 실제로 적용된 파일과 영역을 정리한 것입니다.

### 1. Swagger 문서 작성

- org.example.bookmark`/controller/docs/AuthControllerDocs.java`
- org.example.bookmark`/controller/docs/BookmarkControllerDocs.java`

### 2. 로그인 구조

- Controller: org.example.bookmark`/controller/AuthController.java` (`login` 메서드)
- Service: org.example.bookmark`/service/AuthService.java` (`login` 메서드)

### 3. 페이지네이션 기능

- Service: org.example.bookmark`/service/BookmarkService.java` (`getBookmarksPage()` 메서드)
- Configuration: org.example.bookmark`/common/config/QueryDSLConfig.java` (QueryDSL 설정)

### 4. 예외 처리 방식

- Exception Handler: org.example.bookmark`/common/exception/GlobalExceptionHandler.java`

## ✏️ AI 생성물에 대해 직접 수정·검증한 부분 요약

### 1. Swagger 문서 작성

- **공통 에러**(500 Internal Server Error, 401 Unauthorized 등)를 `OpenApiConfig`에 등록하여 **재사용** 가능하도록 조정
- 각 API별 예외 상황을 검토하고, AI가 생성한 동일한 예외 Response를 **실제 상황에 맞게 수정**

### 2. 로그인 구조

- `/bookmark` 경로 API 요청 시 **모든 컨트롤러에서 세션 검증을 반복하도록 구현**되어 비효율적이라고 판단
- `Interceptor`를 활용하여 공통 세션 **검증 로직을 중앙화**

### 3. 페이지네이션 기능

- 서비스 레이어에서 QueryDSL `Predicate` 생성과 실제 조회 로직이 함께 있어 코드 가독성이 떨어지는 문제
- 클라이언트 요청을 `@ModelAttribute`로 받아 `BookCriteria` 객체에 전달하고, 해당 객체 안에 `toPredicate()` 메서드로 분리 → **Predicate 생성과 조회 로직 분리, 가독성 향상**