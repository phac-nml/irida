import React from "react";
import { Tag } from "antd";
import { ExportUploadState } from "../../../types/irida";
import { ExportTagStates } from "./ExportUploadTag";

const states: ExportTagStates = {
  NEW: {
    color: "blue",
    text: i18n("ExportUploadState.NEW"),
  },
  UPLOADING: {
    color: "blue",
    text: i18n("ExportUploadState.UPLOADING"),
  },
  UPLOADED: {
    color: "green",
    text: i18n("ExportUploadState.UPLOADED"),
  },
  UPLOAD_ERROR: {
    color: "red",
    text: i18n("ExportUploadState.UPLOAD_ERROR"),
  },
  created: {
    text: i18n("ExportUploadState.created"),
  },
  failed: {
    color: "red",
    text: i18n("ExportUploadState.failed"),
  },
  queued: {
    text: i18n("ExportUploadState.queued"),
  },
  processing: {
    text: i18n("ExportUploadState.processing"),
  },
  "processed-ok": {
    text: i18n("ExportUploadState.processed-ok"),
  },
  "processed-error": {
    color: "red",
    text: i18n("ExportUploadState.processed-error"),
  },
  waiting: {
    text: i18n("ExportUploadState.waiting"),
  },
  submitted: {
    text: i18n("ExportUploadState.submitted"),
  },
  "Submission deleted": {
    text: "DELETED",
  },
  retired: {
    text: "RETIRED",
  },
  unknown: {
    text: "UNKNOWN",
  },
};

/**
 * React component to render an NCBI Upload State in a tag with an appropriate
 * colour.  Also provides internationalization for the state.
 * @param state
 */
const ExportUploadStateTag = ({
  state,
}: {
  state: ExportUploadState;
}): JSX.Element => {
  return states[state] ? (
    <Tag color={states[state].color}>{states[state].text}</Tag>
  ) : (
    <Tag>{i18n("ExportUploadState.unknown")}</Tag>
  );
};

export default ExportUploadStateTag;
