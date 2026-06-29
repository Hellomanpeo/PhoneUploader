# PhoneUploader APK 설치 방법

## Step 1: GitHub 계정 만들기

```
1. 웹 브라우저에서 https://github.com 접속
2. "Sign up" 클릭
3. 이메일, 비밀번호, 사용자 이름 입력
4. 무료 플랜 선택
5. 이메일 인증 완료
```

## Step 2: 저장소 만들기

```
1. GitHub 로그인
2. 우측 상단 "+" 클릭 → "New repository"
3. Repository name: PhoneUploader
4. Public 선택 (무료)
5. "Create repository" 클릭
```

## Step 3: 파일 업로드

```
1. 생성된 저장소 페이지에서 "uploading an existing file" 클릭
2. C:\Users\rkdgk\Desktop\PMSWORK\PhoneUploader 폴더의 모든 파일을 드래그앤드롭
   - .github 폴더 포함
   - app 폴더 포함
   - build.gradle, settings.gradle, gradle 폴더 포함
3. "Commit changes" 클릭
```

## Step 4: APK 빌드 (자동)

```
1. 저장소 페이지 상단 "Actions" 탭 클릭
2. "Build APK" 워크플로우가 자동으로 실행됨
3. 노란색 원 = 빌드 중 (2~5분 대기)
4. 초록색 체크 = 빌드 완료
```

## Step 5: APK 다운로드

```
1. 완료된 워크플로우 클릭
2. "Artifacts" 섹션에서 "phone-uploader-debug" 클릭
3. ZIP 파일 다운로드
4. 압축 해제 → app-debug.apk 파일 확인
```

## Step 6: 핸드폰에 전송 및 설치

```
1. app-debug.apk 파일을 핸드폰으로 전송
   - 카카오톡 "나에게 보내기"
   - 이메일 첨부
   - 클라우드 드라이브 (Google Drive 등)
2. 핸드폰에서 파일 실행
3. "알 수 없는 앱 설치" 경고 → "설정" 클릭 → "이 출처 허용" 켜기
4. "설치" 클릭
5. "열기" 클릭하여 앱 실행
```

## Step 7: 앱 설정

```
1. "통화전송" 앱 실행
2. PC IP 주소: 192.168.219.113
3. 포트: 9876
4. "설정 저장" 클릭
```

## Step 8: 테스트

```
1. PMS 실행 (PC)
2. 핸드폰으로 아무 번호에 전화
3. 통화 종료
4. 알림: "전송하시겠습니까?" → "예"
5. PMS에서 자동 처리 확인
```
