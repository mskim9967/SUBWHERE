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
function App() {
  const [theme, setTheme] = useState(lightTheme);
  const [isDark, setDark] = useState(false);
  const [isSound, setSound] = useState(true);
  const [menuActive, setMenuActive] = useState(false);
  const [lang, setLang] = useState('kor');

  let navigate = useNavigate();

  useEffect(() => {
    console.log(localStorage.getItem('sdfds'));
    if (localStorage.getItem('isDark') !== null) setDark(localStorage.getItem('isDark') === 'true');
    if (localStorage.getItem('lang') !== null) setLang(localStorage.getItem('lang'));
  }, []);

  useEffect(() => {
    localStorage.setItem('isDark', isDark);
    localStorage.setItem('lang', lang);
  }, [isDark, lang]);

  useEffect(() => {
    setTheme(isDark ? darkTheme : lightTheme);
  }, [isDark]);

  return (
    <div className='App' style={{ width: '100vw', height: window.innerHeight, overflow: 'scroll', backgroundColor: theme.bg1 }}>
      <div
        style={{
          position: 'fixed',
          top: 40,
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
      <div style={{ width: '100%', height: '130px', position: 'relative' }}>
       
      </div>
      <Routes>
        <Route exact path='/subway' element={<SubwayPage lang={lang} theme={theme} sound={isSound} />}></Route>
      </Routes>
    </div>
  );
}

export default App;