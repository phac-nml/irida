import React from "react";

import { render } from "react-dom";
import FastQC from "./components/FastQC";
import { setBaseUrl } from "../../utilities/url-utilities";
import { Location, Router } from "@reach/router";
import { getRootPath } from "./fastqc-utilities";

import FastQCDetails from "./components/FastQCDetails";
import FastQCCharts from "./components/FastQCCharts";
import OverRepresentedSequences from "./components/OverRepresentedSequences";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

render(
  <Location style={{ height: "100%" }}>
    {({ location }) => {
      const [path, route] = getRootPath(location.pathname);
      return (
        <Router style={{ height: "100%" }}>
          <FastQC path={path} route={route}>
            <OverRepresentedSequences path="overrepresented" />
            <FastQCDetails path="details" />
            <FastQCCharts path="charts" />
            {/* Handles legacy link case */}
            <FastQCCharts path="summary" />
            <FastQCCharts path="" default />
          </FastQC>
        </Router>
      );
    }}
  </Location>,
  document.getElementById("root")
);

