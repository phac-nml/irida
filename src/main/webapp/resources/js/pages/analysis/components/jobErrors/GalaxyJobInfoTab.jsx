/*
 * This file renders the 'Galaxy Job Information' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays this galaxy job
 * information in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { GalaxyJobInfo } from "./GalaxyJobInfo";

import { TabPanelContent } from "../../../../components/tabs/TabPanelContent";

export default function GalaxyJobInfoTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors,
  galaxyUrl,
}) {
  return (
    <TabPanelContent title={i18n("AnalysisError.galaxyJobInfo")}>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="job-error-info"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
          galaxyUrl={galaxyUrl}
        />
      ) : (
        <GalaxyJobInfo
          galaxyJobErrors={galaxyJobErrors}
          galaxyUrl={galaxyUrl}
        />
      )}
    </TabPanelContent>
  );
}
