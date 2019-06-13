import React, { useEffect } from "react";
import { Button, message, notification } from "antd";
import PropTypes from "prop-types";

export const MESSAGE_EVENT = "EVENT/MESSAGE";
export const NOTIFICATION_EVENT = "EVENT/NOTIFICATION";

export function Notifications() {
  useEffect(() => {
    window.addEventListener(MESSAGE_EVENT, showMessage);
    window.addEventListener(NOTIFICATION_EVENT, showUndoNotification);

    return () => {
      window.removeEventListener(MESSAGE_EVENT, showMessage);
      window.removeEventListener(NOTIFICATION_EVENT, showUndoNotification);
    };
  });

  function showMessage({ detail }) {
    const { text, type } = detail;
    switch (type) {
      case "info":
        message.warning(text);
        break;
      case "error":
        message.error(text);
        break;
      default:
        message.success(text);
    }
  }

  function showUndoNotification({ detail }) {
    const { text, callback } = detail;

    const clickHandler = () => {
      noti.close();
      callback();
    };

    const noti = notification.open({
      description: <React.Fragment>{text}</React.Fragment>,
      key: `open${Date.now()}`,
      btn: (
        <Button type="primary" size="small" onClick={clickHandler}>
          UNDO
        </Button>
      )
    });
  }

  return <div />;
}

Notifications.propTypes = {};
