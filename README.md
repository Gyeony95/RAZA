# RAZA

낮선 사람과의 소통

## 사용기술
- Platform : Android, AWS
- IDE : Android Studio, Pycharm, VSCode
- Language : Kotlin, Java, JavaScript, Python
- Database : MySQL

- Back End 
  - HTTP Server : Django
  - Socket Server , Signalling Server : Node.js


## 기능

### 로그인
![login](https://user-images.githubusercontent.com/46639812/90663132-3b7f9980-e284-11ea-8959-4ba9c94676a0.gif)

- DB에서 ID/PW 비교
- 로그인 후에는 전면카메라 surfaceview 출력

### 영상통화용 카메라 open
![RTCon](https://user-images.githubusercontent.com/46639812/90664007-61596e00-e285-11ea-9b8c-102f146c1bc7.gif)

- 메인화면 상단의 '채팅시작'버튼을 누르면 서버에 연결되고 영상통화 대기상태가 됨

### 영상통화 connect
![connect2](https://user-images.githubusercontent.com/46639812/90664879-358ab800-e286-11ea-9fcf-e401551de586.gif)

- 서버에 2명 이상의 대기자가 있으면 자동으로 매핑하여 1:1 영상통화가 시작됨

### 영상통화 상대 바꾸기
![reconnect2](https://user-images.githubusercontent.com/46639812/90665045-7aaeea00-e286-11ea-928b-2df0ea99856b.gif)

- 영상통화 도중 상단의 '다음'버튼을 누르면 현재 진행하던 영상통화를 종료하고 다른 대기자와 영상통화를 시작함

### 친구추가
![add_friend](https://user-images.githubusercontent.com/46639812/90664935-4c310f00-e286-11ea-9a20-a3f41d45faf9.gif)

- 친구의 고유아이디를 사용하여 친구를 추가할 수 있음

### 대화시도
![fisrt_hi](https://user-images.githubusercontent.com/46639812/90665221-b6e24a80-e286-11ea-9040-82a8c772c169.gif)

- 추가한 친구에게 대화를 걸 수 있음

### 실시간 채팅
![second_hi](https://user-images.githubusercontent.com/46639812/90665315-d6797300-e286-11ea-92ce-fe7e8ebb1ecb.gif)

- socket을 사용하여 실시간으로 채팅
