import React from "react";

import { render } from "react-dom";
import { BrowserRouter, Route, Routes, useLocation } from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import FastQC from "./components/FastQC";
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

  return <Routes></Routes>;
}

render(
  <BrowserRouter>
    <App />
  </BrowserRouter>,
  document.getElementById("root")
);
