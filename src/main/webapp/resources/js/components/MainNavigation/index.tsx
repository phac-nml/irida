import React from "react";
import { Menu } from "antd";
import type { MenuProps } from "antd/es";
import {
  CONTEXT_PATH,
  ROUTE_HOME,
  ROUTE_PROJECTS_ALL,
  ROUTE_PROJECTS_PERSONAL,
  ROUTE_PROJECTS_SYNC,
} from "../../data/routes";
import { setBaseUrl } from "../../utilities/url-utilities";
import { SPACE_LG } from "../../styles/spacing";
import { theme } from "../../utilities/theme-utilities";
import "./index.css";

type MenuItem = {
  key: string;
  label?: string | JSX.Element;
  type?: "divider";
  children?: MenuItem[];
};

const menuItems: MenuItem[] = [
  {
    key: `nav-projects`,
    label: `PROJECTS`,
    children: [
      {
        key: `nav-projects-personal`,
        label: (
          <a href={ROUTE_PROJECTS_PERSONAL}>{i18n("nav.main.project-list")}</a>
        ),
      },
      ...[
        {
          key: `nav-projects-all`,
          label: (
            <a href={ROUTE_PROJECTS_ALL}>{i18n("nav.main.project-list-all")}</a>
          ),
        },
      ],
      { type: `divider`, key: `nav-div-1` },
      {
        key: `nav-projects-sync`,
        label: (
          <a href={ROUTE_PROJECTS_SYNC}>{i18n("nav.main.project-sync")}</a>
        ),
      },
    ],
  },
];

function renderMenuItem(item: MenuItem): JSX.Element {
  if (item.type === `divider`) {
    return <Menu.Divider key={item.key} />;
  } else if (!item.children) {
    return <Menu.Item key={item.key}>{item.label}</Menu.Item>;
  } else {
    return (
      <Menu.SubMenu key={item.key} title={item.label}>
        {item.children.map(renderMenuItem)}
      </Menu.SubMenu>
    );
  }
}

export default function MainNavigation(): JSX.Element {
  return (
    <>
      <a href={ROUTE_HOME}>
        <img
          className={"nav-logo"}
          src={`${CONTEXT_PATH}/resources/img/irida_logo_${theme}.svg`}
          alt={i18n("global.title")}
        />
      </a>
      <Menu theme={theme} mode={"horizontal"}>
        {menuItems.map(renderMenuItem)}
      </Menu>
    </>
  );
}
