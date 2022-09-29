import React from "react";
import { useDispatch } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { setHeaders, setMetadata } from "../services/importReducer";
import { notification, Spin, StepsProps, Typography, UploadProps } from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import * as XLSX from "xlsx";
import { WorkBook } from "xlsx";

const { Text } = Typography;

/**
 * React component that displays Step #1 of the Sample Metadata Uploader.
 * This page is where the user selects the file to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportUploadFile(): JSX.Element {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [status, setStatus] = React.useState<StepsProps["status"]>("process");
  const [loading, setLoading] = React.useState<boolean>(false);

  const options: UploadProps = {
    multiple: false,
    showUploadList: false,
    accept: ".xls,.xlsx,.csv",
    onChange(info: {
      file: { originFileObj?: any; name?: any; status?: any };
    }) {
      const { status } = info.file;
      if (status !== "uploading") {
        setLoading(true);
        let reader = new FileReader();
        if (reader.readAsBinaryString) {
          reader.onload = (e) => {
            const workbook: WorkBook = XLSX.read(reader.result, {
              type: "binary",
              raw: true,
            });
            const firstSheet: string = workbook.SheetNames[0];
            const rows: any[] = XLSX.utils.sheet_to_json(
              workbook.Sheets[firstSheet],
              {
                rawNumbers: false,
              }
            );
            dispatch(setHeaders(Object.keys(rows[0])));
            dispatch(setMetadata(rows));
          };
          reader.readAsBinaryString(info.file.originFileObj);
        }
      }
      if (status === "done") {
        notification.success({
          message: i18n(
            "SampleMetadataImportUploadFile.success",
            info.file.name
          ),
        });
        navigate(`/${projectId}/sample-metadata/upload/headers`);
      } else if (status === "error") {
        setLoading(false);
        setStatus("error");
        notification.error({
          message: i18n("SampleMetadataImportUploadFile.error", info.file.name),
        });
      }
    },
  };

  return (
    <SampleMetadataImportWizard current={0} status={status}>
      <Spin spinning={loading}>
        <DragUpload
          className="t-metadata-uploader-dropzone"
          uploadText={i18n("SampleMetadataImportUploadFile.dropzone")}
          uploadHint={
            <Text strong>{i18n("SampleMetadataImportUploadFile.warning")}</Text>
          }
          options={options}
        />
      </Spin>
    </SampleMetadataImportWizard>
  );
}
