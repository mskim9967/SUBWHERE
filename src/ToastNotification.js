import React, { useState, useEffect } from "react";
import './App.css';

function ToastNotification(props) {
    
    useEffect(() => {
        let timer = setTimeout(() => {
            props.setToastState(false);
            console.log('change');		
        }, 5000);

        return () => { clearTimeout(timer) }
    }, []);

    return (
        <div className="toast-alert">
            <p>{{kor:props.train+'역 접근',eng:props.train+'approach' }[props.lang]}</p>
        </div>
    );
}

export { ToastNotification }