import React from "react";
import { useParams } from "react-router-dom";
import { Badge, Button, DatePicker, Form, Input, InputNumber, Select, Switch } from 'antd';
import moment from 'moment';

import { useGetUserDetailsQuery, useGetCurrentUserDetailsQuery } from "../../../apis/users/users";

const formDisabled = true;
const dateFormat = 'YYYY/MM/DD';

/**
 * React component to display the user details page.
 * @returns {*}
 * @constructor
 */
export default function UserDetailsPage() {
  const { userId } = useParams();
  const { data, isSuccess } = userId ? useGetUserDetailsQuery(userId) : useGetCurrentUserDetailsQuery();

  if (isSuccess) {
    console.log(data);
    return (
      <Form
        layout="vertical"
//         initialValues={{
//           id:5,
//           username:"this is a test",
//           name:"Test User",
//           email:"test@nowhere.ca",
//           phone:"1234",
//           role:"user",
//           enabled:true,
//           created:moment("2021/09/27", dateFormat),
//           modified:moment("2021/09/27", dateFormat),
//           lastlogin:moment("2021/09/29", dateFormat)
//         }}
      >
        <Form.Item label="ID" name="id" initialValue={data.user.identifier}>
          <InputNumber disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Username" name="username" initialValue={data.user.username}>
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Name" name="name" initialValue={data.user.label}>
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Email" name="email" initialValue={data.user.email}>
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Phone Number" name="phone" initialValue={data.user.phoneNumber}>
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Role" name="role" initialValue={data.systemRole}>
          <Select disabled={formDisabled}>
            <Select.Option value="user">User</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item label="Enabled" name="enabled" valuePropName="checked" initialValue={data.user.enabled}>
          <Switch disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Created" name="created" initialValue={moment(data.user.createdDate)}>
          <DatePicker format={dateFormat} disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Modified" name="modified" initialValue={moment(data.user.modifiedDate)}>
          <DatePicker format={dateFormat} disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Last Login" name="lastlogin">
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