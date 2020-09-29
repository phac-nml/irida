import React from "react";
import { render } from "react-dom";
import { Button, PageHeader, Popconfirm } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { deleteSequencingRun } from "../../../apis/sequencing-runs/sequencing-runs";

function SequencingRunDetails() {
  const runId = window.location.href.match(/sequencingRuns\/(\d+)/)[1];

  return (
    <PageHeader
      title={i18n("SequenceRunDetails.header", runId)}
      onBack={() =>
        (window.location.href = setBaseUrl(`/admin/sequencing_runs`))
      }
      extra={[
        window.TL._USER.systemRole === "ROLE_ADMIN" ? (
          <Popconfirm
            key="remove-btn"
            title={i18n("SequenceRunDetails.delete.confirmation")}
            onConfirm={() => deleteSequencingRun({ id: runId })}
          >
            <Button>{i18n("SequenceRunDetails.delete")}</Button>
          </Popconfirm>
        ) : null,
      ]}
    />
  );
}

render(<SequencingRunDetails />, document.querySelector("#root"));
