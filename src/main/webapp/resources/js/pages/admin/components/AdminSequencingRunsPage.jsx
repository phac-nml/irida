/*
 * This file renders the Sequencing Runs component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { PagedTableProvider } from "../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../utilities/url-utilities";
import SequencingRunListTable
  from "../../sequencing-runs/components/SequencingRunListTable";

export default function AdminSequencingRunsPage({}) {
  const URL = setBaseUrl(`ajax/sequencing-runs`);

  // The following renders the Sequencing Runs component view
  return (
    <PageWrapper title={i18n("AdminPanel.sequencingRuns")}>
      <PagedTableProvider url={`${URL}/list`}>
        <SequencingRunListTable/>
      </PagedTableProvider>
    </PageWrapper>
  );
}