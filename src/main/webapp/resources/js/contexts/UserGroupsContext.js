/*
 * This file contains the state and functions
 * required for displaying user groups pages
 */

import React from "react";

import {
  showNotification,
  showErrorNotification
} from "../modules/notifications";
import { deleteUserGroup } from "../apis/users/groups";
import { navigate } from "@reach/router";

const initialContext = {};

const UserGroupsContext = React.createContext(initialContext);

function UserGroupsProvider(props) {
  /*
   * Deletes User Group and show confirmation notification.
   */
  function userGroupsContextDeleteUserGroup(id, url) {
    deleteUserGroup(id).then(res => {
      if (res.type === "error") {
        showErrorNotification({ text: res.text, type: res.type });
      } else {
        navigate(`${url}`, { replace: true });
        showNotification({ text: "User Group deleted successfully" });
      }
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
