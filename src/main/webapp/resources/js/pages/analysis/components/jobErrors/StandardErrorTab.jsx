/*
 * This file renders the 'Standard Error' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays the standard error
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { StandardErrorOutput } from "./StandardErrorOutput";

import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function StandardErrorTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors
}) {
  return (
    <TabPaneContent title={i18n("AnalysisError.standardError")}>
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
    </TabPaneContent>
  );
}
