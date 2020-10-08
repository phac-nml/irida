import React, { createContext, useContext, useEffect, useState } from "react";

const RolesContext = createContext();

/**
 * React context to allow multiple components to get a list of project roles
 * without having to make multiple requests, or passing through props.
 *
 * @param children - a React component
 * @returns {*}
 * @constructor
 */
function RolesProvider({ children, rolesFn }) {
  const [roles, setRoles] = useState([]);

  /*
  When the component is mounted, get the list up current roles from the server.
   */
  useEffect(() => {
    rolesFn().then((data) => setRoles(data));
  }, [rolesFn]);

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
    <RolesContext.Provider value={{ roles, getRoleFromKey }}>
      {children}
    </RolesContext.Provider>
  );
}

function useRoles() {
  const context = useContext(RolesContext);
  if (context === undefined) {
    throw new Error("useRoles must be used within a RolesProvider");
  }
  return context;
}

export { RolesProvider, useRoles };
