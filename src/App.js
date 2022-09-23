import logo from './logo.svg';
import { RiArrowRightSLine, RiArrowLeftSLine, RiAlarmWarningFill, RiKakaoTalkFill, RiTimeFill, RiMenuFill } from 'react-icons/ri';
import './App.css';
import { useState } from 'react';

function App() {
  const [menuActive, setMenuActive] = useState(false);

  return (
    <div
      className='App'
      style={{ width: '100vw', padding: '60px 0px', backgroundColor: '#ffffff', display: 'flex', flexDirection: 'column', alignItems: 'center' }}
    >
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <div style={{ fontSize: '15px', fontWeight: '400', color: '#888888' }}>22년 9월 21일(목) 17:35:14 기준</div>

        <div style={{ height: '10px' }} />

        <div style={{ fontSize: '20px', fontWeight: '500', color: '#555555' }}>현재 탑승 중이신 열차는</div>

        <div style={{ height: '30px' }} />

        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{ color: '#ffffff', backgroundColor: '#B3A78A', padding: '5px 10px', borderRadius: '20px', fontSize: '15px', fontWeight: 600 }}>
            9호선 - 중앙보훈병원 행
          </div>

          <div style={{ color: '#ffffff', backgroundColor: '#ff4444', padding: '5px 10px', borderRadius: '20px', fontSize: '15px', fontWeight: 600 }}>
            급행열차
          </div>
          <div style={{ color: '#ffffff', backgroundColor: '#777777', padding: '5px 10px', borderRadius: '20px', fontSize: '15px', fontWeight: 600 }}>
            5518칸
          </div>
        </div>

        <div style={{ height: '40px' }} />

        <div style={{ display: 'flex', alignItems: 'start', flexDirection: 'column', gap: 7 }}>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>신논현역</div>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>고속터미널역</div>
          <div style={{ position: 'relative', margin: '-7px 0' }}>
            <div style={{ position: 'absolute', fontSize: '40px', bottom: 13, left: -60, opacity: '20%' }}>
              <RiArrowLeftSLine />
            </div>

            <div style={{ fontSize: '70px', fontWeight: '700', letterSpacing: 3 }}>동작역</div>
            <div style={{ position: 'absolute', fontSize: '40px', bottom: 13, right: -60 }}>
              <RiArrowRightSLine />
            </div>
            <div style={{ position: 'absolute', fontSize: '27px', fontWeight: '600', right: 0 }}>접근 중</div>
          </div>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>노량진역</div>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>여의도역</div>
          <div style={{ fontSize: '15px', fontWeight: '400', color: '#aaaaaa' }}>당산역</div>
        </div>
      </div>

      <div style={{ height: '80px' }} />

      <div style={{ display: 'flex', alignItems: 'center', gap: 3, zIndex: 10 }}>
        <img src='train_body.png' style={{ width: '36px', opacity: '25%' }} />
        <img src='train_body.png' style={{ width: '36px', opacity: '25%' }} />
        <img src='train_body.png' style={{ width: '36px', opacity: '100%' }} />
        <img src='train_body.png' style={{ width: '36px', opacity: '25%' }} />
        <img src='train_body.png' style={{ width: '36px', opacity: '25%' }} />
        <img src='train_body.png' style={{ width: '36px', opacity: '25%' }} />
        <img src='train_body.png' style={{ width: '36px', opacity: '25%' }} />
        <img src='train_head.png' style={{ width: '36px', opacity: '25%' }} />
      </div>
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          height: '60px',
          fontSize: '13px',
          fontWeight: 700,
          color: '#444444',
          backgroundColor: '#f8f8f8',
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
          <RiAlarmWarningFill style={{ color: 'FFB833', fontSize: '20px' }} />
          신고
          <br />
          문의
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
          <RiKakaoTalkFill style={{ color: 'FFE600', fontSize: '20px' }} />
          현위치
          <br />
          공유
        </div>
        <div style={{ width: '90px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 5, padding: '10px' }}>
          <RiTimeFill style={{ color: '92FF6B', fontSize: '20px' }} />
          도착
          <br />
          예정시각
        </div>
      </div>

      <div
        style={{
          position: 'fixed',
          bottom: 30,
          right: 30,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 999,
          width: menuActive ? '80vw' : '50px',
          height: '50px',
          borderRadius: '50px',
          boxShadow: '0px 0px 20px -5px #888888',
          transition: 'width ease 0.4s',
        }}
        onClick={() => setMenuActive(!menuActive)}
      >
        {menuActive ? (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#000000', gap: 20, overflow: 'hidden' }}>
            <div>지하철 정보</div>
            <div>역 정보</div>
            <div>분실물 조회</div>
          </div>
        ) : (
          <RiMenuFill style={{ color: '#000000', fontSize: '20px' }} />
        )}
      </div>
    </div>
  );
}

export default App;
