import React, { useState, useContext } from "react";
import { Button, Checkbox, Alert, Popconfirm, Row, Typography } from "antd";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { showNotification } from "../../../modules/notifications";
import { getI18N } from "../../../utilities/i18n-utilties";

import { deleteAnalysis } from "../../../apis/analysis/analysis";
const { Title } = Typography;

export default function AnalysisDelete() {
  const { analysisContext } = useContext(AnalysisContext);
  const [deleteConfirm, setDeleteConfirm] = useState(false);

  function onChange(e) {
    // Set local state for deleteConfirm
    setDeleteConfirm(e.target.checked);
  }

  function handleDeleteConfirm() {
    if (deleteConfirm) {
      deleteAnalysis(analysisContext.analysis.identifier).then(res =>
        showNotification({ text: res.result })
      );

      window.setTimeout(function() {
        window.location.replace(window.TL.BASE_URL);
      }, 3500);
    }
  }

  return (
    <>
      <Title level={2}>{getI18N("analysis.tab.delete-analysis")}</Title>
      <Alert
        message=<strong className="spaced-top__sm">
          {getI18N("analysis.tab.content.delete.permanent-action-warning")}
        </strong>
        type="warning"
      />

      <Row className="spaced-top__lg">
        <Checkbox onChange={onChange}>
          {getI18N("analysis.tab.content.delete.checkbox-confirmation-label")}
        </Checkbox>
      </Row>

      <Row>
        <Popconfirm
          placement="top"
          title={`${getI18N("analysis.tab.delete-analysis")} ${
            analysisContext.analysisName
          }?`}
          okText={getI18N("analysis.tab.content.delete.confirm")}
          cancelText={getI18N("analysis.tab.content.delete.cancel")}
          onConfirm={handleDeleteConfirm}
          disabled={!deleteConfirm}
        >
          <Button
            type="danger"
            className="spaced-top__lg"
            disabled={!deleteConfirm}
          >
            {getI18N("analysis.tab.content.delete.button")}
          </Button>
        </Popconfirm>
      </Row>
    </>
  );
}
