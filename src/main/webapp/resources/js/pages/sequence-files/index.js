import React from "react";

import { render } from "react-dom";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import FastQC from "./components/FastQC";
import FastQCCharts from "./components/FastQCCharts";
import FastQCDetails from "./components/FastQCDetails";
import OverRepresentedSequences from "./components/OverRepresentedSequences";
import { getRootPath } from "./fastqc-utilities";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

function App() {
  const [path, route] = getRootPath(location.pathname);

  return (
    <Routes>
      <Route path={path} element={<FastQC current={route} />}>
        <Route path="overrepresented" element={<OverRepresentedSequences />} />
        <Route path="details" element={<FastQCDetails />} />
        <Route path="charts" element={<FastQCCharts />} />
        <Route path="summary" element={<FastQCCharts />} />
        <Route index element={<FastQCCharts />} />
      </Route>
    </Routes>
  );
}

render(
  <BrowserRouter>
    <App />
  </BrowserRouter>,
  document.getElementById("root")
);

