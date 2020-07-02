import React from "react";
import { Tag } from "antd";

/**
 * React component to render an NCBI Upload State in a tag with an appropriate
 * colour.  Also provides internationalization for the state.
 * @param state
 * @returns {JSX.Element}
 * @constructor
 */
export default function NcbiUploadStates({ state }) {
  const states = {
    NEW: {
      color: "blue",
      text: i18n("NcbiUploadStates.NEW"),
    },
    UPLOADING: {
      color: "blue",
      text: i18n("NcbiUploadStates.UPLOADING"),
    },
    UPLOADED: {
      color: "green",
      text: i18n("NcbiUploadStates.UPLOADED"),
    },
    UPLOAD_ERROR: {
      color: "red",
      text: i18n("NcbiUploadStates.UPLOAD_ERROR"),
    },
    created: {
      text: i18n("NcbiUploadStates.created"),
    },
    failed: {
      color: "red",
      text: i18n("NcbiUploadStates.failed"),
    },
    queued: {
      text: i18n("NcbiUploadStates.queued"),
    },
    processing: {
      text: i18n("NcbiUploadStates.processing"),
    },
    "processed-ok": {
      text: i18n("NcbiUploadStates.processed-ok"),
    },
    "processed-error": {
      color: "red",
      text: i18n("NcbiUploadStates.processed-error"),
    },
    waiting: {
      text: i18n("NcbiUploadStates.waiting"),
    },
    submitted: {
      text: i18n("NcbiUploadStates.submitted"),
    },
  };
  return states[state] ? (
    <Tag color={states[state].color}>{states[state].text}</Tag>
  ) : (
    <Tag>{i18n("NcbiUploadStates.unknown")}</Tag>
  );
}
