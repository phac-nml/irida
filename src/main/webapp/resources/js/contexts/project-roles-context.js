import React, { createContext, useContext, useEffect, useState } from "react";
import { getProjectRoles } from "../apis/projects/projects";

const ProjectRolesContext = createContext();

/**
 * React context to allow multiple components to get a list of project roles
 * without having to make multiple requests, or passing through props.
 *
 * @param children - a React component
 * @param {function} getRolesFn - function to get roles (whether project or group)
 * @returns {*}
 * @constructor
 */
function ProjectRolesProvider({ children }) {
  const [roles, setRoles] = useState([]);

  /*
  When the component is mounted, get the list up current roles from the server.
   */
  useEffect(() => {
    getProjectRoles().then((data) => setRoles(data));
  }, []);

  /**
   * Find the translation for any project role.  If the role is not found,
   * just return "UNKnOWN"
   *
   * @param key
   * @returns {*}
   */
  const getRoleFromKey = (key) => {
    const role = roles.find((r) => r.value === key);
    return role ? role.label : "UNKNOWN";
  };

  return (
    <ProjectRolesContext.Provider value={{ roles, getRoleFromKey }}>
      {children}
    </ProjectRolesContext.Provider>
  );
}

function useProjectRoles() {
  const context = useContext(ProjectRolesContext);
  if (context === undefined) {
    throw new Error(
      "useProjectRoles must be used within a ProjectRolesProvider"
    );
  }
  return context;
}

export { ProjectRolesProvider, useProjectRoles };
