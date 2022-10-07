/*
 * This file renders the 'Standard Error' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays the standard error
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { StandardErrorOutput } from "./StandardErrorOutput";

import { TabPanelContent } from "../../../../components/tabs/TabPanelContent";

export default function StandardErrorTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors,
}) {
  return (
    <TabPanelContent title={i18n("AnalysisError.standardError")}>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="standard-error"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
        />
      ) : (
        <StandardErrorOutput galaxyError={galaxyJobErrors[0].standardError} />
      )}
    </TabPanelContent>
  );
}
