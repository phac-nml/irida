import React from "react";
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

  const onFinish = (values) => {
    console.log('Success:', values);
    editUser({'userId': userId, ...values})
      .unwrap()
      .then(success => notification.success({ message: "User updated successfully." }))
      .catch(error => notification.error({ message: "An Error has occurred." }));
  };

  if (isSuccess) {
    console.log(data);
    return (
      <Space direction="vertical">
        <Typography.Title level={4}>{data.user.username}</Typography.Title>
        <Form
          layout="vertical"
          initialValues={data.user}
          onFinish={onFinish}
        >
          <Form.Item label="First Name" name="firstName">
            <Input disabled={formDisabled} />
          </Form.Item>
          <Form.Item label="Last Name" name="lastName">
            <Input disabled={formDisabled} />
          </Form.Item>
          <Form.Item label="Email" name="email">
            <Input disabled={formDisabled} />
          </Form.Item>
          <Form.Item label="Phone Number" name="phoneNumber">
            <Input disabled={formDisabled} />
          </Form.Item>
          <Form.Item label="Language" name="locale">
            <Select disabled={formDisabled}>
              { Object.keys(data.locales).map((key, index) =>
                <Select.Option key={`user-account-details-locale-${index}`} value={key}>{data.locales[key]}</Select.Option>
              )}
            </Select>
          </Form.Item>
          <Form.Item label="Role" name="role">
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