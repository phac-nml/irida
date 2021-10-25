import React from "react";
import { getMetadataRoles } from "../apis/metadata/metadata";

const MetadataRolesContext = React.createContext();

function MetadataRolesProvider({ children }) {
  const [roles, setRoles] = React.useState([]);

  React.useEffect(() => {
    getMetadataRoles().then((data) => setRoles(data));
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
    <MetadataRolesContext.Provider
      value={{
        roles,
        getRoleFromKey,
      }}
    >
      {children}
    </MetadataRolesContext.Provider>
  );
}

function useMetadataRoles() {
  const context = React.useContext(MetadataRolesContext);
  if (context === undefined) {
    throw new Error(
      "useMetadataRoles must be used within a MetadataRolesProvider"
    );
  }
  return context;
}

export { MetadataRolesProvider, useMetadataRoles };
