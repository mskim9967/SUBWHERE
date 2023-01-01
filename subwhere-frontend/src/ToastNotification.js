import React, { useState, useEffect } from 'react';
import './App.css';

function ToastNotification(props) {
  const msg = new SpeechSynthesisUtterance();

  const speechHandler = (msg) => {
    msg.text = { kor: props.train + '역 진입', eng: props.train + ' approach' }[props.lang];
    window.speechSynthesis.speak(msg);
  };
  useEffect(() => {
    if (props.sound == true) {
      speechHandler(msg);
    }
  }, []);

  useEffect(() => {
    let timer = setTimeout(() => {
      props.setToastState(false);
      console.log('change');
      if (props.sound == true) {
        speechHandler(msg);
      }
    }, 5000);

    return () => {
      clearTimeout(timer);
    };
  }, []);

  return <></>;
}

export { ToastNotification };
