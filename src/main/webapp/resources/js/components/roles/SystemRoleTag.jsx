import { Tag } from "antd";
import React from "react";

/**
 * React component to render a tag and translation for the user system role
 * @param {string} role - user's system role
 * @returns {JSX.Element}
 * @constructor
 */
export default function SystemRoleTag({ role }) {
  switch (role) {
    case "ROLE_USER":
      return <Tag color="geekblue">{i18n("systemRole.ROLE_USER")}</Tag>;
    case "ROLE_MANAGER":
      return <Tag color="orange">{i18n("systemRole.ROLE_MANAGER")}</Tag>;
    case "ROLE_SEQUENCER":
      return <Tag color="gold">{i18n("systemRole.ROLE_SEQUENCER")}</Tag>;
    case "ROLE_ADMIN":
      return <Tag color="magenta">{i18n("systemRole.ROLE_ADMIN")}</Tag>;
    case "ROLE_TECHNICIAN":
      return <Tag color="cyan">{i18n("systemRole.ROLE_TECHNICIAN")}</Tag>;
  }
}
