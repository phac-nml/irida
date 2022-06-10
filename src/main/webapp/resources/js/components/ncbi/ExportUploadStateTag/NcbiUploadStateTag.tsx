import { Tag } from "antd";
import React from "react";
import { ExportTagStates } from "./NcbiUploadStateTag.types";
import {TagColor} from "../../ant.design/ant.types.";
import { ExportUploadState } from "../../../types/irida/ExportUpoadState";

const states: ExportTagStates = {
  [ExportUploadState.NEW]: {
    color: TagColor.LIME,
    text: i18n("ExportUploadState.NEW"),
  },
  [ExportUploadState.UPLOADING]: {
    color: TagColor.LIME,
    text: i18n("ExportUploadState.UPLOADING"),
  },
  [ExportUploadState.UPLOADED]: {
    color: TagColor.LIME,
    text: i18n("ExportUploadState.UPLOADED"),
  },
  [ExportUploadState.UPLOADED_ERROR]: {
    color: TagColor.RED,
    text: i18n("ExportUploadState.UPLOAD_ERROR"),
  },
  [ExportUploadState.CREATED]: {
    color: TagColor.BLUE,
    text: i18n("ExportUploadState.created"),
  },
  [ExportUploadState.FAILED]: {
    color: TagColor.RED,
    text: i18n("ExportUploadState.failed"),
  },
  [ExportUploadState.QUEUED]: {
    color: TagColor.BLUE,
    text: i18n("ExportUploadState.queued"),
  },
  [ExportUploadState.PROCESSING]: {
    color: TagColor.BLUE,
    text: i18n("ExportUploadState.processing"),
  },
  [ExportUploadState.PROCESSED_OK]: {
    color: TagColor.GREEN,
    text: i18n("ExportUploadState.processed-ok"),
  },
  [ExportUploadState.PROCESSED_ERROR]: {
    color: TagColor.RED,
    text: i18n("ExportUploadState.processed-error"),
  },
  [ExportUploadState.WAITING]: {
    color: TagColor.CYAN,
    text: i18n("ExportUploadState.waiting"),
  },
  [ExportUploadState.SUBMITTED]: {
    color: TagColor.BLUE,
    text: i18n("ExportUploadState.submitted"),
  },
  [ExportUploadState.DELETED]: {
    color: TagColor.VOLCANO,
    text: "DELETED",
  },
  [ExportUploadState.RETIRED]: {
    color: TagColor.GOLD,
    text: "RETIRED",
  },
  [ExportUploadState.UNKNOWN]: {
    color: TagColor.ORANGE,
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
