import React, { useMemo } from "react";
import { MenuItem } from "../../types/ant-design";
import { Link, useMatches } from "react-router-dom";
import { Menu } from "antd";
import { renderMenuItem } from "../ant.design/menu-utilities";

/**
 * React component to render the project navigation
 * @constructor
 */
export default function ProjectNavigation() {
  const matches = useMatches();

  /**
   * Determine which path of the project pages the user is on to
   * select the appropriate menu item.
   */
  const path = useMemo<string>(() => {
    const match = matches.find((match) => match.id.startsWith(`project-`));
    return match?.id || "project-samples";
  }, [matches]);

  const menuItems: MenuItem[] = [
    {
      key: `project-samples`,
      label: <Link to={``}>{i18n("project.nav.samples")}</Link>,
    },
    {
      key: `project-linelist`,
      label: <Link to={`linelist`}>{i18n("project.nav.linelist")}</Link>,
    },
    {
      key: `project-analyses`,
      label: <Link to={`analyses`}>{i18n("project.nav.analysis")}</Link>,
    },
    {
      key: `project-exports`,
      label: <Link to={`exports`}>{i18n("project.nav.exports")}</Link>,
    },
    {
      key: `project-activity`,
      label: <Link to={`activity`}>{i18n("project.nav.activity")}</Link>,
    },
    {
      key: `project-settings`,
      label: <Link to={`settings`}>{i18n("project.nav.settings")}</Link>,
    },
  ];

  return (
    <Menu mode={"horizontal"} theme={"light"} selectedKeys={[path]}>
      {menuItems.map(renderMenuItem)}
    </Menu>
  );
}
