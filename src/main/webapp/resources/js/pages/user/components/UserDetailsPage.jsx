import React, { useState } from "react";
import { useParams } from "react-router-dom";
import {
  Button,
  Form,
  Input,
  notification,
  Select,
  Space,
  Switch,
  Typography,
} from "antd";
import moment from "moment";

import {
  useGetUserDetailsQuery,
  useEditUserDetailsMutation,
} from "../../../apis/users/users";

/**
 * React component to display the user details page.
 * @returns {*}
 * @constructor
 */
export default function UserDetailsPage() {
  const { userId } = useParams();
  const { data, isSuccess } = useGetUserDetailsQuery(userId);
  const [editUser] = useEditUserDetailsMutation();

  const [formErrors, setFormErrors] = useState();

  const onFormFinish = (values) => {
    editUser({ userId: userId, ...values })
      .unwrap()
      .then((payload) => {
        notification.success({
          message: i18n("UserDetailsPage.notification.success"),
        });
      })
      .catch((error) => {
        notification.error({
          message: i18n("UserDetailsPage.notification.error"),
        });
        setFormErrors(error.data);
      });
  };

console.log(data);

  if (isSuccess) {
    return (
      <Space direction="vertical">
        <Typography.Title level={4}>{data.user.username}</Typography.Title>
        <Form
          layout="vertical"
          initialValues={data.user}
          onFinish={onFormFinish}
        >
          <Form.Item
            label={i18n("UserDetailsPage.form.label.firstName")}
            name="firstName"
            help={formErrors?.find(error => error.field === "firstName")?.message}
            validateStatus={formErrors?.find(error => error.field === "firstName") ? "error" : undefined}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label={i18n("UserDetailsPage.form.label.lastName")}
            name="lastName"
            help={formErrors?.find(error => error.field === "lastName")?.message}
            validateStatus={formErrors?.find(error => error.field === "lastName") ? "error" : undefined}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label={i18n("UserDetailsPage.form.label.email")}
            name="email"
            help={formErrors?.find(error => error.field === "email")?.message}
            validateStatus={formErrors?.find(error => error.field === "email") ? "error" : undefined}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label={i18n("UserDetailsPage.form.label.phoneNumber")}
            name="phoneNumber"
            help={formErrors?.find(error => error.field === "phoneNumber")?.message}
            validateStatus={formErrors?.find(error => error.field === "phoneNumber") ? "error" : undefined}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label={i18n("UserDetailsPage.form.label.locale")}
            name="locale"
          >
            <Select>
              {data.locales.map((locale, index) => (
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
            label={i18n("UserDetailsPage.form.label.role")}
            name="role"
            help={formErrors?.find(error => error.field === "role")?.message}
            validateStatus={formErrors?.find(error => error.field === "role") ? "error" : undefined}
            hidden={!data.admin}
          >
            <Select>
              {data.allowedRoles.map((role, index) => (
                <Select.Option
                  key={`user-account-details-role-${index}`}
                  value={role.code}
                >
                  {role.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label={i18n("UserDetailsPage.form.label.enabled")}
            name="enabled"
            valuePropName="checked"
            hidden={!data.admin}
          >
            <Switch />
          </Form.Item>
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              disabled={!data.canEditUser}
            >
              {i18n("UserDetailsPage.form.button.submit")}
            </Button>
          </Form.Item>
        </Form>
        <Typography.Text type="secondary">
          {i18n(
            "UserDetailsPage.createdDate",
            moment(data.user.createdDate).format()
          )}
        </Typography.Text>
        <Typography.Text type="secondary">
          {i18n(
            "UserDetailsPage.modifiedDate",
            moment(data.user.modifiedDate).format()
          )}
        </Typography.Text>
        <Typography.Text type="secondary">
          {i18n(
            "UserDetailsPage.lastLogin",
            moment(data.user.lastLogin).format()
          )}
        </Typography.Text>
      </Space>
    );
  } else {
    return null;
  }
}
