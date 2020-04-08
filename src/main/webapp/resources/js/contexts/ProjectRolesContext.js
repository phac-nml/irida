import React, { createContext, useEffect, useState } from "react";
import { getProjectRoles } from "../apis/projects/projects";

let ProjectRolesContext;
const { Provider } = (ProjectRolesContext = createContext());

function ProjectRolesProvider({ children }) {
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    getProjectRoles().then((data) => setRoles(data));
  }, []);

  return <Provider value={{ roles }}>{children}</Provider>;
}

export { ProjectRolesContext, ProjectRolesProvider };
