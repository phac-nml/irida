import { Layout } from "antd";
import React from "react";
import { render } from "react-dom";
import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
} from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import userLoader from "./loaders/user-loader";
import SearchLayout from "./SearchLayout";

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route
      path={setBaseUrl("/search")}
      element={<SearchLayout />}
      loader={userLoader}
    />
  )
);

const element = document.querySelector("#root");
render(
  <Layout
    style={{
      minHeight: `100%`,
      display: `flex`,
      flexDirection: "column",
    }}
  >
    <RouterProvider router={router} />
  </Layout>,
  element
);
