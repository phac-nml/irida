import React from "react";
import { render } from "react-dom";
import { Session } from "../session/Session";
import { Notifications } from "../notifications/Notifications";
import GalaxyAlert from "./GalaxyAlert";
import { Breadcrumbs } from "./Breadcrumbs";
import { setBaseUrl } from "../../utilities/url-utilities";

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
      <div>
        <Breadcrumbs crumbs={window.breadcrumbs} />
        <Session />
        <Notifications />
        {this.state.inGalaxy ? <GalaxyAlert /> : null}
      </div>
    );
  }
}

render(<PageHeader />, document.querySelector(".js-page-header"));
