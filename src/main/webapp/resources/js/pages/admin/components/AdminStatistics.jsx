/*
 * This file renders the Admin Panel Statistics component
 */

import React, { useContext } from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { AnalysesQueue } from "../../../components/AnalysesQueue";
import BasicStats from "./statistics/BasicStats";
import { AdminStatisticsContext } from "../../../contexts/AdminStatisticsContext";

export default function AdminStatistics() {
  const { adminStatisticsContext } = useContext(AdminStatisticsContext);

  return (
    <PageWrapper
      title={i18n("AdminPanel.statistics")}
      headerExtras={<AnalysesQueue />}
    >
      <BasicStats statistics={adminStatisticsContext.basicStats} />
    </PageWrapper>
  );
}