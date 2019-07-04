import React, { useState, useContext } from "react";
import { Button, Checkbox, Alert, Popconfirm, Row } from "antd";
import { AnalysisContext } from "../../../state/AnalysisState";
import { showNotification } from "../../../modules/notifications";
import { getI18N } from "../../../utilities/i18n-utilties";

import { deleteAnalysis } from "../../../apis/analysis/analysis";

export default function AnalysisDelete() {
  const { state } = useContext(AnalysisContext);
  const [deleteConfirm, setDeleteConfirm] = useState(false);

  function onChange(e) {
    if (e.target.checked == true) {
      setDeleteConfirm(true);
    } else {
      setDeleteConfirm(false);
    }
  }

  function handleDeleteConfirm() {
    if (deleteConfirm) {
      deleteAnalysis(state.analysis.identifier).then(res =>
        showNotification({ text: res.result })
      );

      window.setTimeout(function() {
        window.location.replace(window.TL.BASE_URL);
      }, 3500);
    }
  }

  return (
    <>
      <h2 style={{ fontWeight: "bold" }}>
        {getI18N("analysis.tab.delete-analysis")}
      </h2>
      <strong className="spaced-top__sm">
        <Alert
          message={getI18N(
            "analysis.tab.content.delete.permanent-action-warning"
          )}
          type="warning"
        />
      </strong>
      <Row className="spaced-top__lg">
        <Checkbox onChange={onChange}>
          {getI18N("analysis.tab.content.delete.checkbox-confirmation-label")}
        </Checkbox>
      </Row>
      <Row>
        <Popconfirm
          placement="top"
          title={`Delete Analysis ${state.analysisName}?`}
          okText="Confirm"
          cancelText="Cancel"
          onConfirm={handleDeleteConfirm}
        >
          <Button
            type="danger"
            className="spaced-top__lg"
            disabled={deleteConfirm ? false : true}
          >
            {getI18N("analysis.tab.content.delete.button")}
          </Button>
        </Popconfirm>
      </Row>
    </>
  );
}
