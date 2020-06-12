/*
 * This file renders the Users component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";
import PageHeader from "antd";

export default function AdminUsers() {
  // The following renders the Users component view
  return (
    <PageHeader title={i18n("admin.panel.users")} className={"t-admin-users-title"} />
  );
}