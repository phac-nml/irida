import { ExportTagStates } from "./NcbiUploadStateTag";

const states: ExportTagStates = {
  NEW: {
    color: undefined, // Defaults to gray
    text: i18n("ExportUploadState.NEW"),
  },
  UPLOADING: {
    color: "blue",
    text: i18n("ExportUploadState.UPLOADING"),
  },
  UPLOADED: {
    color: "cyan",
    text: i18n("ExportUploadState.UPLOADED"),
  },
  UPLOAD_ERROR: {
    color: "red",
    text: i18n("ExportUploadState.UPLOAD_ERROR"),
  },
  created: {
    color: "cyan",
    text: i18n("ExportUploadState.created"),
  },
  failed: {
    color: "red",
    text: i18n("ExportUploadState.failed"),
  },
  queued: {
    color: "cyan",
    text: i18n("ExportUploadState.queued"),
  },
  processing: {
    color: "blue",
    text: i18n("ExportUploadState.processing"),
  },
  "processed-ok": {
    color: "green",
    text: i18n("ExportUploadState.processed-ok"),
  },
  "processed-error": {
    color: "red",
    text: i18n("ExportUploadState.processed-error"),
  },
  waiting: {
    color: "cyan",
    text: i18n("ExportUploadState.waiting"),
  },
  submitted: {
    color: "cyan",
    text: i18n("ExportUploadState.submitted"),
  },
  deleted: {
    color: "volcano",
    text: i18n("ExportUploadState.DELETED"),
  },
  retired: {
    color: "gold",
    text: i18n("ExportUploadState.RETIRED"),
  },
  unknown: {
    color: "orange",
    text: i18n("ExportUploadState.unknown"),
  },
};

export default states;
