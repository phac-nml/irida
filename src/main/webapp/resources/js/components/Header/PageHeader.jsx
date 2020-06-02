import React from "react";
import { render } from "react-dom";
import { Session } from "../session/Session";
import { Notifications } from "../notifications/Notifications";
import GalaxyAlert from "./GalaxyAlert";
import { Breadcrumbs } from "./Breadcrumbs";
import { setBaseUrl } from "../../utilities/url-utilities";
import { Avatar, Input, Menu } from "antd";
import { grey10, grey4 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";
import { IconLogout, IconUser } from "../icons/Icons";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

export class PageHeader extends React.Component {
  state = {
    inGalaxy: false,
  };

  componentDidMount() {
    if (typeof window.GALAXY !== "undefined") {
      this.setState({ inGalaxy: true });
    }
  }

  render() {
    return (
      <>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            borderBottom: "1px solid #f0f0f0",
            paddingRight: 20,
          }}
        >
          <Menu mode="horizontal" style={{ border: "none" }}>
            <Menu.Item>
              <form layout="inline" action={setBaseUrl("/search")}>
                <Input.Search name="query" style={{ width: 400 }} />
              </form>
            </Menu.Item>
          </Menu>
          <Menu mode="horizontal" style={{ border: "none" }}>
            <Menu.SubMenu
              title={
                <>
                  <Avatar
                    size="small"
                    style={{
                      backgroundColor: grey4,
                      color: grey10,
                      marginRight: SPACE_XS,
                    }}
                    icon={<IconUser />}
                  />
                  {`${window.TL._USER.firstName} ${window.TL._USER.lastName}`}
                </>
              }
            >
              <Menu.Item>
                <>
                  <IconLogout style={{ marginRight: SPACE_XS }} />
                  <a href={setBaseUrl("/logout")}>{i18n("nav.main.logout")}</a>
                </>
              </Menu.Item>
            </Menu.SubMenu>
          </Menu>
        </div>
        <Breadcrumbs crumbs={window.breadcrumbs} />
        <Session />
        <Notifications />
        {this.state.inGalaxy ? <GalaxyAlert /> : null}
      </>
    );
  }
}

render(<PageHeader />, document.querySelector(".js-page-header"));
