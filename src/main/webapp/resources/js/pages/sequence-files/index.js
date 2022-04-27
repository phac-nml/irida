import React from "react";

import { render } from "react-dom";
import { BrowserRouter, Route, Routes, useLocation } from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import FastQC from "./components/FastQC";
import FastQCCharts from "../../components/samples/components/fastqc/FastQCCharts";
import FastQCDetails from "../../components/samples/components/fastqc/FastQCDetails";
import OverRepresentedSequences from "../../components/samples/components/fastqc/OverRepresentedSequences";
import { getRootPath } from "./fastqc-utilities";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

function App() {
  const location = useLocation();
  let [path, route] = getRootPath(location.pathname);

  React.useEffect(() => {
    [path, route] = getRootPath(location.pathname);
  }, [location.pathname]);

  return (
    <Routes>
      <Route path={path} element={<FastQC current={route} />}>
        <Route index element={<FastQCCharts />} />
        <Route path="overrepresented" element={<OverRepresentedSequences />} />
        <Route path="details" element={<FastQCDetails />} />
        <Route path="*" element={<FastQCCharts />} />
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

