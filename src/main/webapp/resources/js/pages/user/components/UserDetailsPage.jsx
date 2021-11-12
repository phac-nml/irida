import React from "react";
import { Badge, Button, DatePicker, Descriptions, Form, Input, InputNumber, Select, Space, Switch } from 'antd';
import moment from 'moment';

const formDisabled = true;
const dateFormat = 'YYYY/MM/DD';

/**
 * React component to display the user details page.
 * @returns {*}
 * @constructor
 */
export default function UserDetailsPage({}) {
  return (
    <Space direction="vertical">
      <Descriptions bordered title="User Details" extra={<Button type="primary">Edit</Button>}>
        <Descriptions.Item label="ID">5</Descriptions.Item>
        <Descriptions.Item label="Username">test</Descriptions.Item>
        <Descriptions.Item label="Name">Test User</Descriptions.Item>
        <Descriptions.Item label="Email">test@nowhere.ca</Descriptions.Item>
        <Descriptions.Item label="Phone Number">1234</Descriptions.Item>
        <Descriptions.Item label="Role">User</Descriptions.Item>
        <Descriptions.Item label="Enabled"><Badge status="success" text="yes" /></Descriptions.Item>
        <Descriptions.Item label="Created">27 Oct 2021</Descriptions.Item>
        <Descriptions.Item label="Modified">27 Oct 2021</Descriptions.Item>
        <Descriptions.Item label="Last Login">29 Oct 2021</Descriptions.Item>
      </Descriptions>
      <Form
        layout="vertical"
        initialValues={{
          id:5,
          username:"this is a test",
          name:"Test User",
          email:"test@nowhere.ca",
          phone:"1234",
          role:"user",
          enabled:true,
          created:moment("2021/09/27", dateFormat),
          modified:moment("2021/09/27", dateFormat),
          lastlogin:moment("2021/09/29", dateFormat)
        }}
      >
        <Form.Item label="ID" name="id">
          <InputNumber disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Username" name="username">
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Name" name="name">
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Email" name="email">
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Phone Number" name="phone">
          <Input disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Role" name="role">
          <Select disabled={formDisabled}>
            <Select.Option value="user">User</Select.Option>
          </Select>
        </Form.Item>
        <Form.Item label="Enabled" name="enabled" valuePropName="checked">
          <Switch disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Created" name="created">
          <DatePicker format={dateFormat} disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Modified" name="modified">
          <DatePicker format={dateFormat} disabled={formDisabled} />
        </Form.Item>
        <Form.Item label="Last Login" name="lastlogin">
          <DatePicker format={dateFormat} disabled={formDisabled} />
        </Form.Item>
        <Form.Item>
           <Button type="primary" htmlType="submit" disabled={formDisabled}> Submit</Button>
        </Form.Item>
      </Form>
    </Space>
  );
}