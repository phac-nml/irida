import React from "react";
import { notification, Popconfirm, Typography } from "antd";
import { LinkButton } from "../../../components/Buttons/LinkButton";
import { IconQuestionCircle } from "../../../components/icons/Icons";
import { red6 } from "../../../styles/colors";
import { useCreatePasswordResetMutation } from "../../../apis/passwordReset";

/**
 * React component to display the user account reset password link with confirmation.
 * @param {string} firstName the first name of the user
 * @param {string} lastName the last name of the user
 * @returns {JSX.Element}
 * @constructor
 */
export function UserResetPasswordLink({ userId, firstName, lastName }) {
  const [resetPassword] = useCreatePasswordResetMutation();

  const handleResetPassword = () => {
    resetPassword({ userId })
      .unwrap()
      .then(({ message }) => {
        notification.success({ message });
      })
      .catch((payload) => {
        notification.error({
          message: payload.data.error,
        });
      });
  };

  return (
    <>
      <Typography.Title level={5}>
        {i18n("UserResetPasswordLink.title")}
      </Typography.Title>
      <Popconfirm
        placement={"topLeft"}
        title={i18n("UserResetPasswordLink.confirm.title", firstName, lastName)}
        onConfirm={handleResetPassword}
        icon={<IconQuestionCircle style={{ color: red6 }} />}
      >
        <LinkButton text={i18n("UserResetPasswordLink.button.text")} />
      </Popconfirm>
    </>
  );
}
