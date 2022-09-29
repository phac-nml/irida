/*
 * This file renders the Delete Analysis component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */
import React, { useContext } from "react";
import { Button, Popconfirm } from "antd";
import { AnalysisContext } from "../../../../contexts/AnalysisContext";
import { showNotification } from "../../../../modules/notifications";

import { deleteAnalysis } from "../../../../apis/analysis/analysis";
import { WarningAlert } from "../../../../components/alerts/WarningAlert";
import { TabPanelContent } from "../../../../components/tabs/TabPanelContent";
import { SPACE_LG } from "../../../../styles/spacing";
import { setBaseUrl } from "../../../../utilities/url-utilities";

export default function AnalysisDelete() {
  /*
   * The following const statement
   * make the required context which contains
   * the state and methods available to the component
   */
  const { analysisContext, analysisIdentifier } = useContext(AnalysisContext);

  /* Delete the analysis if the user selected
   * the confirm delete checkbox and clicked
   * confirm within the popup, then redirect
   * to the dashboard
   */
  function handleDeleteConfirm() {
    deleteAnalysis(analysisIdentifier).then((res) =>
      showNotification({ text: res.result })
    );

    window.setTimeout(function () {
      window.location.replace(setBaseUrl("/"));
    }, 3500);
  }

  // The following renders the Delete Analysis component view
  return (
    <TabPanelContent title={i18n("AnalysisDelete.deleteAnalysis")}>
      <WarningAlert message={i18n("AnalysisDelete.permanentActionWarning")} />

      <section>
        <Popconfirm
          placement="right"
          title={i18n("AnalysisDelete.deleteAnalysisSubmission").replace(
            "[NAME]",
            analysisContext.analysisName
          )}
          okText={i18n("AnalysisDelete.confirm")}
          cancelText={i18n("AnalysisDelete.cancel")}
          onConfirm={handleDeleteConfirm}
        >
          <Button
            type="danger"
            style={{ marginTop: SPACE_LG }}
            className="t-delete-analysis-btn"
          >
            {i18n("AnalysisDelete.delete")}
          </Button>
        </Popconfirm>
      </section>
    </TabPanelContent>
  );
}
