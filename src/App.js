import { RiMenuFill } from 'react-icons/ri';
import { MdDarkMode, MdLightMode } from 'react-icons/md';
import IconButton from '@mui/material/IconButton';
import Button from '@mui/material/Button';
import './App.css';
import { useEffect, useState } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import SubwayPage from './page/SubwayPage';
import { darkTheme, lightTheme } from './static/color';
function App() {
  const [theme, setTheme] = useState(lightTheme);
  const [isDark, setDark] = useState(false);
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
      <div style={{ width: '100%', height: '130px', position: 'relative' }}>
        <div
          style={{
            position: 'absolute',
            right: 30,
            bottom: 40,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            height: '50px',
          }}
        >
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
        </div>
      </div>
      <Routes>
        <Route exact path='/subway' element={<SubwayPage lang={lang} theme={theme} />}></Route>
      </Routes>
    </div>
  );
}

export default App;
