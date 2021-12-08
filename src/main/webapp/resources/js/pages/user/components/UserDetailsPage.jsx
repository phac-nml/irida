import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { Button, Form, Input, notification, Select, Space, Switch, Typography } from 'antd';
import moment from 'moment';

import { useGetUserDetailsQuery, useEditUserDetailsMutation } from "../../../apis/users/users";

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
    editUser({'userId': userId, ...values})
      .unwrap()
      .then((payload) => {
        notification.success({ message: i18n("UserDetailsPage.notification.success") });
      })
      .catch((error) => {
        notification.error({ message: i18n("UserDetailsPage.notification.error") });
        setFormErrors(error.data);
      });
  }

  if (isSuccess) {
    return (
      <Space direction="vertical">
        <Typography.Title level={4}>{data.user.username}</Typography.Title>
        <Form
          layout="vertical"
          initialValues={data.user}
          onFinish={onFormFinish}
        >
          <Form.Item label={i18n("UserDetailsPage.form.label.firstName")} name="firstName" help={formErrors?.firstName} validateStatus={ formErrors?.firstName ? "error" : undefined }>
            <Input />
          </Form.Item>
          <Form.Item label={i18n("UserDetailsPage.form.label.lastName")} name="lastName" help={formErrors?.lastName} validateStatus={ formErrors?.lastName ? "error" : undefined }>
            <Input />
          </Form.Item>
          <Form.Item label={i18n("UserDetailsPage.form.label.email")} name="email" help={formErrors?.email} validateStatus={ formErrors?.email ? "error" : undefined }>
            <Input />
          </Form.Item>
          <Form.Item label={i18n("UserDetailsPage.form.label.phoneNumber")} name="phoneNumber" help={formErrors?.phoneNumber} validateStatus={ formErrors?.phoneNumber ? "error" : undefined }>
            <Input />
          </Form.Item>
          <Form.Item label={i18n("UserDetailsPage.form.label.locale")} name="locale">
            <Select>
              { Object.keys(data.locales).map((key, index) =>
                <Select.Option key={`user-account-details-locale-${index}`} value={key}>{data.locales[key]}</Select.Option>
              )}
            </Select>
          </Form.Item>
          <Form.Item label={i18n("UserDetailsPage.form.label.role")} name="role" help={formErrors?.role} validateStatus={ formErrors?.role ? "error" : undefined } hidden={data.user.role !== "ROLE_ADMIN"}>
            <Select>
              { Object.keys(data.allowedRoles).map((key, index) =>
                <Select.Option key={`user-account-details-role-${index}`} value={key}>{data.allowedRoles[key]}</Select.Option>
              )}
            </Select>
          </Form.Item>
          <Form.Item label={i18n("UserDetailsPage.form.label.enabled")} name="enabled" valuePropName="checked" hidden={data.user.role !== "ROLE_ADMIN"}>
            <Switch />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">{i18n("UserDetailsPage.form.button.submit")}</Button>
          </Form.Item>
        </Form>
        <Typography.Text type="secondary">{i18n("UserDetailsPage.createdDate", moment(data.user.createdDate).format())}</Typography.Text>
        <Typography.Text type="secondary">{i18n("UserDetailsPage.modifiedDate", moment(data.user.modifiedDate).format())}</Typography.Text>
        <Typography.Text type="secondary">{i18n("UserDetailsPage.lastLogin", moment(data.user.lastLogin).format())}</Typography.Text>
      </Space>
    );
  } else {
    return null
  }
}