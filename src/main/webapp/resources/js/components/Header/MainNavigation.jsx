import { Button, Dropdown, Menu, Space, Row, Col } from "antd";
import React from "react";
import styled from "styled-components";
import { primaryColour, theme } from "../../utilities/theme-utilities";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconQuestionCircle, IconUser } from "../icons/Icons";

import { AnnouncementsSubMenu } from "./main-navigation/components/AnnouncementsSubMenu";
import { CartLink } from "./main-navigation/components/CartLink";
import { GlobalSearch } from "./main-navigation/components/GlobalSearch";
import "./main-navigation/style.css";

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";
const isManager = isAdmin || window.TL._USER.systemRole === "ROLE_MANAGER";

const textColor = theme === "dark" ? "#fff" : "#222";

const MenuStyle = styled.div`
  display: flex;
  justify-content: space-between;
  justify-self: end;
  align-items: center;
  flex-wrap: wrap;
  font-size: 2rem;
  border-bottom: 5px solid ${primaryColour};
  color: ${textColor};

  .ant-menu-item,
  .ant-menu-item-active,
  .ant-menu-item-selected,
  .ant-menu-item-only-child {
    background-color: #001529 !important;
  }

  .global-search {
    border-radius: 10px;
  }

  .ant-dropdown-trigger {
    color: ${textColor};
    font-size: 1.5rem;
  }
`;

const MenuItemStyle = styled(Menu.Item)`
  width: 50px !important;
  padding: 0 !important;
`;

const ProjectsMenu = (
  <Menu key="projects" theme={theme}>
    <Menu.Item key="project:list">
      <a href={setBaseUrl("/projects")}>{i18n("nav.main.project-list")}</a>
    </Menu.Item>
    {isAdmin && (
      <Menu.Item key="project:all">
        <a href={setBaseUrl("/projects/all")}>
          {i18n("nav.main.project-list-all")}
        </a>
      </Menu.Item>
    )}
    <Menu.Divider />
    <Menu.Item key="project:sync">
      <a href={setBaseUrl("/projects/synchronize")}>
        {i18n("nav.main.project-sync")}
      </a>
    </Menu.Item>
  </Menu>
);

const AnalysesMenu = (
  <Menu theme={theme}>
    <Menu.Item key="analyses:user">
      <a href={setBaseUrl(`/analysis`)}>
        {i18n("nav.main.analysis-admin-user")}
      </a>
    </Menu.Item>
    {isAdmin && (
      <Menu.Item key="analyses:all">
        <a href={setBaseUrl("/analysis/all")}>
          {i18n("nav.main.analysis-admin-all")}
        </a>
      </Menu.Item>
    )}
    <Menu.Divider />
    <Menu.Item key="analyses:output">
      <a href={setBaseUrl("/analysis/user/analysis-outputs")}>
        {i18n("Analysis.outputFiles")}
      </a>
    </Menu.Item>
  </Menu>
);

const UsersMenu = (
  <Menu theme={theme}>
    <Menu.Item key="user:users">
      <a href={setBaseUrl("/users")}>{i18n("nav.main.users-list")}</a>
    </Menu.Item>
    <Menu.Item key="user:groups">
      <a href={setBaseUrl("/groups")}>{i18n("nav.main.groups-list")}</a>
    </Menu.Item>
  </Menu>
);

const HelpMenu = (
  <Menu theme={theme}>
    <Menu.Item key="userguide">
      <a
           href="https://aries.iss.it/static/images/IRIDA21-ARIES.pdf"
           target="_blank"
           rel="noreferrer"
         >
           {i18n("nav.main.userguide")}
         </a>
       </Menu.Item>
    <Menu.Item key="icogenguide">
      <a
        href="https://aries.iss.it/static/images/IRIDA21-ICoGen.pdf"
        target="_blank"
        rel="noreferrer"
      >
        {i18n("nav.main.icogenguide")}
      </a>
    </Menu.Item>
    {isAdmin && (
      <Menu.Item key="adminguide">
        <a href="https://irida.corefacility.ca/documentation/user/administrator">
          {i18n("nav.main.adminguide")}
        </a>
      </Menu.Item>
    )}
    <Menu.Divider />
    <Menu.Item key="website">
      <a href="http://www.iss.it" target="_blank" rel="noopener noreferrer">
        {i18n("generic.irida.website")}
      </a>
    </Menu.Item>
    <Menu.Divider />
    <Menu.Item key="help:version" disabled>
      {i18n("irida.version")}
    </Menu.Item>
  </Menu>
);

const AccountMenu = (
  <Menu theme={theme}>
    <Menu.Item key="account">
      <a href={setBaseUrl(`/users/current`)}>{i18n("nav.main.account")}</a>
    </Menu.Item>
    <Menu.Item key="logout">
      <a href={setBaseUrl("/logout")}>{i18n("nav.main.logout")}</a>
    </Menu.Item>
  </Menu>
);

export function MainNavigation() {
  return (
    <Row justify="center">
      <Col xxl={24} xl={24} lg={24} md={24} sm={24} xs={24}>
        <Menu theme={theme} mode="inline">
          <MenuStyle>
            <Space align="center">
              <Button type="link" href={setBaseUrl("/")}>
                <img
                  style={{ height: 28, width: 129 }}
                  src={setBaseUrl(`/resources/img/irida_logo_${theme}.svg`)}
                  alt={i18n("global.title")}
                />
              </Button>
              <Menu.Item key="projects">
                <Dropdown overlay={ProjectsMenu}>
                  <Button type="link">{i18n("nav.main.project")}</Button>
                </Dropdown>
              </Menu.Item>
              <Menu.Item key="analyses">
                <Dropdown overlay={AnalysesMenu}>
                  <Button type="link">{i18n("nav.main.analysis")}</Button>
                </Dropdown>
              </Menu.Item>
              {!isAdmin && isManager && (
                <Menu.Item key="users">
                  <Dropdown overlay={UsersMenu}>
                    <Button type="link">{i18n("nav.main.users-list")}</Button>
                  </Dropdown>
                </Menu.Item>
              )}
              {!isAdmin && (
                <Menu.Item key="remote-apis">
                  <Button type="link" href={setBaseUrl("/remote_api")}>
                    {i18n("nav.main.remoteapis")}
                  </Button>
                </Menu.Item>
              )}
            </Space>
            <Space align="center">
              <Menu.Item key="global-search">
                <GlobalSearch />
              </Menu.Item>
              {isAdmin && (
                <Menu.Item key="admin-panel">
                  <Button
                    type="primary"
                    className="t-admin-panel-btn"
                    href={setBaseUrl("/admin")}
                  >
                    {i18n("MainNavigation.admin").toUpperCase()}
                  </Button>
                </Menu.Item>
              )}
              <Menu.Item key="cart-link">
                <CartLink />
              </Menu.Item>
              <Menu.Item key="announcements-dropdown-link">
                <AnnouncementsSubMenu />
              </Menu.Item>
              <Menu.Item key="help-dropdown-link">
                <Dropdown overlay={HelpMenu}>
                  <Button type="link">
                    <IconQuestionCircle />
                  </Button>
                </Dropdown>
              </Menu.Item>
              <Menu.Item key="account-dropdown-link">
                <Dropdown overlay={AccountMenu}>
                  <Button type="link" icon={<IconUser />}>
                    {window.TL._USER.username}
                  </Button>
                </Dropdown>
              </Menu.Item>
            </Space>
          </MenuStyle>
        </Menu>
      </Col>
    </Row>
  );
}
