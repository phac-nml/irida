import React from "react";
import { Avatar, Col, Menu, Row, Space } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { SPACE_MD } from "../../styles/spacing";
import { IconQuestionCircle, IconUser } from "../icons/Icons";
import { CartLink } from "./main-navigation/components/CartLink";
import { GlobalSearch } from "./main-navigation/components/GlobalSearch";
import { primaryColour, theme } from "../../utilities/theme-utilities";

export function MainNavigation() {
  const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";

  return (
    <Row
      style={{
        backgroundColor: theme === "dark" ? "#001529" : "transparent",
        display: "flex",
        alignItems: "center",
        borderBottom: `2px solid ${primaryColour}`,
      }}
    >
      <Col md={10} sm={24}>
        <a href={setBaseUrl("/")} style={{ padding: `0 ${SPACE_MD}` }}>
          <img
            style={{ height: 28 }}
            src={setBaseUrl(`/resources/img/irida_logo_${theme}.svg`)}
            alt={i18n("global.title")}
          />
        </a>
        <Menu
          mode="horizontal"
          theme={theme}
          style={{ display: "inline-block", borderBottom: "none" }}
        >
          <Menu.SubMenu key="projects" title={i18n("nav.main.project")}>
            <Menu.Item key="project:list">
              <a href={setBaseUrl("/projects")}>
                {i18n("nav.main.project-list")}
              </a>
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
          </Menu.SubMenu>
          <Menu.SubMenu title={i18n("nav.main.analysis")}>
            <Menu.Item>
              <a href={setBaseUrl(`/analysis`)}>
                {i18n("nav.main.analysis-admin-user")}
              </a>
            </Menu.Item>
            {isAdmin && (
              <Menu.Item key="project:all">
                <a href={setBaseUrl("/analysis/all")}>
                  {i18n("nav.main.analysis-admin-all")}
                </a>
              </Menu.Item>
            )}
            <Menu.Divider />
            <Menu.Item>
              <a href={setBaseUrl("/analysis/user/analysis-outputs")}>
                {i18n("Analysis.outputFiles")}
              </a>
            </Menu.Item>
          </Menu.SubMenu>
        </Menu>
      </Col>
      <Col
        md={14}
        sm={24}
        style={{
          display: "flex",
          flexDirection: "row-reverse",
          alignItems: "center",
          width: `100%`,
        }}
      >
        <Menu
          mode="horizontal"
          theme={theme}
          style={{
            display: "inline-block",
            borderBottom: "none",
            marginLeft: SPACE_MD,
          }}
        >
          <Menu.Item icon={<CartLink />} />
          {isAdmin && (
            <Menu.Item>
              <a href={setBaseUrl("/admin")}>
                {i18n("MainNavigation.admin").toUpperCase()}
              </a>
            </Menu.Item>
          )}
          <Menu.SubMenu title={<IconQuestionCircle />}>
            <Menu.ItemGroup title="Documentation">
              <Menu.Item>
                <a
                  href="https://irida.corefacility.ca/documentation/user/user"
                  target="_blank"
                >
                  {i18n("nav.main.userguide")}
                </a>
              </Menu.Item>
              {isAdmin && (
                <Menu.Item>
                  <a href="https://irida.corefacility.ca/documentation/user/administrator">
                    {i18n("nav.main.adminguide")}
                  </a>
                </Menu.Item>
              )}
            </Menu.ItemGroup>
            <Menu.Divider />
            <Menu.Item>
              <a
                href="http://www.irida.ca"
                target="_blank"
                rel="noopener noreferrer"
              >
                {i18n("generic.irida.website")}
              </a>
            </Menu.Item>
            <Menu.Divider />
            <Menu.Item disabled>{i18n("irida.version")}</Menu.Item>
          </Menu.SubMenu>
          <Menu.SubMenu
            title={
              <Space>
                <Avatar
                  size="small"
                  style={{ backgroundColor: primaryColour }}
                  icon={<IconUser />}
                />
                {window.TL._USER.username}
              </Space>
            }
          >
            <Menu.Item>
              <a href={setBaseUrl(`/users/current`)}>
                {i18n("nav.main.account")}
              </a>
            </Menu.Item>
            <Menu.Item>
              <a href={setBaseUrl("/logout")}>{i18n("nav.main.logout")}</a>
            </Menu.Item>
          </Menu.SubMenu>
        </Menu>
        <GlobalSearch />
      </Col>
    </Row>
  );
}
