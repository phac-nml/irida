import React from "react";
import { render } from "react-dom";
import { Session } from "../session/Session";
import { Notifications } from "../notifications/Notifications";
import GalaxyAlert from "./GalaxyAlert";
import { Breadcrumb } from "antd";
import { HomeTwoTone } from "@ant-design/icons";
import { setBaseUrl } from "../../utilities/url-utilities";
import styled from "styled-components";
import { blue1 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = `dist/`;

const BreadCrumbs = styled(Breadcrumb)`
  background-color: ${blue1};
  padding: ${SPACE_XS};
`;

export class PageHeader extends React.Component {
  state = {
    inGalaxy: false
  };

  componentDidMount() {
    if (typeof window.GALAXY !== "undefined") {
      this.setState({ inGalaxy: true });
    }
  }

  render() {
    return (
      <div>
        {window.breadcrumbs ? (
          <BreadCrumbs>
            <Breadcrumb.Item>
              <a href={setBaseUrl("")}>
                <HomeTwoTone />
              </a>
            </Breadcrumb.Item>
            {window.breadcrumbs.map(crumb => (
              <Breadcrumb.Item key={crumb.label}>
                <a href={crumb.url}>{crumb.label}</a>
              </Breadcrumb.Item>
            ))}
          </BreadCrumbs>
        ) : null}
        <Session />
        <Notifications />
        {this.state.inGalaxy ? <GalaxyAlert /> : null}
      </div>
    );
  }
}

render(<PageHeader />, document.querySelector(".js-page-header"));
