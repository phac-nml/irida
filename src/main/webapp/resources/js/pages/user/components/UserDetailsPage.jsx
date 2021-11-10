import React from "react";
import { Badge, Button, Descriptions } from 'antd';

/**
 * React component to display the user details page.
 * @returns {*}
 * @constructor
 */
export default function UserDetailsPage({}) {
  return (
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
  );
}