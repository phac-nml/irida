import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  setHeaders,
  setMetadata,
  setProjectId,
} from "../services/importReducer";
import { notification, Spin, StepsProps, Typography } from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import * as XLSX from "xlsx";
import { WorkBook } from "xlsx";
import { ImportDispatch, useImportDispatch } from "../store";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import { MetadataItem } from "../../../../apis/projects/samples";
import { RcFile, UploadFileStatus } from "antd/lib/upload/interface";

const { Text } = Typography;

/**
 * React component that displays Step #1 of the Sample Metadata Uploader.
 * This page is where the user selects the file to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportUploadFile(): JSX.Element {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate: NavigateFunction = useNavigate();
  const dispatch: ImportDispatch = useImportDispatch();
  const [status, setStatus] = React.useState<StepsProps["status"]>("process");
  const [loading, setLoading] = React.useState<boolean>(false);

  React.useEffect(() => {
    if (projectId != null) {
      dispatch(setProjectId(projectId));
    }
  }, [dispatch, projectId]);

  const options = {
    multiple: false,
    showUploadList: false,
    accept: ".xls,.xlsx,.csv",
    onChange(info: {
      file: {
        originFileObj?: RcFile;
        name?: string;
        status?: UploadFileStatus;
      };
    }) {
      const { status } = info.file;
      if (status !== "uploading") {
        setLoading(true);
        const reader = new FileReader();
        if (reader.readAsBinaryString) {
          reader.onload = () => {
            const workbook: WorkBook = XLSX.read(reader.result, {
              type: "binary",
              raw: true,
            });
            const { SheetNames } = workbook;
            const [firstSheet] = SheetNames;
            const rows: MetadataItem[] = XLSX.utils.sheet_to_json(
              workbook.Sheets[firstSheet],
              {
                rawNumbers: false,
              }
            );
            dispatch(setHeaders(Object.keys(rows[0])));
            dispatch(setMetadata(rows));
          };
          if (info.file.originFileObj) {
            reader.readAsBinaryString(info.file.originFileObj);
          }
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
          uploadText={i18n("SampleMetadataImportUploadFile.dropzone")}
          uploadHint={
            <Text strong>{i18n("SampleMetadataImportUploadFile.warning")}</Text>
          }
          options={options}
          props={{ className: "t-metadata-uploader-dropzone" }}
        />
      </Spin>
    </SampleMetadataImportWizard>
  );
}
