import React, { createContext, useEffect, useState } from "react";
import { getProjectRoles } from "../apis/projects/projects";

let ProjectRolesContext;
const { Provider } = (ProjectRolesContext = createContext());

function ProjectRolesProvider({ children }) {
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    getProjectRoles().then((data) => setRoles(data));
  }, []);

  const getRoleFromKey = (key) => roles.find((r) => r.value === key)?.label;

  return <Provider value={{ roles, getRoleFromKey }}>{children}</Provider>;
}

export { ProjectRolesContext, ProjectRolesProvider };
