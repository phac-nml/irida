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
  const [resetPassword] = useCreatePasswordResetMutation();

  const handleOk = () => {
    setShowModal(false);
    resetPassword({ userId })
      .unwrap()
      .then((payload) => {
        notification.success({
          message: payload,
        });
      })
      .catch((payload) => {
        notification.error({
          message: payload.data.error,
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
        <p>
          {i18n("UserResetPasswordModal.confirmation", firstName, lastName)}
        </p>
      </Modal>
      <LinkButton text="Reset Password" onClick={() => setShowModal(true)} />
    </>
  );
}
