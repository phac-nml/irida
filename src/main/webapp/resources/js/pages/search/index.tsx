import { Layout } from "antd";
import React from "react";
import { createRoot } from "react-dom/client";
import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
} from "react-router-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import userLoader from "./loaders/user-loader";
import SearchLayout from "./SearchLayout";

/**
 * @fileoverview
 * Sets up the base layout and router for the global search page
 */

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
if (element) {
  const root = createRoot(element);
  root.render(
    <Layout
      style={{
        minHeight: `100%`,
        display: `flex`,
        flexDirection: "column",
      }}
    >
      <RouterProvider router={router} />
    </Layout>
  );
} else {
  throw new Error(`Cannot find root element "#root"`);
}
