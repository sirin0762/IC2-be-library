# Jpa Slow Repository Method Logger 라이브러리
*Jpa Slow Repository Method Logger* 라이브러리는 유저가 설정한 특정 시간 이상의 실행시간을 가지는 Jpa Repository Method를 감지하여 로그로 출력하는 라이브러리 입니다.
또한 어플리케이션 전역적으로 특정 Repository가 임계치 이상 호출되는 것에 대해서도 log 기능을 제공합니다.

### 기능
- 임계치 이상의 실행시간을 가진 쿼리에 대해서 해당 쿼리를 호출한 메서드 및 쿼리에 대한 로깅 진행
- 임계치 이상의 호출횟수를 가진 쿼리에 대해서 해당 메서드에 대한 로깅 진행

### 설치 방법

Maven
1. 저장소에 jitpack 등록
```maven
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. 의존성 등록
```maven
<dependency>
    <groupId>com.github.sirin0762</groupId>
    <artifactId>IC2-be-library</artifactId>
    <version>0.2.5</version>
</dependency>
```

Gradle
1. 저장소에 jitpack 등록
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

2. 의존성 등록
```gradle
implementation 'com.github.sirin0762:IC2-be-library:0.2.5'
```

### 사용방법
1. application.yml 내 해당 속성을 명시할 경우 기능 활성화
```yml
my:
  jpa:
    query:
      logger:
        enabled: true
```

2. 느린 쿼리의 기준이 될 임계치 설정(milli second 단위)
```yml
my:
  jpa:
    query:
      logger:
        enabled: true
        local-query-threshold: 3000 #default(3 seconds)
```

3. 빈도에 대한 측정 시간 및 빈도 기준 설정(milli second 단위)
```yml
my:
  jpa:
    query:
      logger:
        enabled: true
        global-query-measurement-time-ms: 600000  #default(1 hour)
        global-query-invoke-count-threshold: 100  #default(100 times)
```