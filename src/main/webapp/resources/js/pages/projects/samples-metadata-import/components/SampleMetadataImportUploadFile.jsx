import React from "react";
import { useDispatch } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { setHeaders } from "../services/importReducer";
import { notification, Typography } from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { useClearProjectSampleMetadataMutation } from "../../../../apis/metadata/metadata-import";
import * as XLSX from "xlsx";

const { Text } = Typography;

function processFile(data) {
  const workbook = XLSX.read(data, { type: "binary", raw: true });
  const firstSheet = workbook.SheetNames[0];
  const rows = XLSX.utils.sheet_to_row_object_array(
    workbook.Sheets[firstSheet]
  );
  console.log(rows);
}

/**
 * React component that displays Step #1 of the Sample Metadata Uploader.
 * This page is where the user selects the file to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportUploadFile() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [status, setStatus] = React.useState("process");
  const [clearStorage] = useClearProjectSampleMetadataMutation();

  React.useEffect(() => {
    clearStorage(projectId);
  }, [clearStorage, projectId]);

  const options = {
    multiple: false,
    showUploadList: false,
    accept: [".xls", ".xlsx", ".csv"],
    // action: setBaseUrl(
    //   `/ajax/projects/sample-metadata/upload/file?projectId=${projectId}`
    // ),
    onChange(info) {
      const { status } = info.file;
      if (info.file.status !== "uploading") {
        let reader = new FileReader();
        if (reader.readAsBinaryString) {
          reader.onload = (e) => {
            processFile(reader.result);
          };
          reader.readAsBinaryString(info.file.originFileObj);
        }
        return false;
      }
      if (status === "done") {
        notification.success({
          message: i18n(
            "SampleMetadataImportUploadFile.success",
            info.file.name
          ),
        });
        dispatch(
          setHeaders(
            info.file.response.headers,
            info.file.response.sampleNameColumn
          )
        );
        navigate(`/${projectId}/sample-metadata/upload/headers`);
      } else if (status === "error") {
        setStatus("error");
        notification.error({
          message: i18n("SampleMetadataImportUploadFile.error", info.file.name),
        });
      }
    },
  };

  return (
    <SampleMetadataImportWizard currentStep={0} currentStatus={status}>
      <DragUpload
        className="t-metadata-uploader-dropzone"
        uploadText={i18n("SampleMetadataImportUploadFile.dropzone")}
        uploadHint={
          <Text strong>{i18n("SampleMetadataImportUploadFile.warning")}</Text>
        }
        options={options}
      />
    </SampleMetadataImportWizard>
  );
}
