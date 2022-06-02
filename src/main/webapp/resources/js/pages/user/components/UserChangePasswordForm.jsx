import React from "react";
import { useNavigate } from "react-router-dom";
import {
  Alert,
  Button,
  Form,
  Input,
  List,
  notification,
  Typography,
} from "antd";
import { useChangeUserPasswordMutation } from "../../../apis/users/users";
import { SPACE_SM } from "../../../styles/spacing";
import { validatePassword } from "../../../utilities/validation-utilities";

/**
 * React component to display the user change password form.
 * @param {number} userId the identification number of the user
 * @returns {*}
 * @constructor
 */
export function UserChangePasswordForm({ userId }) {
  const [changeUserPassword] = useChangeUserPasswordMutation();
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const passwordRules = [
    i18n("validation-utilities.password.minimumLength"),
    i18n("validation-utilities.password.uppercase"),
    i18n("validation-utilities.password.lowercase"),
    i18n("validation-utilities.password.number"),
    i18n("validation-utilities.password.specialCharacters"),
  ];

  const onFormFinish = (values) => {
    changeUserPassword({ userId: userId, ...values })
      .unwrap()
      .then(() => {
        notification.success({
          message: i18n("UserChangePasswordForm.notification.success"),
        });
        form.resetFields();
        navigate(`/${userId}/details`);
      })
      .catch((error) => {
        notification.error({
          message: i18n("UserChangePasswordForm.notification.error"),
        });
        const fields = Object.entries(error.data.errors).map(
          ([field, error]) => ({
            name: field,
            errors: [error],
          })
        );
        form.setFields(fields);
      });
  };

  return (
    <>
      <Typography.Title level={5}>
        {i18n("UserChangePasswordForm.title")}
      </Typography.Title>
      <Alert
        style={{ marginBottom: SPACE_SM }}
        message={i18n("UserChangePasswordForm.alert.title")}
        description={
          <Typography.Paragraph>
            <List
              header={i18n("UserChangePasswordForm.alert.description")}
              dataSource={passwordRules}
              renderItem={(item) => <List.Item>{item}</List.Item>}
            />
          </Typography.Paragraph>
        }
        type="info"
        showIcon
      />
      <Form
        form={form}
        layout="vertical"
        onFinish={onFormFinish}
        autoComplete="off"
      >
        <Form.Item
          label={i18n("UserChangePasswordForm.form.label.oldPassword")}
          name="oldPassword"
          rules={[
            {
              required: true,
              message: i18n("validation-utilities.password.required"),
            },
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label={i18n("UserChangePasswordForm.form.label.newPassword")}
          name="newPassword"
          rules={[
            ({}) => ({
              validator(_, value) {
                return validatePassword(value);
              },
            }),
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item>
          <Button className="t-submit-btn" type="primary" htmlType="submit">
            {i18n("UserChangePasswordForm.form.button.submit")}
          </Button>
        </Form.Item>
      </Form>
    </>
  );
}
