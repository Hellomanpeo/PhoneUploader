# PhoneUploader - 통화 녹음 전송 앱

## 기능
1. 전화 통화 종료 감지
2. "전송하시겠습니까?" 알림 표시
3. "예" 클릭 시 PC HTTP 서버로 녹음 파일 전송
4. "아니오" 클릭 시 무시

## 설정
- PC IP 주소: 메인 화면에서 설정 (예: 192.168.219.113)
- PC 포트: 9876 (기본값)

## 필요 권한
- READ_PHONE_STATE: 통화 상태 감지
- RECORD_AUDIO: 통화 녹음
- READ_CALL_LOG: 통화 기록
- INTERNET: PC 전송
- READ_EXTERNAL_STORAGE: 녹음 파일 읽기

## 빌드
Android Studio에서 프로젝트 열기 → 빌드 → APK 생성
