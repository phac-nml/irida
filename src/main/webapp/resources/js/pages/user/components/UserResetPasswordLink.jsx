import React from "react";
import { Button, notification, Popconfirm, Typography } from "antd";
import { IconQuestionCircle } from "../../../components/icons/Icons";
import { red6 } from "../../../styles/colors";
import { useCreatePasswordResetMutation } from "../../../apis/passwordReset";

/**
 * React component to display the user account reset password link with confirmation.
 * @param {number} userId the identification number of the user
 * @param {string} firstName the first name of the user
 * @param {string} lastName the last name of the user
 * @returns {JSX.Element}
 * @constructor
 */
export function UserResetPasswordLink({userId, firstName, lastName}) {
  const [resetPassword] = useCreatePasswordResetMutation();

  const handleResetPassword = () => {
    resetPassword({userId})
      .unwrap()
      .then(({message}) => {
        notification.success({message});
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
      <Typography.Paragraph>
        {i18n("UserResetPasswordLink.paragraph")}
      </Typography.Paragraph>
      <Popconfirm
        placement={"topLeft"}
        title={i18n("UserResetPasswordLink.confirm.title", firstName, lastName)}
        onConfirm={handleResetPassword}
        icon={<IconQuestionCircle style={{color: red6}}/>}
      >
        <Button
          className="t-password-reset-link">{i18n("UserResetPasswordLink.button.text")}</Button>
      </Popconfirm>
    </>
  );
}
