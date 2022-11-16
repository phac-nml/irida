import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { setHeaders, setMetadata, setProjectId } from "../redux/importReducer";
import { notification, Spin, StepsProps, Typography } from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import * as XLSX from "xlsx";
import { WorkBook } from "xlsx";
import { ImportDispatch, useImportDispatch } from "../redux/store";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import { MetadataItem } from "../../../../apis/projects/samples";
import { RcFile } from "antd/lib/upload/interface";

const { Text } = Typography;

/**
 * React component that displays Step #1 of the Sample Metadata Uploader.
 * This page is where the user selects the file to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportSelectFile(): JSX.Element {
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
    beforeUpload: (file: RcFile) => {
      if (file) {
        setLoading(true);
        const reader = new FileReader();
        if (reader.readAsBinaryString) {
          reader.onload = async () => {
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
            const cleanRows = JSON.parse(
              JSON.stringify(rows).replace(/"\s+|\s+"/g, '"')
            );
            await dispatch(setHeaders({ headers: Object.keys(cleanRows[0]) }));
            await dispatch(setMetadata(cleanRows));
          };
          reader.readAsBinaryString(file);
          notification.success({
            message: i18n("SampleMetadataImportSelectFile.success", file.name),
          });
          navigate(`/${projectId}/sample-metadata/upload/columns`);
        }
      } else {
        setLoading(false);
        setStatus("error");
        notification.error({
          message: i18n("SampleMetadataImportSelectFile.error", file.name),
        });
      }
      return false;
    },
  };

  return (
    <SampleMetadataImportWizard current={0} status={status}>
      <Spin spinning={loading}>
        <DragUpload
          uploadText={i18n("SampleMetadataImportSelectFile.dropzone")}
          uploadHint={
            <Text strong>{i18n("SampleMetadataImportSelectFile.warning")}</Text>
          }
          options={options}
          props={{ className: "t-metadata-uploader-dropzone" }}
        />
      </Spin>
    </SampleMetadataImportWizard>
  );
}
