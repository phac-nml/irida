/*
 * This file renders the Statistics component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";
import { PageHeader } from "antd";

export default function AdminStatistics() {
  // The following renders the Statistics component view
  return (
    <PageHeader title={i18n("admin.panel.statistics")} />
  );
}