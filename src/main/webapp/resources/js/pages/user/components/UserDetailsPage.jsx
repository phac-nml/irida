import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { Button, Form, Input, notification, Select, Space, Switch, Typography } from 'antd';
import moment from 'moment';

import { useGetUserDetailsQuery, useEditUserDetailsMutation } from "../../../apis/users/users";

const formDisabled = false;

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

  const onFormFinish = async(values) => {
    try {
      await editUser({'userId': userId, ...values});
      notification.success({ message: "User updated successfully." });
    } catch (editUserErrorResponse) {
      notification.error({ message: "An Error has occurred." });
      setFormErrors(editUserErrorResponse.data.errors);
    }
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
          <Form.Item label="First Name" name="firstName" help={formErrors?.firstName} validateStatus={ formErrors?.firstName ? "error" : undefined }>
            <Input disabled={formDisabled} />
          </Form.Item>
          <Form.Item label="Last Name" name="lastName" help={formErrors?.lastName} validateStatus={ formErrors?.lastName ? "error" : undefined }>
            <Input disabled={formDisabled} />
          </Form.Item>
          <Form.Item label="Email" name="email" help={formErrors?.email} validateStatus={ formErrors?.email ? "error" : undefined }>
            <Input disabled={formDisabled} />
          </Form.Item>
          <Form.Item label="Phone Number" name="phoneNumber" help={formErrors?.phoneNumber} validateStatus={ formErrors?.phoneNumber ? "error" : undefined }>
            <Input disabled={formDisabled} />
          </Form.Item>
          <Form.Item label="Language" name="locale">
            <Select disabled={formDisabled}>
              { Object.keys(data.locales).map((key, index) =>
                <Select.Option key={`user-account-details-locale-${index}`} value={key}>{data.locales[key]}</Select.Option>
              )}
            </Select>
          </Form.Item>
          <Form.Item label="Role" name="role" help={formErrors?.role} validateStatus={ formErrors?.role ? "error" : undefined }>
            <Select disabled={formDisabled}>
              { Object.keys(data.allowedRoles).map((key, index) =>
                <Select.Option key={`user-account-details-role-${index}`} value={key}>{data.allowedRoles[key]}</Select.Option>
              )}
            </Select>
          </Form.Item>
          <Form.Item label="Enabled" name="enabled" valuePropName="checked">
            <Switch disabled={formDisabled} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" disabled={formDisabled}>Update User</Button>
          </Form.Item>
        </Form>
        <Typography.Text type="secondary">Created on {moment(data.user.createdDate).format()}</Typography.Text>
        <Typography.Text type="secondary">Modified on {moment(data.user.modifiedDate).format()}</Typography.Text>
        <Typography.Text type="secondary">Last login on {moment(data.user.lastLogin).format()}</Typography.Text>
      </Space>
    );
  } else {
    return null
  }
}