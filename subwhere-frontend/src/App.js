import { RiMenuFill } from 'react-icons/ri';
import { MdDarkMode, MdLightMode } from 'react-icons/md';
import IconButton from '@mui/material/IconButton';
import Button from '@mui/material/Button';
import './App.css';
import { useEffect, useState } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import SubwayPage from './page/SubwayPage';
import { darkTheme, lightTheme } from './static/color';
import VolumeOffIcon from '@mui/icons-material/VolumeOff';
import VolumeUpIcon from '@mui/icons-material/VolumeUp';
import { enableAutoTTS } from 'enable-auto-tts';

function App() {
  const [theme, setTheme] = useState(lightTheme);
  const [isDark, setDark] = useState(false);
  const [isSound, setSound] = useState();
  const [menuActive, setMenuActive] = useState(false);
  const [lang, setLang] = useState('kor');

  let navigate = useNavigate();

  enableAutoTTS();

  useEffect(() => {
    if (localStorage.getItem('isDark') !== null) setDark(localStorage.getItem('isDark') === 'true');
    if (localStorage.getItem('lang') !== null) setLang(localStorage.getItem('lang'));
    if (localStorage.getItem('sound') !== null) setSound(localStorage.getItem('sound') === 'true');
  }, []);

  useEffect(() => {
    localStorage.setItem('isDark', isDark);
    localStorage.setItem('lang', lang);
    localStorage.setItem('sound', isSound);
  }, [isDark, lang, isSound]);

  useEffect(() => {
    setTheme(isDark ? darkTheme : lightTheme);
  }, [isDark]);

  return (
    <div
      className='App'
      style={{ width: '100vw', height: window.innerHeight, overflow: 'scroll', backgroundColor: theme.bg1 }}
      onClick={() => setMenuActive(false)}
    >
      <div
        style={{
          position: 'fixed',
          top: 40,
          right: 30,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 999,
          width: menuActive ? '250px' : '50px',
          height: '50px',
          borderRadius: '50px',
          boxShadow: `0px 0px 20px -5px ${theme.gray6}`,
          transition: 'width ease 0.4s',
        }}
        onClick={(e) => {
          e.stopPropagation();
          setMenuActive(true);
        }}
      >
        {menuActive ? (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', color: theme.text1, gap: 20, overflow: 'hidden' }}>
            <IconButton
              size='large'
              style={{ color: theme.text1 }}
              onClick={() => {
                setDark(!isDark);
              }}
            >
              {isDark ? <MdLightMode /> : <MdDarkMode />}
            </IconButton>

            <Button size='large' style={{ color: theme.text1 }} onClick={() => setLang(lang === 'kor' ? 'eng' : 'kor')}>
              {lang === 'kor' ? 'ENG' : 'KOR'}
            </Button>

            <IconButton
              size='large'
              style={{ color: theme.text1 }}
              onClick={() => {
                setSound(!isSound);
              }}
            >
              {isSound ? <VolumeOffIcon /> : <VolumeUpIcon />}
            </IconButton>
          </div>
        ) : (
          <RiMenuFill style={{ color: theme.text1, fontSize: '20px' }} />
        )}
      </div>
      <div style={{ width: '100%', height: '130px', position: 'relative' }}></div>
      <Routes>
        <Route exact path='/subway' element={<SubwayPage lang={lang} theme={theme} sound={isSound} />}></Route>
      </Routes>
    </div>
  );
}

export default App;
