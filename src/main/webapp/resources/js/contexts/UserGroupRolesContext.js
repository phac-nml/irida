import React, { createContext, useEffect, useState } from "react";
import { getUserGroupRoles } from "../apis/users/groups";

let UserGroupRolesContext;
const { Provider } = (UserGroupRolesContext = createContext());

function UserGroupRolesProvider({ children }) {
  const [roles, setRoles] = useState([]);

  useEffect(() => {
    getUserGroupRoles().then(setRoles);
  }, []);

  const getRoleFromKey = (key) => {
    const role = roles.find((r) => r.value === key);
    return role ? role.label : "UNKNOWN";
  };

  return <Provider value={{ roles, getRoleFromKey }}>{children}</Provider>;
}

export { UserGroupRolesContext, UserGroupRolesProvider };
