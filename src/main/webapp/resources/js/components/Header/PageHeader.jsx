import React, { Suspense } from "react";
import { render } from "react-dom";

const GalaxyAlert = React.lazy(() => import("./GalaxyAlert"));

export class PageHeader extends React.Component {
  state = {
    inGalaxy: false
  };

  componentDidMount() {
    if (window.TL.IN_GALAXY) {
      this.setState({ inGalaxy: true });
    }
  }

  render() {
    return (
      <div>
        {this.state.inGalaxy ? (
          <Suspense fallback={<div>Loading...</div>}>
            <GalaxyAlert />
          </Suspense>
        ) : null}
      </div>
    );
  }
}

render(<PageHeader />, document.querySelector(".js-page-header"));
