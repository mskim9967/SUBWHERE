import React, { useState, useEffect } from "react";
import './App.css';

function ToastNotification(props) {
    
    useEffect(() => {
        let timer = setTimeout(() => {
            props.setToastState(false);		
        }, 10000);

        return () => { clearTimeout(timer) }
    }, []);

    return (
        <div className="toast-alert">
            <img alt="" src="img/alert.png" />
            <p>{}동작역에 접근 중 입니다</p>
        </div>
    );
}

export { ToastNotification }