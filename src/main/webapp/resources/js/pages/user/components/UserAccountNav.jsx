import React from "react";
import { Link } from "react-router-dom";
import { Menu } from 'antd';

/**
 * React component to display the user account navigation.
 * @returns {*}
 * @constructor
 */
export default function UserAccountNav() {
  return (
    <Menu mode="inline" defaultSelectedKeys={['details']}>
      <Menu.Item key="details"><Link to="details">Details</Link></Menu.Item>
      <Menu.Item key="groups"><Link to="groups">Groups</Link></Menu.Item>
      <Menu.Item key="projects"><Link to="projects">Projects</Link></Menu.Item>
      <Menu.ItemGroup key="security" title="Security">
        <Menu.Item key="password"><Link to="password">Password Reset</Link></Menu.Item>
      </Menu.ItemGroup>
    </Menu>
  );
}
