import React from "react";
import { Button, Layout, Menu } from "antd";
import { SPACE_LG, SPACE_MD } from "../../../styles/spacing";
import { theme } from "../../../utilities/theme-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { IconUser } from "../../icons/Icons";
import { AnnouncementsSubMenu } from "./components/AnnouncementsSubMenu";
import { CartLink } from "./components/CartLink";
import { GlobalSearch } from "./components/GlobalSearch";
import "./MainNavigation.css";

const { Header } = Layout;

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";
const isManager = isAdmin || window.TL._USER.systemRole === "ROLE_MANAGER";
const isTechnician = window.TL._USER.systemRole === "ROLE_TECHNICIAN";

export function MainNavigation() {
  const [isLargeScreen, setIsLargeScreen] = React.useState(
    window.innerWidth > 1050
  );

  React.useEffect(() => {
    const handleResize = () => {
      setIsLargeScreen(window.innerWidth > 1050);
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <Header className="main-navigation">
      <GlobalSearch />
      {isAdmin && (
        <div style={{ padding: `0 ${SPACE_MD}` }}>
          <Button
            type="primary"
            className="t-admin-panel-btn"
            href={setBaseUrl("/admin")}
          >
            {i18n("MainNavigation.admin").toUpperCase()}
          </Button>
        </div>
      )}
      <CartLink />
      <AnnouncementsSubMenu />
      <Menu theme={theme} mode="horizontal" defaultSelectedKeys={[""]}>
        <Menu.SubMenu key="account-dropdown-link" icon={<IconUser />}>
          <Menu.Item key="account">
            <a href={setBaseUrl(`/users/current`)}>
              {i18n("nav.main.account")}
            </a>
          </Menu.Item>
          <Menu.SubMenu key="help" title={i18n("nav.main.help")}>
            <Menu.Item key="userguide">
              <a
                href="https://phac-nml.github.io/irida-documentation/user/user/"
                target="_blank"
                rel="noreferrer"
              >
                {i18n("nav.main.userguide")}
              </a>
            </Menu.Item>
            {isAdmin && (
              <Menu.Item key="adminguide">
                <a
                  href="https://phac-nml.github.io/irida-documentation/user/administrator/"
                  target="_blank"
                  rel="noreferrer"
                >
                  {i18n("nav.main.adminguide")}
                </a>
              </Menu.Item>
            )}
            <Menu.Divider />
            <Menu.Item key="website">
              <a
                href="http://www.irida.ca"
                target="_blank"
                rel="noopener noreferrer"
              >
                {i18n("generic.irida.website")}
              </a>
            </Menu.Item>
            <Menu.Divider />
            <Menu.Item key="help:version" disabled>
              {i18n("irida.version")}
            </Menu.Item>
          </Menu.SubMenu>
          <Menu.Item key="logout">
            <a href={setBaseUrl("/logout")}>{i18n("nav.main.logout")}</a>
          </Menu.Item>
        </Menu.SubMenu>
      </Menu>
    </Header>
  );
}
