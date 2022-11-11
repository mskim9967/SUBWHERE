import { RiArrowRightSLine, RiArrowLeftSLine, RiAlarmWarningFill, RiKakaoTalkFill, RiTimeFill } from 'react-icons/ri';
import { useEffect, useState,useRef } from 'react';
import {  useSearchParams } from 'react-router-dom';
import { axiosInstance } from '../axiosInstance';
import Modal from '@mui/material/Modal';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import RefreshIcon from '@mui/icons-material/Refresh';
import AcUnitIcon from '@mui/icons-material/AcUnit';
import CleaningServicesIcon from '@mui/icons-material/CleaningServices';
import React from "react";
import AirIcon from '@mui/icons-material/Air';
import { ToastNotification } from '../ToastNotification';




function SubwayPage({ theme, lang }) {
 
  
  const [nowtrain, setNowtrain] = useState(true);
  const [report, setReport] = useState(false);

  let [toastState, setToastState] = useState(false);
  let [timeState, setTimeState] = useState(5);
  const [searchParams, setSearchParams] = useSearchParams();
  const [modalOpen, setModalOpen] = useState(false);
  const [trainfo,setTrainfo]=useState([]);
  const [nexinfo,setNexinfo]=useState([]);
  const colors=["#0d3692","#33a23d","#fe5d10","#00a2d1","#8b50a4","#c55c1d","#54640d","#f14c82","#aa9872"];
  let asd=searchParams.get('subwayNm');//subwayNm으로 체크할거임 
  const linecolor=colors[asd.replace('호선','')-1]//subwayNm으로 체크할거임 get.replace~~
  const style = {
    color: theme.gray1,
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 300,
    bgcolor: theme.bg1,
    border: '2px solid ',
    bordercolor:theme.gray1,
    boxShadow: 24,
    p: 4,
    borderRadius: '20px',
  };
  const shareKakao = () => {
    window.Kakao.Share.sendCustom({
      templateId: 85076, 
      templateArgs:{
        title: {kor:trainfo.statnNm.kor,eng:trainfo.statnNm.eng}[lang],
        state: {kor:{0:'역에 진입',1:'역에 도착',2:'역에서 출발',3:'역에서 출발'}[trainfo.trainSttus],eng:
        {0:'approach',1:'arrival',2:'leave',3:'leave'}[trainfo.trainSttus]}[lang],
      },
    });
  };
  const Nextreceive=()=>{// 도착 예정시간 받아오는거 
  axiosInstance.get(`/subway/arrival?subwayNm=${searchParams.get('subwayNm')}&updnLine=${trainfo.updnLine}&statnNm=${trainfo.statnNm}`)
  .then((res) => {
    setNexinfo(res.data.data)
    console.log(res.data.data)
  })
  .catch(function (error) {
    if (error.response) {
      // 요청이 이루어졌으며 서버가 2xx의 범위를 벗어나는 상태 코드로 응답했습니다.
      console.log(error.response.data);
      console.log(error.response.status);
      console.log(error.response.headers);
    }
    else if (error.request) {
      // 요청이 이루어 졌으나 응답을 받지 못했습니다.
      // `error.request`는 브라우저의 XMLHttpRequest 인스턴스 또는
      // Node.js의 http.ClientRequest 인스턴스입니다.
      console.log(error.request);
    }
    else {
      // 오류를 발생시킨 요청을 설정하는 중에 문제가 발생했습니다.
      console.log('Error', error.message);
    }
    console.log(error.config);
  });;
    
}
 

const Inforeceive=()=>{
  axiosInstance.get(`/subway?subwayNm=${searchParams.get('subwayNm')}&trainNo=${searchParams.get('trainNo')}`)
  .then((res) => {
    setTrainfo(res.data.data)
    console.log(res.data.data)
  
    
  })
  .catch(function (error) {
    if (error.response) {
      console.log(error.response.data);
      console.log(error.response.status);
      console.log(error.response.headers);
      if(error.response.data.code===-1){
        setNowtrain(false);   
      }
  
    }
    else if (error.request) {
      console.log(error.request);
    }
    else {
      console.log('Error', error.message);
      
    }
    console.log(error.config);
    
  });;
    
}
const fontRef = useRef(5);
useEffect(() => {
  setInterval(() => {
    Inforeceive() 
  }, 5000);
}, []);//5초마다 새로고침하는거
let nowst='d';

const notifi=()=>{
  if(trainfo.trainSttus==0){
    if(toastState==0){
      if(nowst!=trainfo.statnNm){
        nowst=trainfo.statnNm;
        setToastState(true);
      
      }
    }
  }

}

  return (
    useEffect(() => {
      Inforeceive()
    },[]),
    useEffect(() => {
      notifi()
    },[trainfo.trainSttus]),

    <div style={{ position:'fixed',width: '100vw', padding: '100px 0px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
    
      

      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <div style={{color:theme.gray1}}>{
          {
            kor:((new Date()).toLocaleString('ko-KR')),
            eng:(new Date()).toLocaleString('en-US'),
          }[lang]
        
        }
        <RefreshIcon onClick={()=>{
           Inforeceive()
           setTimeState((fontRef.current += 1));
           console.log('click')
        }
         
          }/></div>
        <div style={{ height: '10px' }} />
        

        <div style={{ fontSize: '20px', fontWeight: '500', color: theme.gray1 }}>
          {
            {
              kor: '현재 탑승 중이신 열차는',
              eng: 'This train is',
            }[lang]
          }
        </div>

        <div style={{ height: '30px' }} />

        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div
            style={{
              color: '#ffffff',
              backgroundColor: linecolor,
              padding: '5px 10px',
              borderRadius: '20px',
              fontSize: '15px',
              fontWeight: 600,
            }}//trainfo.statnTnm
          >
          {{kor:searchParams.get('subwayNm'),eng:searchParams.get('subwayNm').replace('호선','line')}[lang]} -{{kor:trainfo?.statnTnm?.kor+'행',
            eng:trainfo?.statnTnm?.eng}[lang]} 
          </div> 

          <div
            style={{
              color: '#ffffff',
              backgroundColor: '#ff4444',
              padding: '5px 15px',
              borderRadius: '20px',
              fontSize: '15px',
              fontWeight: 600,
            }}//{trainfo.direcctAt===1?<p>급행열차</p>:<p>일반열차</p>}
          >{
            {
              kor:(trainfo?.directAt==1?<div>급행열차</div>:<div>일반열차</div>),
              eng:(trainfo?.directAt==1?<div>Express</div>:<div>All Stop</div>)

            }[lang]
          }
            
          </div>
         
        </div>

        <div style={{ height: '40px' }} />

        <div style={{display: 'flex', alignItems: 'center', flexDirection: 'column', gap: 14 }}>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>{
          {
            kor:trainfo?.prevStatns?.stationNameList[0]?.kor,
            eng:trainfo?.prevStatns?.stationNameList[0]?.eng
          }[lang]      
        }
          </div>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>
          {
          {
            kor:trainfo?.prevStatns?.stationNameList[1]?.kor,
            eng:trainfo?.prevStatns?.stationNameList[1]?.eng
          }[lang]      
        }</div>
          <div style={{ position: 'relative', margin: '-7px 0' }}>
            {
              ((trainfo.subwayHeading==0)&&(trainfo.trainSttus==0||trainfo.trainSttus==1))?trainfo?.statnNm?.length>5?
              <div style={{ position: 'absolute', fontSize: '40px', bottom: 'center', left: -40, opacity: '100%', color:theme.gray1 }}>
              <RiArrowLeftSLine />
            </div>:<div style={{ position: 'absolute', fontSize: '40px', bottom: 13, left: -40, opacity: '100%', color:theme.gray1 }}>
              <RiArrowLeftSLine />
            </div>
            :trainfo?.statnNm?.length>5?
            <div style={{ position: 'absolute', fontSize: '40px', top: 'center', left: -40, opacity: '20%', color:theme.gray1 }}>
              <RiArrowLeftSLine />
            </div>:<div style={{ position: 'absolute', fontSize: '40px', top: 13, left: -40, opacity: '20%', color:theme.gray1 }}>
              <RiArrowLeftSLine />
            </div>
            }

            <div style={{ fontSize: '70px', fontWeight: '700', letterSpacing: 3,color:theme.gray1 ,whiteSpace:"pre-line"}}>
            {
              {
                false:'운행 종료된 \n열차입니다.',
                true:{kor:(trainfo?.statnNm?.kor.length>5?<div style={{ fontSize: '30px', fontWeight: '700', letterSpacing: 3,color:theme.gray1 ,whiteSpace:"pre-line"}}>{trainfo?.statnNm?.kor}</div>:
                <div style={{ fontSize: '60px', fontWeight: '700', letterSpacing: 3,color:theme.gray1 ,whiteSpace:"pre-line"}}>{trainfo?.statnNm?.kor}</div>),
              eng:(trainfo?.statnNm?.eng.length>8?trainfo?.statnNm?.eng.length>13?<div style={{ fontSize: '15px', fontWeight: '700', letterSpacing: 3,color:theme.gray1 ,whiteSpace:"pre-line"}}>{trainfo?.statnNm?.eng}</div>:<div style={{ fontSize: '30px', fontWeight: '700', letterSpacing: 3,color:theme.gray1 ,whiteSpace:"pre-line"}}>{trainfo?.statnNm?.eng}</div>:
              <div style={{ fontSize: '60px', fontWeight: '700', letterSpacing: 3,color:theme.gray1 ,whiteSpace:"pre-line"}}>{trainfo?.statnNm?.eng}</div>)}[lang]

              }[nowtrain]
            }
            </div>
            {{kor:
              ((trainfo.subwayHeading==1)&&(trainfo.trainSttus==0||trainfo.trainSttus==1))?trainfo?.statnNm?.kor.length>5?
              <div style={{ position: 'absolute', fontSize: '40px', top: '1vw', opacity: '100%',right: -40 ,color:theme.gray1}}>
              <RiArrowRightSLine />
            </div>:<div style={{ position: 'absolute', fontSize: '40px', bottom: 13, opacity: '100%',right: -40 ,color:theme.gray1}}>
              <RiArrowRightSLine />
            </div>
            :trainfo?.statnNm?.kor.length>5?
            <div style={{ position: 'absolute', fontSize: '40px', top: '1vw',opacity: '20%', right: -40 ,color:theme.gray1}}>
              <RiArrowRightSLine />
            </div>
            :<div style={{ position: 'absolute', fontSize: '40px', bottom: 13,opacity: '20%', right: -40 ,color:theme.gray1}}>
            <RiArrowRightSLine />
          </div>,
          eng:((trainfo.subwayHeading==1)&&(trainfo.trainSttus==0||trainfo.trainSttus==1))?trainfo?.statnNm?.eng.length>5?
          <div style={{ position: 'absolute', fontSize: '40px', top: '1vw', opacity: '100%',right: -40 ,color:theme.gray1}}>
          <RiArrowRightSLine />
        </div>:<div style={{ position: 'absolute', fontSize: '40px', bottom: 13, opacity: '100%',right: -40 ,color:theme.gray1}}>
          <RiArrowRightSLine />
        </div>
        :trainfo?.statnNm?.eng.length>5?
        <div style={{ position: 'absolute', fontSize: '40px', top: '3vw',opacity: '20%', right: -40 ,color:theme.gray1}}>
          <RiArrowRightSLine />
        </div>
        :<div style={{ position: 'absolute', fontSize: '40px', top: 14,opacity: '20%', right: -40 ,color:theme.gray1}}>
        <RiArrowRightSLine />
      </div>
        }[lang]
            }
            <div style={{ position: 'absolute', fontSize: '27px', fontWeight: '600', right: 0, color:theme.gray1 }}> {
          {
            0:{
              eng:'approach',
              kor:'진입'
            }[lang],
            1: {
              eng:'arrival',
              kor:'도착'
            }[lang],
            2:{
              eng:'leave',
              kor:'출발'
            }[lang],
            3:'출발'
          }[trainfo?.trainSttus]        
        }
        </div>
          </div>
          <div></div>
          <div></div>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>{
          {
            kor:trainfo?.nextStatns?.stationNameList[0]?.kor,
            eng:trainfo?.nextStatns?.stationNameList[0]?.eng
          }[lang]      
        }</div>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>{
          {
            kor:trainfo?.nextStatns?.stationNameList[1]?.kor,
            eng:trainfo?.nextStatns?.stationNameList[1]?.eng
          }[lang]      
        }</div>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>{
          {
            kor:trainfo?.nextStatns?.stationNameList[2]?.kor,
            eng:trainfo?.nextStatns?.stationNameList[2]?.eng
          }[lang]      
        }</div>
        </div>
        {
          {
            true: <ToastNotification setToastState={setToastState} lang={lang} train={{kor:trainfo?.statnNm?.kor,
            eng:trainfo?.statnNm?.eng}[lang]}></ToastNotification>,
            false: console.log(toastState),
          }[toastState]        
        }
        
      </div>
      <div style={{ height: '80px' }} />
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          height: '60px',
          fontSize: '13px',
          fontWeight: 700,
          color: theme.gray1,
          backgroundColor: theme.bacco,
          padding: '20px 0',
          borderRadius: '20px',
          
        }}
      >
        <div
          style={{
            width: '90px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 5,
            borderRight: 'solid white 1px',
            padding: '10px',
          }}
        >
          <RiAlarmWarningFill onClick={() => {
            setReport(true);
          }} style={{ color: 'FFB833', fontSize: '20px' }} />
          
          {{
            kor:'신고',
            eng:'report'
          }[lang]}
          <br />
          {
            {
              kor:'문의',
              eng:'inquiry'
            }[lang]
          }
        </div>
        <div
          style={{
            width: '90px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 5,
            borderRight: 'solid white 1px',
            padding: '10px',
          }}
        
        >
          
          <RiKakaoTalkFill onClick={shareKakao} style={{ color: 'FFE600', fontSize: '20px' }} />
          {
            {
              kor:'현위치',
              eng:'Location',
            }[lang]
          }
          <br />
          {
            {
              kor:'공유',
              eng:'sharing',
            }[lang]
          }
        </div>
        <div
          style={{ width: '90px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 5, padding: '10px' }}
          onClick={() => {
            setModalOpen(true);
            Nextreceive();
          }}
        >
          <RiTimeFill style={{ color: '92FF6B', fontSize: '20px' }} />
          {
            {
              kor:'도착',
              eng:'ETA',
            }[lang]
          }
          <br />
          {
            {
              kor:'예정 시간',
              eng:' ',
            }[lang]
          }
        </div>
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)}>
        <Box sx={style}>
          <Typography id='modal-modal-title' variant='h6' component='h2'>
            {{
              kor:'도착 예정 시간',
              eng:'estimated time of arrival',
            }[lang]
            }
          </Typography>
          <Typography id='modal-modal-description' sx={{ mt: 2 }}>
            {
              {
                kor:'동작 00:00:00 ~~',
                eng:'dongjak 00:00:00 ing~',
              }[lang]
            }
          </Typography>
        </Box>
      </Modal>



      <Modal open={report} onClose={() => setReport(false)}>
        <Box sx={style}>
          <Typography id='modal-modal-title' variant='h6' component='h2'>
            {{
              kor:'민원 신고',
              eng:'report',
            }[lang]
            }
          </Typography>
          <div
        style={{
          display: 'flex',
          alignItems: 'center',
          height: '60px',
          fontSize: '13px',
          fontWeight: 700,
          color: theme.gray1,
          backgroundColor: theme.bacco,
          padding: '20px 0',
          borderRadius: '20px',
          
        }}
      >
        <div
          style={{
            width: '90px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 5,
            borderRight: 'solid white 1px',
            padding: '10px',
          }}
        ><a  href="sms:15771234&body="class="sms" style={{position:'relative', color:theme.gray1}}>
          <AcUnitIcon  style={{ color:'#00F5FF	', fontSize: '20px' }} />
          
         
          </a>
          {{
            kor:'추워요',
            eng:'it\'s cold'
          }[lang]}
        </div>
        <div
          style={{
            width: '90px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 5,
            borderRight: 'solid white 1px',
            padding: '10px',
          }}
        
        >
          <a  href="sms:15771234&body=보낼메세지"class="sms" style={{position:'relative', color:theme.gray1}}>
          <AirIcon  style={{ color: '	#1E82FF', fontSize: '20px' }} />
          </a>
          {
            {
              kor:'더워요',
              eng:'it\'s hot',
            }[lang]
          }
          
        </div>
        <div
          style={{ width: '90px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 5, padding: '10px' }}
        
        >
          
          <a  href="sms:15771234&body=보낼메세지"class="sms" style={{position:'relative', color:theme.gray1}}>
          <CleaningServicesIcon style={{ color: '92FF6B', fontSize: '20px' }} />
          </a>
          {
            {
              kor:'칸이더러워요',
              eng:'train is dirty',
            }[lang]
          }
        </div>
      </div>
          
          
        </Box>
      </Modal>
      
    </div>
  );
}

export default SubwayPage;