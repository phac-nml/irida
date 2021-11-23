import React from "react";
import { useParams } from "react-router-dom";
import { Badge, Button, DatePicker, Form, Input, InputNumber, Select, Switch } from 'antd';
import moment from 'moment';

import { useGetUserDetailsQuery } from "../../../apis/users/users";

const formDisabled = true;
const dateFormat = 'YYYY/MM/DD';

/**
 * React component to display the user details page.
 * @returns {*}
 * @constructor
 */
export default function UserDetailsPage() {
  const { userId } = useParams();
  const { data, isSuccess } = useGetUserDetailsQuery(userId);

  if (isSuccess) {
    console.log(data);
    return (
      <Form
        layout="vertical"
        initialValues={data.user}
      >
        <Form.Item label="ID" name="identifier">
          <InputNumber disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Username" name="username">
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Name" name="label">
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Email" name="email">
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Phone Number" name="phoneNumber">
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Role" name="systemRole">
          <Select disabled={formDisabled}>
            <Select.Option value="ROLE_USER">{i18n("systemrole.ROLE_USER")}</Select.Option>
            <Select.Option value="ROLE_MANAGER">{i18n("systemrole.ROLE_MANAGER")}</Select.Option>
            <Select.Option value="ROLE_SEQUENCER">{i18n("systemrole.ROLE_SEQUENCER")}</Select.Option>
            <Select.Option value="ROLE_ADMIN">{i18n("systemrole.ROLE_ADMIN")}</Select.Option>
            <Select.Option value="ROLE_TECHNICIAN">{i18n("systemrole.ROLE_TECHNICIAN")}</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item label="Enabled" name="enabled" valuePropName="checked">
          <Switch disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Created" name="createdDatePlaceholder" initialValue={moment(data.user.createdDate)}>
          <DatePicker format={dateFormat} disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Modified" name="modifiedDatePlaceholder" initialValue={moment(data.user.modifiedDate)}>
          <DatePicker format={dateFormat} disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Last Login" name="lastLoginPlaceholder" initialValue={moment(data.user.lastLogin)}>
          <DatePicker format={dateFormat} disabled={formDisabled} />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" disabled={formDisabled}>Submit</Button>
        </Form.Item>
      </Form>
    );
  } else {
    return null
  }
}