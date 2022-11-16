import React, { useState, useEffect } from "react";
import './App.css';

function ToastNotification(props) {
    
  const msg = new SpeechSynthesisUtterance()
  const speechHandler = (msg) => {
    msg.text = {kor:props.train+'역 접근',eng:props.train+' approach' }[props.lang]
    window.speechSynthesis.speak(msg)
  }
  useEffect(() => {
    speechHandler(msg)
}, []);
    
    useEffect(() => {
        let timer = setTimeout(() => {
            props.setToastState(false);
            console.log('change');		
            speechHandler(msg)
    
        }, 5000);

        return () => { clearTimeout(timer) }
    }, []);


    return (
        <div className="toast-alert">
            <p>{{kor:props.train+'역 접근',eng:props.train+' approach' }[props.lang]}</p>
            
        </div>
    );
}

export { ToastNotification }