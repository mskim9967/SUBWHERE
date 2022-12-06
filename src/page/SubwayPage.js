import { RiArrowRightSLine, RiArrowLeftSLine, RiAlarmWarningFill, RiKakaoTalkFill, RiTimeFill } from 'react-icons/ri';
import { useEffect, useState, useRef } from 'react';
import { useSearchParams } from 'react-router-dom';
import { axiosInstance } from '../axiosInstance';
import Modal from '@mui/material/Modal';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import AcUnitIcon from '@mui/icons-material/AcUnit';
import CleaningServicesIcon from '@mui/icons-material/CleaningServices';
import MasksIcon from '@mui/icons-material/Masks';
import React from 'react';
import AirIcon from '@mui/icons-material/Air';
import { ToastNotification } from '../ToastNotification';
import ReportIcon from '@mui/icons-material/Report';
import ProductionQuantityLimitsIcon from '@mui/icons-material/ProductionQuantityLimits';
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant='filled' {...props} />;
});

function SubwayPage({ theme, lang, sound }) {
  const tableStyle = {
    borderTop: `1px solid ${theme.gray7}`,
    color: theme.gray2,
    borderRight: 0,
    borderLeft: 0,
  };

  var smstext = '';

  function sendSMS(smstext) {
    let whatphone = 0;
    var IorA = navigator.userAgent.toLowerCase();
    if (IorA.indexOf('android') !== -1) {
      whatphone = 0;
    } else if (IorA.indexOf('iphone') !== -1) {
      whatphone = 1;
    }

    var text = `[SUBWHERE]%0a${searchParams.get('subwayNm')} ${searchParams.get('carNo')}칸%0a${smstext}`;
    if (whatphone == 0) {
      window.location.href = 'sms:15771234?body=' + text;
    } else {
      window.location.href = 'sms:15771234&body=' + text;
    }
  }
  const addTime = (arrivalTime) => {
    const min = date.getMinutes() + arrivalTime;
    const hou = date.getHours();
    var total = '';
    if (min < 60) {
      if (min < 10) {
        total = hou + ':0' + min;
      } else {
        total = hou + ':' + min;
      }
      return total;
    } else if (min < 120) {
      if (min - 60 < 10) {
        total = hou + 1 + ':0' + (min - 60);
      } else {
        total = hou + 1 + ':' + (min - 60);
      }
      return total;
    } else if (min < 180) {
      if (min - 120 < 10) {
        total = hou + 2 + ':0' + (min - 120);
      } else {
        total = hou + 2 + ':' + (min - 120);
      }
      return total;
    } else if (min < 240) {
      if (min - 180 < 10) {
        total = hou + 3 + ':0' + (min - 180);
      } else {
        total = hou + 3 + ':' + (min - 180);
      }
      return total;
    }
  };

  const [nowtrain, setNowtrain] = useState(true);
  const [report, setReport] = useState(false);
  const date = new Date();

  let [toastState, setToastState] = useState(false);
  let [timeState, setTimeState] = useState(5);
  const [searchParams, setSearchParams] = useSearchParams();
  const [modalOpen, setModalOpen] = useState(false);
  const [isFirst, setFirst] = useState(true);
  const [trainfo, setTrainfo] = useState([]);
  const [nexinfo, setNexinfo] = useState([]);
  const colors = ['#0d3692', '#33a23d', '#fe5d10', '#00a2d1', '#8b50a4', '#c55c1d', '#54640d', '#f14c82', '#aa9872'];
  let kakaosubwayNm = searchParams.get('subwayNm'); //subwayNm으로 체크할거임
  let kakaotrainNo=searchParams.get('trainNo');
  let kakaocarNo=searchParams.get('carNo');
  const linecolor = colors[kakaosubwayNm.replace('호선', '') - 1]; //subwayNm으로 체크할거임 get.replace~~
  const style = {
    color: theme.gray1,
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 300,
    bgcolor: theme.bg1,
    border: '2px solid ',
    bordercolor: theme.gray1,
    boxShadow: 24,
    p: 4,
    borderRadius: '20px',
  };

  const shareKakao = () => {
    window.Kakao.Share.sendCustom({
      templateId: 85076,
      templateArgs: {
        title: { kor: '현재 ' + trainfo.statnNm.kor, eng: trainfo.statnNm.eng }[lang],
        state: {
          kor: { 0: '역에 진입 하였습니다', 1: '역에 도착하였습니다', 2: '역에서 출발하였습니다', 3: '역에서 출발하였습니다' }[trainfo.trainSttus],
          eng: { 0: ' approach', 1: ' arrival', 2: ' leave', 3: ' leave' }[trainfo.trainSttus],
        }[lang],
        address: '?subwayNm='+kakaosubwayNm+'&trainNo='+kakaotrainNo+'&carNo='+kakaocarNo
      },
    });
  };
  const Nextreceive = () => {
    // 도착 예정시간 받아오는거
    axiosInstance
      .get(`/subway/arrival?subwayNm=${searchParams.get('subwayNm')}&trainNo=${searchParams.get('trainNo')}`)
      .then((res) => {
        setNexinfo(res.data.data.arrivalTimeList);
        console.log(date.getMinutes());
      })
      .catch(function (error) {
        if (error.response) {
          // 요청이 이루어졌으며 서버가 2xx의 범위를 벗어나는 상태 코드로 응답했습니다.
          console.log(error.response.data);
          console.log(error.response.status);
          console.log(error.response.headers);
        } else if (error.request) {
          // 요청이 이루어 졌으나 응답을 받지 못했습니다.
          // `error.request`는 브라우저의 XMLHttpRequest 인스턴스 또는
          // Node.js의 http.ClientRequest 인스턴스입니다.
          console.log(error.request);
        } else {
          // 오류를 발생시킨 요청을 설정하는 중에 문제가 발생했습니다.
          console.log('Error', error.message);
        }
        console.log(error.config);
      });
  };

  const Inforeceive = () => {
    axiosInstance
      .get(`/subway?subwayNm=${searchParams.get('subwayNm')}&trainNo=${searchParams.get('trainNo')}`)
      .then((res) => {
        setFirst(false);
        setTrainfo(res.data.data);
      })
      .catch(function (error) {
        if (error.response) {
          console.log(error.response.data);
          console.log(error.response.status);
          console.log(error.response.headers);
          if (error.response.data.code === -1) {
            setNowtrain(false);
            if (!isFirst) window.location.reload();
          }
        } else if (error.request) {
          console.log(error.request);
        } else {
          console.log('Error', error.message);
        }
        console.log(error.config);
      });
  };
  const fontRef = useRef(5);
  useEffect(() => {
    setInterval(() => {
      Inforeceive();
    }, 5000);
  }, []); //5초마다 새로고침하는거
  let nowst = 'd';

  const notifi = () => {
    if (trainfo.trainSttus == 0) {
      if (toastState == 0) {
        if (nowst != trainfo.statnNm) {
          nowst = trainfo.statnNm;
          setToastState(true);
        }
      }
    }
  };

  return (
    useEffect(() => {
      Inforeceive();
    }, []),
    useEffect(() => {
      notifi();
    }, [trainfo.trainSttus]),
    (
      <div
        style={{ position: 'relative', display: 'flex', flexDirection: 'column', alignItems: 'center' }}
        onClick={() => {
          Inforeceive();
          console.log(trainfo.curPosition)
          setTimeState((fontRef.current += 1));
        }}
      >
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <div style={{ color: theme.gray1, fontSize: '15px' }}>
            {
              {
                kor: new Date().toLocaleString('ko-KR'),
                eng: new Date().toLocaleString('en-US'),
              }[lang]
            }
            {/* <RefreshIcon
              onClick={() => {
                Inforeceive();
                setTimeState((fontRef.current += 1));
                console.log('click');
              }}
            /> */}
          </div>
          <div style={{ height: '5px' }} />

          <div style={{ fontSize: '20px', fontWeight: '500', color: theme.gray1 }}>
            {
              {
                kor: '현재 탑승 중이신 열차는',
                eng: 'This train is',
              }[lang]
            }
          </div>

          <div style={{ height: '20px' }} />

          <div style={{ display: 'flex', alignItems: 'center', gap: 7 }}>
            <div
              style={{
                color: '#ffffff',
                backgroundColor: linecolor,
                padding: '5px 12px',
                borderRadius: '20px',
                fontSize: '15px',
                fontWeight: 600,
              }} //trainfo.statnTnmd
            >
              {{ kor: searchParams.get('subwayNm'), eng: 'Line ' + searchParams.get('subwayNm').replace('호선', '') }[lang]}
              {trainfo?.statnTnm && trainfo?.statnTnm?.kor ? ' - ' + { kor: trainfo?.statnTnm?.kor + '행', eng: trainfo?.statnTnm?.eng }[lang] : ''}
            </div>

            <div
              style={{
                color: '#ffffff',
                backgroundColor: trainfo?.directAt != 1 ? '#285CAA' : '#ff4444',
                padding: '5px 12px',
                borderRadius: '20px',
                fontSize: '15px',
                fontWeight: 600,
              }} //{trainfo.direcctAt===1?<p>급행열차</p>:<p>일반열차</p>}
            >
              {
                {
                  kor: trainfo?.directAt == 1 ? <div>급행열차</div> : <div>일반열차</div>,
                  eng: trainfo?.directAt == 1 ? <div>Express</div> : <div>All Stop</div>,
                }[lang]
              }
            </div>
            <div
              style={{ color: '#ffffff', backgroundColor: '#777777', padding: '5px 12px', borderRadius: '20px', fontSize: '15px', fontWeight: 600 }}
            >
              {{ kor: searchParams.get('carNo') + '칸', eng: 'No.' + searchParams.get('carNo') }[lang]}
            </div>
          </div>

          <div style={{ height: '40px' }} />

          <div style={{ display: 'flex', alignItems: 'flex-start', flexDirection: 'column', gap: 5 }}>
            <div style={{ fontSize: '15px', fontWeight: '400', color: theme.gray6 }}>
              {
                {
                  kor: trainfo?.prevStatns?.stationNameList[0]?.kor,
                  eng: trainfo?.prevStatns?.stationNameList[0]?.eng,
                }[lang]
              }
            </div>
            <div style={{ fontSize: '15px', fontWeight: '400', color: theme.gray6 }}>
              {
                {
                  kor: trainfo?.prevStatns?.stationNameList[1]?.kor,
                  eng: trainfo?.prevStatns?.stationNameList[1]?.eng,
                }[lang]
              }
            </div>
            <div style={{ position: 'relative', margin: '-7px 0' }}>
              {nowtrain &&
                (trainfo.subwayHeading == 0 && (trainfo.trainSttus == 0 || trainfo.trainSttus == 1) ? (
                  trainfo?.statnNm?.length > 5 ? (
                    <div style={{ position: 'absolute', fontSize: '40px', bottom: 'center', left: -40, opacity: '100%', color: theme.gray1 }}>
                      <RiArrowLeftSLine />
                    </div>
                  ) : (
                    <div style={{ position: 'absolute', fontSize: '40px', top: 8, left: -40, opacity: '100%', color: theme.gray1 }}>
                      <RiArrowLeftSLine />
                    </div>
                  )
                ) : trainfo?.statnNm?.length > 5 ? (
                  <div style={{ position: 'absolute', fontSize: '40px', top: 'center', left: -40, opacity: '20%', color: theme.gray1 }}>
                    <RiArrowLeftSLine />
                  </div>
                ) : (
                  <div style={{ position: 'absolute', fontSize: '40px', top: 8, left: -40, opacity: '20%', color: theme.gray1 }}>
                    <RiArrowLeftSLine />
                  </div>
                ))}

              <div style={{ fontSize: '70px', fontWeight: '700', letterSpacing: 0, color: theme.gray1, whiteSpace: 'pre-line' }}>
                {
                  {
                    false: { kor: '운행 종료', eng: 'Ended Train' }[lang],
                    true: {
                      kor:
                        trainfo?.statnNm?.kor.length > 5 ? (
                          <div style={{ fontSize: '30px', fontWeight: '700', letterSpacing: 3, color: theme.gray1, whiteSpace: 'pre-line' }}>
                            {trainfo?.statnNm?.kor}
                          </div>
                        ) : (
                          <div style={{ fontSize: '60px', fontWeight: '700', letterSpacing: 3, color: theme.gray1, whiteSpace: 'pre-line' }}>
                            {trainfo?.statnNm?.kor}
                          </div>
                        ),
                      eng:
                        trainfo?.statnNm?.eng.length > 8 ? (
                          trainfo?.statnNm?.eng.length > 13 ? (
                            <div style={{ fontSize: '15px', fontWeight: '700', letterSpacing: 3, color: theme.gray1, whiteSpace: 'pre-line' }}>
                              {trainfo?.statnNm?.eng}
                            </div>
                          ) : (
                            <div style={{ fontSize: '30px', fontWeight: '700', letterSpacing: 3, color: theme.gray1, whiteSpace: 'pre-line' }}>
                              {trainfo?.statnNm?.eng}
                            </div>
                          )
                        ) : (
                          <div style={{ fontSize: '60px', fontWeight: '700', letterSpacing: 3, color: theme.gray1, whiteSpace: 'pre-line' }}>
                            {trainfo?.statnNm?.eng}
                          </div>
                        ),
                    }[lang],
                  }[nowtrain]
                }
              </div>
              {nowtrain &&
                {
                  kor:
                    trainfo.subwayHeading == 1 && (trainfo.trainSttus == 0 || trainfo.trainSttus == 1) ? (
                      trainfo?.statnNm?.kor.length > 5 ? (
                        <div style={{ position: 'absolute', fontSize: '40px', top: '1.1vw', opacity: '100%', right: -40, color: theme.gray1 }}>
                          <RiArrowRightSLine />
                        </div>
                      ) : (
                        <div style={{ position: 'absolute', fontSize: '40px', bottom: 11, opacity: '100%', right: -40, color: theme.gray1 }}>
                          <RiArrowRightSLine />
                        </div>
                      )
                    ) : trainfo?.statnNm?.kor.length > 5 ? (
                      <div style={{ position: 'absolute', fontSize: '40px', top: '1vw', opacity: '20%', right: -40, color: theme.gray1 }}>
                        <RiArrowRightSLine />
                      </div>
                    ) : (
                      <div style={{ position: 'absolute', fontSize: '40px', bottom: 11, opacity: '20%', right: -40, color: theme.gray1 }}>
                        <RiArrowRightSLine />
                      </div>
                    ),
                  eng:
                    trainfo.subwayHeading == 1 && (trainfo.trainSttus == 0 || trainfo.trainSttus == 1) ? (
                      trainfo?.statnNm?.eng.length > 5 ? (
                        <div style={{ position: 'absolute', fontSize: '40px', top: '1vw', opacity: '100%', right: -40, color: theme.gray1 }}>
                          <RiArrowRightSLine />
                        </div>
                      ) : (
                        <div style={{ position: 'absolute', fontSize: '40px', bottom: 13, opacity: '100%', right: -40, color: theme.gray1 }}>
                          <RiArrowRightSLine />
                        </div>
                      )
                    ) : trainfo?.statnNm?.eng.length > 5 ? (
                      <div style={{ position: 'absolute', fontSize: '40px', top: '3vw', opacity: '20%', right: -40, color: theme.gray1 }}>
                        <RiArrowRightSLine />
                      </div>
                    ) : (
                      <div style={{ position: 'absolute', fontSize: '40px', top: 14, opacity: '20%', right: -40, color: theme.gray1 }}>
                        <RiArrowRightSLine />
                      </div>
                    ),
                }[lang]}
              <div style={{ position: 'absolute', fontSize: '27px', fontWeight: '600', right: 0, color: theme.gray1, marginTop: '-3px' }}>
                {' '}
                {
                  {
                    0: {
                      eng: 'approach',
                      kor: '진입',
                    }[lang],
                    1: {
                      eng: 'arrival',
                      kor: '도착',
                    }[lang],
                    2: {
                      eng: 'leave',
                      kor: '출발',
                    }[lang],
                    3: {
                      eng: 'leave',
                      kor: '출발',
                    }[lang],
                  }[trainfo?.trainSttus]
                }
              </div>
            </div>
            <div style={{ height: '27px' }}></div>
            <div style={{ fontSize: '15px', fontWeight: '400', color: theme.gray4 }}>
              {
                {
                  kor: trainfo?.nextStatns?.stationNameList[0]?.kor,
                  eng: trainfo?.nextStatns?.stationNameList[0]?.eng,
                }[lang]
              }
            </div>
            <div style={{ fontSize: '15px', fontWeight: '400', color: theme.gray4 }}>
              {
                {
                  kor: trainfo?.nextStatns?.stationNameList[1]?.kor,
                  eng: trainfo?.nextStatns?.stationNameList[1]?.eng,
                }[lang]
              }
            </div>
            <div style={{ fontSize: '15px', fontWeight: '400', color: theme.gray4 }}>
              {
                {
                  kor: trainfo?.nextStatns?.stationNameList[2]?.kor,
                  eng: trainfo?.nextStatns?.stationNameList[2]?.eng,
                }[lang]
              }
            </div>
          </div>
          <div style={{display:'flex',marginBottom:'20px'}}>
            {
              {true:{kor:<div>
                <div style={{position: 'absolute',zIndex:9,left:23,borderRadius:10,width: '8px',height:'8px',marginTop: '26px',backgroundColor:'black' }}></div>
                <img src="train_head.png" style={{position: 'absolute',width: '30px',height:'15px',marginTop:'10px', left:5+(Math.abs(trainfo.curPosition)*80)+'%',marginBottom: '50px' }}/>
                <div style={{position: 'absolute',zIndex:9,left:329,borderRadius:10,width: '8px',height:'8px',marginTop: '26px',backgroundColor:'black' }}></div>
              <div style={{position: 'absolute',left:30,width: '300px',height:'5px',marginTop:'10px',marginTop: '28px',backgroundColor:linecolor }}></div>
              <div style={{position: 'absolute',fontWeight:700,left:-110+(trainfo?.statnNm?.kor.length*2),width: '300px',height:'5px',marginTop: '32px' }}>{trainfo?.statnNm?.kor}</div>
              <div style={{position: 'absolute',fontWeight:700,left:170-(trainfo?.nextStatns?.stationNameList[0]?.kor.length*2),width: '300px',height:'5px',marginTop: '32px' }}>{trainfo?.nextStatns?.stationNameList[0]?.kor}</div>
              </div>,
              eng:<div>
              <div style={{position: 'absolute',zIndex:9,left:23,borderRadius:10,width: '8px',height:'8px',marginTop: '26px',backgroundColor:'black' }}></div>
              <img src="train_head.png" style={{position: 'absolute',width: '30px',height:'15px',marginTop:'10px', left:5+(Math.abs(trainfo.curPosition)*80)+'%',marginBottom: '50px' }}/>
              <div style={{position: 'absolute',zIndex:9,left:329,borderRadius:10,width: '8px',height:'8px',marginTop: '26px',backgroundColor:'black' }}></div>
            <div style={{position: 'absolute',left:30,width: '300px',height:'5px',marginTop:'10px',marginTop: '28px',backgroundColor:linecolor }}></div>
            <div style={{position: 'absolute',fontWeight:700,left:-110+(trainfo?.statnNm?.eng.length*2),width: '300px',height:'5px',marginTop: '32px' }}>{trainfo?.statnNm?.eng}</div>
            <div style={{position: 'absolute',fontWeight:700,left:150-(trainfo?.nextStatns?.stationNameList[0]?.eng.length*2),width: '300px',height:'5px',marginTop: '32px' }}>{trainfo?.nextStatns?.stationNameList[0]?.eng}</div>
            </div>
            
            }[lang]
              ,
            false:''}[nowtrain]
            }
            
          </div>
          

          <Snackbar open={toastState} autoHideDuration={4000}>
            <Alert severity='info' sx={{ width: '70%', margin: '0 auto 10vh auto', fontSize: '17px' }}>
              {{ kor: trainfo?.statnNm?.kor + '역 접근', eng: trainfo?.statnNm?.eng + ' approach' }[lang]}
            </Alert>
          </Snackbar>

          {
            {
              true: (
                <ToastNotification
                  setToastState={setToastState}
                  lang={lang}
                  sound={sound}
                  train={{ kor: trainfo?.statnNm?.kor, eng: trainfo?.statnNm?.eng }[lang]}
                ></ToastNotification>
              ),
              false: console.log(toastState),
            }[toastState]
          }
        </div>
        <div style={{ height: '40px' }} />
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
              ...(!nowtrain && { pointerEvents: 'none', opacity: '0.25' }),
            }}
            onClick={() => {
              setReport(true);
            }}
          >
            <RiAlarmWarningFill style={{ color: 'FFB833', fontSize: '20px' }} />

            {
              {
                kor: '신고',
                eng: 'Report',
              }[lang]
            }
            <br />
            {
              {
                kor: '문의',
                eng: 'inquiry',
              }[lang]
            }
          </div>
          <div
            onClick={shareKakao}
            style={{
              width: '90px',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: 5,
              borderRight: 'solid white 1px',
              padding: '10px',
              ...(!nowtrain && { pointerEvents: 'none', opacity: '0.25' }),
            }}
          >
            <RiKakaoTalkFill style={{ color: 'FFE600', fontSize: '20px' }} />
            {
              {
                kor: '현위치',
                eng: 'Location',
              }[lang]
            }
            <br />
            {
              {
                kor: '공유',
                eng: 'sharing',
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
              padding: '10px',
              ...((trainfo?.directAt == 1 || !nowtrain) && { pointerEvents: 'none', opacity: '0.25' }),
            }}
            onClick={() => {
              Nextreceive();
              setModalOpen(true);
            }}
          >
            <RiTimeFill style={{ color: '92FF6B', fontSize: '20px' }} />
            {
              {
                kor: '도착',
                eng: 'ETA',
              }[lang]
            }
            <br />
            {
              {
                kor: '예정 시각',
                eng: ' ',
              }[lang]
            }
          </div>
        </div>

        <div style={{ height: '80px' }} />
        <div
          style={{
            width: '95%',
            fontSize: '10px',
            color: theme.gray6,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-end',
          }}
        >
          <a style={{ color: theme.gray6, textDecoration: 'underline' }} href='/personalInfo.html' target='_blank'>
            {
              {
                kor: '개인정보처리방침',
                eng: 'Privacy Policy',
              }[lang]
            }
          </a>
          <div style={{ letterSpacing: '-0.6px' }}>Copyright © SUBWHERE. All right reserved.</div>
        </div>

        <Modal open={modalOpen} onClose={() => setModalOpen(false)}>
          <Box
            className='wrapModal'
            style={{
              color: theme.gray1,
              backgroundColor: theme.bg1,
              bordercolor: theme.gray1,
              padding: '20px',
              boxSizing: 'border-box',
              overflow: 'scroll',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: 15,
              maxHeight: '70vh',
            }}
          >
            <Typography sx={{ fontWeight: '700', fontSize: '22px', letterSpacing: '-0.7px' }}>
              {
                {
                  kor: '도착 예정 시각',
                  eng: 'Estimated Arrival Time',
                }[lang]
              }
            </Typography>

            <TableContainer sx={{ color: theme.gray1 }}>
              <Table size='small'>
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ ...tableStyle, fontWeight: '200', color: theme.gray1, borderBottom: `solid ${theme.gray4} 1px` }} align='right'>
                      {{ kor: '역', eng: 'Station' }[lang]}
                    </TableCell>
                    <TableCell sx={{ ...tableStyle, fontWeight: '200', color: theme.gray1, borderBottom: `solid ${theme.gray4} 1px` }}>
                      {{ kor: '시각', eng: 'Time' }[lang]}
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {nexinfo.map((value, i) => (
                    <TableRow key={i}>
                      <TableCell sx={{ ...tableStyle, letterSpacing: '-0.5px' }} align='right'>
                        {{ kor: value.statnNm.kor, eng: value.statnNm.eng }[lang]}
                      </TableCell>
                      <TableCell sx={{ ...tableStyle, fontWeight: '600' }}>{addTime(value.arrivalTime)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        </Modal>

        <Modal open={report} onClose={() => setReport(false)}>
          <Box sx={style}>
            <Typography id='modal-modal-title' variant='h6' component='h2'>
              {
                {
                  kor: '민원 신고',
                  eng: 'report',
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
                borderRadius: '20px 20px 0 0',
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
                <a
                  href='javascript:void(0)'
                  onClick={() => sendSMS((smstext = '객차 내 온도 상승 요청'))}
                  class='sms'
                  style={{ position: 'relative', color: theme.gray1 }}
                >
                  <AcUnitIcon style={{ color: '#00F5FF	', fontSize: '20px' }} />
                </a>
                {
                  {
                    kor: '추워요',
                    eng: "it's cold",
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
                <a
                  href='javascript:void(0)'
                  onClick={() => sendSMS((smstext = '객차 내 온도 하강 요청'))}
                  class='sms'
                  style={{ position: 'relative', color: theme.gray1 }}
                >
                  <AirIcon style={{ color: '	#1E82FF', fontSize: '20px' }} />
                </a>
                {
                  {
                    kor: '더워요',
                    eng: "it's hot",
                  }[lang]
                }
              </div>
              <div style={{ width: '90px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 5, padding: '10px' }}>
                <a
                  href='javascript:void(0)'
                  onClick={() => sendSMS((smstext = '객차 내부 오염됨'))}
                  class='sms'
                  style={{ position: 'relative', color: theme.gray1 }}
                >
                  <CleaningServicesIcon style={{ color: '92FF6B', fontSize: '20px' }} />
                </a>
                {
                  {
                    kor: '칸이더러워요',
                    eng: 'train is dirty',
                  }[lang]
                }
              </div>
            </div>
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
                borderRadius: '0 0 20px 20px',
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
                <a
                  href='javascript:void(0)'
                  onClick={() => sendSMS((smstext = '객차 내 마스크 미착용 인원 신고'))}
                  class='sms'
                  style={{ position: 'relative', color: theme.gray1 }}
                >
                  <MasksIcon style={{ color: '#000000	', fontSize: '20px' }} />
                </a>
                {
                  {
                    kor: '노 마스크',
                    eng: 'No mask',
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
                <a
                  href='javascript:void(0)'
                  onClick={() => sendSMS((smstext = '객차 내 호객 행위 중'))}
                  class='sms'
                  style={{ position: 'relative', color: theme.gray1 }}
                >
                  <ProductionQuantityLimitsIcon style={{ color: '#000000', fontSize: '20px' }} />
                </a>
                {
                  {
                    kor: '잡상인 신고',
                    eng: 'hawker',
                  }[lang]
                }
              </div>
              <div style={{ width: '90px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 5, padding: '10px' }}>
                <a href='javascript:void(0)' onClick={() => sendSMS((smstext = ''))} class='sms' style={{ position: 'relative', color: theme.gray1 }}>
                  <ReportIcon style={{ color: '#EB0000', fontSize: '20px' }} />
                </a>
                {
                  {
                    kor: '기타 신고',
                    eng: 'Other report',
                  }[lang]
                }
              </div>
            </div>
          </Box>
        </Modal>
      </div>
    )
  );
}

export default SubwayPage;
