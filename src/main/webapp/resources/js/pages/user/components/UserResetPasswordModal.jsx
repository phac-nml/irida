import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { Modal, notification } from "antd";
import { LinkButton } from "../../../components/Buttons/LinkButton";
import { useCreatePasswordResetMutation } from "../../../apis/passwordReset";

/**
 * React component to display the user account reset password modal.
 * @param {string} firstName the first name of the user
 * @param {string} lastName the last name of the user
 * @returns {JSX.Element}
 * @constructor
 */
export function UserResetPasswordModal({ firstName, lastName }) {
  const [showModal, setShowModal] = useState(false);
  const { userId } = useParams();
  const [resetPassword] = useCreatePasswordResetMutation(userId);

  const handleOk = () => {
    setShowModal(false);
    resetPassword({ userId: userId })
      .unwrap()
      .then((payload) => {
        notification.success({
          message: i18n("password.reset.success-message"),
        });
      })
      .catch((error) => {
        notification.error({
          message: i18n("password.reset.error-message"),
        });
      });
  };

  return (
    <>
      <Modal
        title="Reset Password"
        visible={showModal}
        onOk={handleOk}
        onCancel={() => setShowModal(false)}
      >
        <p>Reset password for {firstName + " " + lastName}</p>
      </Modal>
      <LinkButton text="Reset Password" onClick={() => setShowModal(true)} />
    </>
  );
}
