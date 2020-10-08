/*
 * This file renders the Admin Panel Statistics component
 */

import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { AnalysesQueue } from "../../../components/AnalysesQueue";
import BasicStats from "./statistics/BasicStats";

export default function AdminStatistics() {

  return (
    <PageWrapper
      title={i18n("AdminPanel.statistics")}
      headerExtras={<AnalysesQueue />}
    >
      <BasicStats />
    </PageWrapper>
  );
}