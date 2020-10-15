/*
 * This file renders the Admin Panel Statistics component
 */

import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { AnalysesQueue } from "../../../components/AnalysesQueue";
import BasicStats from "./statistics/BasicStats";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { navigate } from "@reach/router";

export default function AdminStatistics() {
  const DEFAULT_URL = setBaseUrl("/admin/statistics");
  const returnToBasicStats = () => navigate(DEFAULT_URL);

  return (
    <PageWrapper
      title={i18n("AdminPanel.statistics")}
      onBack={window.location.pathname !== DEFAULT_URL ? returnToBasicStats : null}
      headerExtras={<AnalysesQueue />}
    >
      <BasicStats />
    </PageWrapper>
  );
}