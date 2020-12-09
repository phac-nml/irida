/*
 * This file contains the state and functions
 * required for displaying user groups pages
 */

import React from "react";
import { deleteUserGroup } from "../apis/users/groups";
import { navigate } from "@reach/router";
import { notification } from "antd";

const initialContext = {};

const UserGroupsContext = React.createContext(initialContext);

function UserGroupsProvider(props) {
  /*
   * Deletes User Group and shows confirmation notification.
   */
  function userGroupsContextDeleteUserGroup(id, url) {
    deleteUserGroup(id).then((message) => {
      navigate(`${url}`, { replace: true });
      notification.success({ message });
    }).catch((message) => {
      notification.error({ message })
    });
  }

  return (
    <UserGroupsContext.Provider
      value={{
        userGroupsContextDeleteUserGroup
      }}
    >
      {props.children}
    </UserGroupsContext.Provider>
  );
}
export { UserGroupsContext, UserGroupsProvider };
