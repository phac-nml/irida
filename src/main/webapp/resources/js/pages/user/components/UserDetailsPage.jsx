import React from "react";
import { useSelector, useDispatch } from "react-redux";
import { useParams } from "react-router-dom";
import {
  Button,
  Checkbox,
  Form,
  Input,
  notification,
  Select,
  Space,
  Typography,
} from "antd";
import { formatDate } from "../../../utilities/date-utilities";
import { useEditUserDetailsMutation } from "../../../apis/users/users";
import { updateUserDetails } from "../services/userReducer";

/**
 * React component to display the user details page.
 * @returns {*}
 * @constructor
 */
export default function UserDetailsPage() {
  const { userId } = useParams();
  const dispatch = useDispatch();
  const [editUser] = useEditUserDetailsMutation();
  const [form] = Form.useForm();
  const {
    user,
    admin,
    locales,
    allowedRoles,
    canEditUserInfo,
    canEditUserStatus,
  } = useSelector((state) => state.userReducer);

  const onFormFinish = (values) => {
    editUser({ userId: userId, ...values })
      .unwrap()
      .then((payload) => {
        dispatch(updateUserDetails({...user, ...values}));
        notification.success({
          message: i18n("UserDetailsPage.notification.success"),
        });
      })
      .catch((error) => {
        notification.error({
          message: i18n("UserDetailsPage.notification.error"),
        });
        const fields = Object.entries(error.data).map(([field, error]) => ({
          name: field,
          errors: [error],
        }));
        form.setFields(fields);
      });
  };

  return (
    <Space direction="vertical">
      <Typography.Title level={4}>
        {i18n("UserDetailsPage.title")}
      </Typography.Title>
      <Form
        form={form}
        layout="vertical"
        initialValues={user}
        onFinish={onFormFinish}
      >
        <Form.Item
          label={i18n("UserDetailsPage.form.firstName.label")}
          name="firstName"
          rules={[
            {
              required: true,
              message: i18n("UserDetailsPage.form.firstName.required"),
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label={i18n("UserDetailsPage.form.lastName.label")}
          name="lastName"
          rules={[
            {
              required: true,
              message: i18n("UserDetailsPage.form.lastName.required"),
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label={i18n("UserDetailsPage.form.email.label")}
          name="email"
          rules={[
            {
              required: true,
              message: i18n("UserDetailsPage.form.email.required"),
            },
            {
              type: "email",
              message: i18n("UserDetailsPage.form.email.type"),
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label={i18n("UserDetailsPage.form.phoneNumber.label")}
          name="phoneNumber"
          rules={[
            {
              required: true,
              message: i18n("UserDetailsPage.form.phoneNumber.required"),
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          label={i18n("UserDetailsPage.form.locale.label")}
          name="locale"
        >
          <Select>
            {locales.map((locale, index) => (
              <Select.Option
                key={`user-account-details-locale-${index}`}
                value={locale.language}
              >
                {locale.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item
          label={i18n("UserDetailsPage.form.role.label")}
          name="role"
          hidden={!admin}
        >
          <Select>
            {allowedRoles.map((role, index) => (
              <Select.Option
                key={`user-account-details-role-${index}`}
                value={role.code}
                disabled={!canEditUserStatus}
              >
                {role.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item name="enabled" valuePropName="checked" hidden={!admin}>
          <Checkbox disabled={!canEditUserStatus}>
            {i18n("UserDetailsPage.form.enabled.label")}
          </Checkbox>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" disabled={!canEditUserInfo}>
            {i18n("UserDetailsPage.form.button.submit")}
          </Button>
        </Form.Item>
      </Form>
      <Typography.Text type="secondary">
        {user.createdDate
          ? i18n(
              "UserDetailsPage.createdDate",
              formatDate({ date: user.createdDate })
            )
          : ""}
      </Typography.Text>
      <Typography.Text type="secondary">
        {user.modifiedDate
          ? i18n(
              "UserDetailsPage.modifiedDate",
              formatDate({ date: user.modifiedDate })
            )
          : ""}
      </Typography.Text>
      <Typography.Text type="secondary">
        {user.lastLogin
          ? i18n(
              "UserDetailsPage.lastLogin",
              formatDate({ date: user.lastLogin })
            )
          : ""}
      </Typography.Text>
    </Space>
  );
}
