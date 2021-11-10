import React from "react";
import { Link } from "@reach/router";
import { Menu } from 'antd';

/**
 * React component to display the user details navigation.
 * @returns {*}
 * @constructor
 */
export default function UserDetailsNav({}) {
  return (
    <Menu
      mode="inline"
    >
      <Menu.Item key="account"><Link to="account">Account</Link></Menu.Item>
      <Menu.Item key="groups"><Link to="groups">Groups</Link></Menu.Item>
      <Menu.Item key="projects"><Link to="projects">Projects</Link></Menu.Item>
      <Menu.ItemGroup key="security" title="Security">
        <Menu.Item key="password"><Link to="password">Password Reset</Link></Menu.Item>
      </Menu.ItemGroup>
    </Menu>
  );
}
