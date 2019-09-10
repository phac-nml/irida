import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import axios from "axios";
import { Button, Icon, Modal } from "antd";
import { red6 } from "../../styles/colors";
import { useInterval } from "../../hooks/useInterval";
import { SPACE_SM } from "../../styles/spacing";

const { i18n } = window.TL;

/**
 * Modal displayed when the user has had not server interaction within
 * a given time span
 * @param {number} displayTime - length of time to display the modal
 * @param {function} resetTimeout - function to poke the server and reset the timeout
 * @param {boolean} visibility - whether the modal should be displayed or not.
 * @returns {*}
 * @constructor
 */
export default function SessionModal({
  displayTime,
  resetTimeout,
  visibility
}) {
  const [remainingTime, setRemainingTime] = useState(displayTime / 1000);

  useEffect(() => {
    const modalTimeout = setTimeout(
      () => window.location.reload(),
      displayTime + 5000 // Adding 5 seconds to display the logged out message
    );

    return () => clearTimeout(modalTimeout);
  }, []);

  useInterval(() => {
    setRemainingTime(remainingTime - 1);
  }, 1000);

  const logout = () => (window.location = `${window.TL.BASE_URL}logout`);

  const keepSession = () =>
    axios.head(window.location.href).then(() => resetTimeout());

  const format = time => {
    let seconds = time % 60;
    let minutes = Math.floor(time / 60);
    minutes = minutes === 1 ? `1 min ` : minutes > 1 ? `${minutes} mins` : "";
    seconds = seconds === 1 ? `1 sec` : seconds > 1 ? `${seconds} secs` : "";
    return `${minutes}${seconds}`;
  };

  return remainingTime > 0 ? (
    <Modal
      visible={visibility}
      onOk={keepSession}
      onCancel={logout}
      title={i18n.session_modal_title}
      okText={i18n.session_modal_okText}
      cancelText={i18n.session_modal_cancelText}
      width={350}
      maskClosable={false}
      closable={false}
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          padding: `0 ${SPACE_SM}`
        }}
      >
        <span
          style={{
            display: "flex",
            alignItems: "center"
          }}
        >
          <Icon
            type="clock-circle"
            theme="twoTone"
            style={{ fontSize: 30 }}
            twoToneColor={red6}
          />
          <span style={{ paddingLeft: SPACE_SM }}>
            {i18n.session_modal_intro}
          </span>
        </span>
      </div>
      <section
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          fontSize: 40
        }}
      >
        {format(remainingTime)}
      </section>
      <section style={{ textAlign: "center" }}>
        {i18n.session_modal_description}
      </section>
    </Modal>
  ) : (
    <Modal
      visible={visibility}
      title={i18n.session_modal_title}
      width={350}
      closable={false}
      maskClosable={false}
      footer={
        <Button type="primary" onClick={logout}>
          OK
        </Button>
      }
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          padding: `0 ${SPACE_SM}`
        }}
      >
        <Icon
          type="clock-circle"
          theme="twoTone"
          style={{ fontSize: 55 }}
          twoToneColor={red6}
        />
        <span style={{ paddingLeft: SPACE_SM }}>
          {i18n.session_timeout_modal_desc}
        </span>
      </div>
    </Modal>
  );
}

SessionModal.propTypes = {
  displayTime: PropTypes.number.isRequired,
  resetTimeout: PropTypes.func.isRequired,
  visibility: PropTypes.bool.isRequired
};
