import * as React from "react";
import { render } from "react-dom";
import { Dashboard } from "../components/dashboad";
import { navigationItems } from "./navigationItems";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import StatisticsPage from "./pages/StatisticsPage";
import UserGroupsPage from "./pages/UserGroupsPage";
import UsersPage from "./pages/UsersPage";
import ClientsPage from "./pages/ClientsPage";
import RemoteConnectionsPage from "./pages/RemoteConnectionsPage";
import AnnouncementsPage from "./pages/AnnouncementsPage";

function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <Dashboard navigation={navigationItems} title={"IRIDA Admin"} />
        }
      >
        <Route index element={<StatisticsPage />} />
        <Route path="users" element={<UsersPage />} />
        <Route path="user-groups" element={<UserGroupsPage />} />
        <Route path="clients" element={<ClientsPage />} />
        <Route path="remote-connections" element={<RemoteConnectionsPage />} />
        <Route path="ncbi-exports" element={<RemoteConnectionsPage />} />
        <Route path="announcements" element={<AnnouncementsPage />} />
      </Route>
    </Routes>
  );
}

render(
  <Router basename={"/beta/admin"}>
    <App />
  </Router>,
  document.querySelector("#root")
);
