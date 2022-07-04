import { TagColor } from "../../../types/ant-design";
import { ExportUploadState } from "../../../types/irida/ExportUpoadState";
import { ExportTagStates } from "./NcbiUploadStateTag";

const states: ExportTagStates = {
  [ExportUploadState.NEW]: {
    color: undefined, // Defaults to gray
    text: i18n("ExportUploadState.NEW"),
  },
  [ExportUploadState.UPLOADING]: {
    color: TagColor.BLUE,
    text: i18n("ExportUploadState.UPLOADING"),
  },
  [ExportUploadState.UPLOADED]: {
    color: TagColor.CYAN,
    text: i18n("ExportUploadState.UPLOADED"),
  },
  [ExportUploadState.UPLOADED_ERROR]: {
    color: TagColor.RED,
    text: i18n("ExportUploadState.UPLOAD_ERROR"),
  },
  [ExportUploadState.CREATED]: {
    color: TagColor.CYAN,
    text: i18n("ExportUploadState.created"),
  },
  [ExportUploadState.FAILED]: {
    color: TagColor.RED,
    text: i18n("ExportUploadState.failed"),
  },
  [ExportUploadState.QUEUED]: {
    color: TagColor.CYAN,
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
    color: TagColor.CYAN,
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

export default states;
