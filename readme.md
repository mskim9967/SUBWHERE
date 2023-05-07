<br />
<br />
<div align="center">
  <a href="https://github.com/mskim9967/SUBWHERE">
    <img src="https://user-images.githubusercontent.com/54897403/210173913-8735ec03-e41e-4d35-80c8-4df32a14e2b8.gif" alt="Logo" width="750px">
  </a>


  <h3 align="center">SUBWHERE</h3>

  <p align="center">
    <em> 내 손 안의 <b>지하철 전광판</b> </em>
    <br />
    <sub> 본 프로젝트는 소프트웨어학부 캡스톤디자인(1) 과목 산출물로서, 실서비스를 제공하지 않습니다. </sub>
    <br />
    <br />
  </p>
  
</div>
<br />
<br />



# About This Project

<br/>

- 👀 **시력이 좋지 않아요**
- ✈️ **외국에서 놀러 왔는데…지하철 어려워요**
- 🧳 **출퇴근시간… 전광판이 안보여…**
- 🎧 **지하철에서 음악은 필수죠**
- 👵🏻 **나이를 먹어서 전광판 보기 어렵네요**
- 😪 **졸다가 깼는데…여기가 어디죠??**

<br/>

 여러가지 이유로 전광판을 보기 어려운 상황인가요?

 걱정하지 마세요. 어떠한 설치도 필요하지 않습니다. 언제든 **One Touch**면 충분합니다!

 **SUBWHERE**는 지하철 내에 부착된 NFC 스티커와 스마트폰을 Tag하여 지하철 전광판을 확인할 수 있는 웹서비스입니다.

 *전/후역*, *출입문 방향*, *종착지*와 같은 전광판 정보 뿐만이 아닌 *신고 및 문의*, *현위치 공유*와 *도착예상시각* 등 같은 다양한 기능 또한 제공됩니다.
 
<br>
<br>
<p align="center">
<img src="https://user-images.githubusercontent.com/54897403/210174262-43c7b683-739b-44f5-a9b2-9f6eb5b4f5af.png" width="600px" >
<img src="https://user-images.githubusercontent.com/54897403/210175823-041569ea-a02f-4116-ada9-6626be0eab33.png" width="600px" >
</p>

<br>
<br>


# 이 서비스가 왜 필요하죠?

지하철 내에서 **전광판을 확인하기 어려운 상황**을 해결하고 싶었습니다.

 대중교통을 이용하지 않는 서울 시민을 찾기는 매우 어렵습니다. 특히, 지하철은 통학 및 출퇴근 시간에도 막힘 없이 목적지로 빠르게 이동할 수 있어 그 이용률이 매우 높습니다. 하지만 많은 이용객들이 단시간에 몰려 생기는 필연적인 문제점들도 많은데, 저희는 객차 내의 전광판에 관한 문제점을 해결하고자 하였습니다.
 

## 전광판은 장식이냐?

 객차 내에는 행선지, 현재 역, 다음 역, 환승 정보 등을 표시해 주는 디스플레이, **전광판이** 출입문 상단, 혹은 객차 중간 지점에 설치되어 있습니다. 
 작은 디스플레이에 **여러 정보**를 **표시**하려 하다 보니, 매번 정보들을 바꿔가며 보여줄 수 밖에 없죠.  
 
 **특히, 2호선의 경우 광고 영역이 디스플레이의 8할 이상을 차지**하기 때문에 현재 역 정보를 즉각 알아내기가 매우 어렵습니다. 타이밍을 한번 놓치게 되면, 20초 가량을 전광판만 쳐다보고 있어야 하죠.

<p align="center">
<img src="https://user-images.githubusercontent.com/54897403/210176742-2229db95-c1df-489f-89d1-5125cc4f7907.png" height="200px" />
<img src="https://user-images.githubusercontent.com/54897403/210176746-17970553-ddb6-43fa-8770-762741dee725.png" height="200px" />
</p>


## 그럼 방송을 똑바로 들어!

 **음성 방송**으로도 현재역을 알려주기도 하나, 광고 및 환승 정보가 음성 안내 시간의 대부분을 차지하고, 그 마저도 대부분의 이용객들은 지하철 내에서 **이어폰, 혹은 헤드셋**을 반 필수적으로 착용하기 때문에 내용을 듣지 못하는 것이 현실입니다.


## 잠깐 이어폰빼거나 전광판 보는게 뭐가 어렵다고…

 맞습니다. 전광판을 놓치지 않고 잘 본다면 해결될 간단한 문제이기도 하나, **시력이 좋지 않은** 이용객들은 작은 전광판의 글씨가 잘 보이지 않기도 할 뿐더러, **출퇴근 시간**의 경우 열차가 이용객들로 가득 차기 때문에 전광판을 보려 해도 볼 수 없는 상황이죠.

<p align="center">
<img src="https://user-images.githubusercontent.com/54897403/210176828-1e5b18ad-50e7-4f59-96fc-4df5fb42574f.png" height="200px" />
</p>

 이뿐만이 아닙니다. 대한민국의 고령화가 갈수록 심해지는 만큼 **고령층**의 지하철 이용률도 비례하여 증가하고 있습니다. 그분들의 **노화된 시력과 청력은** 즉각 정보를 파악하는데 많은 어려움을 겪고 있으며, 그러기에 주변 사람들에게 현재 지하철 위치를 물어보곤 하십니다. **외국인 관광객**들 또한 마찬가지로 익숙하지 않은 환경에서 현재 위치를 전광판 만으로 파악하기에는 쉽지 않습니다.

 실제로 [언론에서 이러한 문제점을 취재](https://www.hankookilbo.com/News/Read/202001211893041250)하기도 했으며, 저희 팀원들 또한 이 불편함을 매일 몸소 겪고 있습니다.

<br/>
<br/>

# User Flow

## 좌석에 앉아있는 경우

1. 좌석 다리사이 밑에  부착된 NFC 스티커에 스마트폰을 태깅
2. 웹브라우저로 SubWhere에 접속하여 즉시 열차의 위치 확인 가능

<div align="center">
<img src="https://user-images.githubusercontent.com/54897403/210175037-62f4fad8-59c0-463f-893d-a414b600bbda.gif" />
<br/>
<sub>Android (Samsung Galaxy S9, Android 10)</sub>
</div>
<br>

## 지하철에 탑승하는 경우
  
1. 지하철 문이 열리며 열차에 **탑승**
2. **출입문 옆**에 부착된 **NFC 스티커**에 스마트폰을 **태깅**
3. 웹브라우저에 SubWhere 탭이 열려있음
4. 역을 확인하고 싶을 때마다 **웹브라우저를 열어**서확인

<div align="center">
<img src="https://user-images.githubusercontent.com/54897403/210175092-479a7be7-511b-44ce-bc27-e1d046c83f24.gif" />
<br/>
<sub>iOS (APPLE iPhone 11 Pro Max, iOS 16)</sub>
</div>
<br>
<br>


# Features
<br>

<p align="center">
<img src="https://user-images.githubusercontent.com/54897403/210175919-7b5db39b-dfa0-4f1c-9470-9489be49eb25.png">
</p>


## 신고/문의

현재 탑승중인 객차, 칸에 대한 즉각적인 신고 혹은 문의가 가능합니다.

칸 온도조절, 마스크 미착용, 잡상인 신고 템플릿을 기본으로 가지고 있으며, 템플릿 선택 시 서울교통공사 민원실 연락처로 칸번호와 템플릿 내용을 담은 메시지 전송 창으로 넘어가게 됩니다.

<br>
<p align="center">
<img src="https://user-images.githubusercontent.com/54897403/210175486-17e77618-40a2-4a6d-9e21-edb3dace168d.PNG" width="300px" />
<img src="https://user-images.githubusercontent.com/54897403/210175543-0f4c16a8-3e34-418c-9f3a-be9b86bf50a4.PNG" width="300px" />
</p>
<br>


## 현 위치 공유

다른 사람에게 현재 탑승중인 지하철 위치를 카카오톡 메신저를 통해 공유할 수 있습니다.

해당 메시지를 받은 사람은 링크를 클릭하여 보낸 사람의 지하철 전광판을 공유 받을 수 있습니다.

<br>
<p align="center">
<img src="https://user-images.githubusercontent.com/54897403/210175596-95af3af2-a004-419e-bc58-d3756bce4a57.PNG" width="300px" />
</p>
<br>

## 도착 예정 시각

현재 역으로부터 행선지까지의 모든 역들의 도착 예정 시각을 한눈에 확인할 수 있습니다.

<br>
<p align="center">
<img src="https://user-images.githubusercontent.com/54897403/210175642-02e338c7-9dc9-443f-88a9-a7deae750e8d.PNG" width="300px" />
</p>
<br>
<br>


# Achievement

**Smart City**
- 지나친 도시화로 인해 생긴 교통 관련 문제
- IT 기술을 활용하여 도시의 문제를 해결하여 시민들의 삶의 질 향상

**Equality**
- 시력/청력이 감퇴된 고령자, 시각/청각 장애인, 외국인
- 기술을 통해 모두에게 평등한 서비스를 제공

<br>
<br>

# Next Step

**지원 언어 확대**
- 추후 중국어, 일본어, 스페인어, 힌디어 등 다양한 언어를 지원하여 국적에 구애 받지 않는 서비스 지원

**서비스 지역 확대**
- 현재는 서울시 1~9호선만 지원하지만 추후에는 인천, 대전, 부산, 광주, 대구 등 전국 지하철에 적용하여 모든 지역에서 사용 가능한 서비스 구축

**수익형 모델**
- 지하철 전광판과 음성안내로 재생되던 광고를 SUBWHERE 서비스 내에 배너 광고로 삽입하여 안정적인 서비스를 위한 수익 창출

<br>
<br>


# Architecture

<br>
<p align="center">
<img src="https://user-images.githubusercontent.com/54897403/210175737-cea38667-52d9-4081-828e-54c82c5b54a4.png" />
</p>
<br>


# Team Member

<table>
<thead>
    <tr>
      <th align="center">성명</th>
      <th align="center">소속</th>
      <th align="center">Role</th>
      <th align="center">Github</th>
    </tr> 
</thead>

<tbody>
    <tr>
      <td align="center">김명승(PM)</td>
      <td align="center">소프트웨어학부</td>
      <td align="center">기획, UI Design<br/>Dev(Admin Application, Infra)</td>
      <td align="center">	
        <a href="https://github.com/mskim9967">
            <img src="https://img.shields.io/badge/mskim9967-655ced?style=social&logo=github"/>
        </a>
      </td>
    </tr>
    <tr>
      <td align="center">문성준</td>
      <td align="center">소프트웨어학부</td>
      <td align="center">Dev(Frontend)</td>
      <td align="center">	
        <a href="https://github.com/moon5381">
            <img src="https://img.shields.io/badge/moon5381-655ced?style=social&logo=github"/>
        </a>
      </td>
    </tr>
    <tr>
      <td align="center">배정우</td>
      <td align="center">소프트웨어학부</td>
      <td align="center">Dev(Backend)</td>
      <td align="center">	
        <a href="https://github.com/wjddn2165">
            <img src="https://img.shields.io/badge/wjddn2165-655ced?style=social&logo=github"/>
        </a>
      </td>
    </tr>
</tbody>
</table>

<br>
<br>
<br>

# Dependencies

**Frontend**

  ![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)

| Library         | License | Description                     |
|-----------------|---------|---------------------------------|
|     [React.js](https://reactjs.org)    |   MIT   | Framework                       |
|   [React Router](https://reactrouter.com/en/main)  |   MIT   | URL Routing                     |
|   [Material-Ui](https://mui.com)   |   MIT   | UI Component                    |
|      [Axios](https://axios-http.com)      |   MIT   | HTTP connection                 |
| [enable-auto-tts](https://www.npmjs.com/package/enable-auto-tts) |   MIT   | enable TTS automatically on iOS |

**Backend**

  ![Spring Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=spring%20boot&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white) 

| Library                      | License    | Description                |
|------------------------------|------------|----------------------------|
|          [Spring Boot](https://spring.io/projects/spring-boot)         | Apache 2.0 | Framework                  |
|             [MySQL](https://www.mysql.com)            |   GPL 2.0  | Database Management System |
| spring boot starter data jpa | Apache 2.0 | JPA abstract library       |
|     mysql connector java     |   GPL 2.0  | DBMS connector             |

**Infra**

  ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) ![Linux](https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=linux&logoColor=black)

**Admin Applicaiton**

  ![React Native](https://img.shields.io/badge/react_native-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)

| Library                  | License | Description     |
|--------------------------|---------|-----------------|
|    [React Native (Expo)](https://expo.dev)   |   MIT   | Framework       |
|        [NativeBase](https://nativebase.io)        |   MIT   | UI Library      |
| [react-native-nfc-manager](https://www.npmjs.com/package/react-native-nfc-manager) |   MIT   | NFC SDK Library |
  

<br>
<br>
<br>
