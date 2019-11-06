/*
 * This file renders the 'Galaxy Job Information' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays this galaxy job
 * information in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { GalaxyJobInfo } from "./GalaxyJobInfo";
import { getI18N } from "../../../../utilities/i18n-utilties";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function GalaxyJobInfoTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors,
  galaxyUrl
}) {
  return (
    <TabPaneContent title={getI18N("AnalysisError.galaxyJobInfo")}>
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
    </TabPaneContent>
  );
}
