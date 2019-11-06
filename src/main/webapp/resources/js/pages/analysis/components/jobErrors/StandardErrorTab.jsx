/*
 * This file renders the 'Standard Error' from
 * the jobErrors object returned by the server. If there are
 * multiple job errors then it displays the standard error
 * in the related tab
 */

import React from "react";
import { PassTabs } from "./PassTabs";
import { StandardError } from "./StandardError";
import { getI18N } from "../../../../utilities/i18n-utilties";
import { TabPaneContent } from "../../../../components/tabs/TabPaneContent";

export default function StandardErrorTab({
  currActiveKey,
  updateActiveKey,
  galaxyJobErrors
}) {
  return (
    <TabPaneContent title={getI18N("AnalysisError.standardError")}>
      {galaxyJobErrors.length > 1 ? (
        <PassTabs
          tabName="standard-error"
          currActiveKey={currActiveKey}
          updateActiveKey={updateActiveKey}
          galaxyJobErrors={galaxyJobErrors}
        />
      ) : (
        <StandardError galaxyJobErrors={galaxyJobErrors} />
      )}
    </TabPaneContent>
  );
}
