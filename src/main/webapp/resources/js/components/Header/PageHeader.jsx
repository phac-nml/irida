import React, { Suspense } from "react";
import { render } from "react-dom";
import { blue1 } from "../../styles/colors";

const GalaxyAlert = React.lazy(() => import("./GalaxyAlert"));

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
        {this.state.inGalaxy ? (
          <Suspense
            fallback={
              <div
                style={{
                  backgroundColor: blue1,
                  height: 58
                }}
              />
            }
          >
            <GalaxyAlert />
          </Suspense>
        ) : null}
      </div>
    );
  }
}

render(<PageHeader />, document.querySelector(".js-page-header"));
