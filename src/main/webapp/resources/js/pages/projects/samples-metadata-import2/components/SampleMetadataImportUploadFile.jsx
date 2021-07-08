import React from "react";
import { useDispatch } from "react-redux";
import { setHeaders } from "../services/rootReducer"
import { navigate } from "@reach/router"
import {
  notification,
  Typography,
} from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";

const { Text } = Typography

/**
 * React component that displays Step #1 of the Sample Metadata Uploader.
 * This page is where the user selects the file to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportUploadFile({ projectId }) {
  const dispatch = useDispatch();

  const options = {
    multiple: false,
    showUploadList: false,
    accept: [".xls", ".xlsx", ".csv"],
    action: setBaseUrl(`/ajax/projects/sample-metadata/upload/file?projectId=${projectId}`),
    onChange(info) {
      const { status } = info.file;
      if (status === 'done') {
        notification.success({
          message: i18n("SampleMetadataImportUploadFile.success", info.file.name),
        });
        dispatch(setHeaders(info.file.response.headers, info.file.response.sampleNameColumn));
        navigate('headers');
      } else if (status === 'error') {
        notification.error({
          message: i18n("SampleMetadataImportUploadFile.error", info.file.name),
        });
      }
    },
  }

  return (
    <SampleMetadataImportWizard currentStep={0}>
      <DragUpload
        className="t-sample-metadata-file-uploader"
        uploadText={i18n("SampleMetadataImportUploadFile.dropzone")}
        uploadHint={<Text strong>{i18n("SampleMetadataImportUploadFile.warning")}</Text>}
        options={options}
      />
    </SampleMetadataImportWizard>
  );
}
