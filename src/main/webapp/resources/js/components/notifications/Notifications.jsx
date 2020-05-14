import React, { useEffect } from "react";
import { Button, message, notification } from "antd";

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
    const { text, description, callback } = detail;
    const key = `open${Date.now()}`;

    const clickHandler = () => {
      notification.close(key);
      callback();
    };

    notification.open({
      message: text,
      description,
      key,
      btn: (
        <Button
          className="t-undo-btn"
          type="primary"
          size="small"
          onClick={clickHandler}
        >
          UNDO
        </Button>
      )
    });
  }

  return <div />;
}

Notifications.propTypes = {};
