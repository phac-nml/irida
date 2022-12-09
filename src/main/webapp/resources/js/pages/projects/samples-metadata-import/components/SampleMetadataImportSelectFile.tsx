import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { notification, Spin, StepsProps, Typography } from "antd";
import { DragUpload } from "../../../../components/files/DragUpload";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { ImportDispatch, useImportDispatch } from "../redux/store";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import { RcFile } from "antd/lib/upload/interface";
import { setHeaders, setMetadata, setProjectId } from "../redux/importReducer";
import * as XLSX from "xlsx";
import { MetadataItem } from "../../../../apis/projects/samples";
import { ErrorAlert } from "../../../../components/alerts/ErrorAlert";

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
  const [valid, setValid] = React.useState<boolean>(true);

  React.useEffect(() => {
    if (projectId != null) {
      dispatch(setProjectId(projectId));
    }
  }, [dispatch, projectId]);

  const readFileContents = (file: Blob) => {
    const reader = new FileReader();

    return new Promise((resolve, reject) => {
      reader.onerror = () => {
        reader.abort();
        reject("Problem reading the file.");
      };

      reader.onload = () => {
        resolve(reader.result);
      };

      reader.readAsBinaryString(file);
    });
  };

  const options = {
    multiple: false,
    showUploadList: false,
    accept: ".xls,.xlsx,.csv",
    customRequest: () => {
      navigate(`/${projectId}/sample-metadata/upload/columns`);
    },
    beforeUpload: async (file: RcFile) => {
      try {
        const data = await readFileContents(file);
        const workbook: XLSX.WorkBook = XLSX.read(data, {
          type: "binary",
          raw: true,
        });
        const { SheetNames } = workbook;
        const [firstSheet] = SheetNames;
        // Not loving how I'm parsing the file twice to get the raw headers
        // See https://docs.sheetjs.com/docs/api/utilities
        const headers: string[] = XLSX.utils.sheet_to_json(
          workbook.Sheets[firstSheet],
          {
            header: 1,
          }
        )[0];
        const duplicateHeaders = headers.filter(
          (e, i, a) => a.indexOf(e) !== i
        );
        if (duplicateHeaders.length > 0) {
          setLoading(false);
          setValid(false);
          return false;
        } else {
          setValid(true);
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
          notification.success({
            message: i18n("SampleMetadataImportSelectFile.success", file.name),
          });
        }
      } catch (error) {
        setLoading(false);
        setStatus("error");
        notification.error({
          message: i18n("SampleMetadataImportSelectFile.error", file.name),
        });
        return false;
      }
      return true;
    },
  };

  return (
    <SampleMetadataImportWizard current={0} status={status}>
      <Spin spinning={loading}>
        {!valid && (
          <ErrorAlert
            message={i18n("SampleMetadataImportSelectFile.alert.valid.title")}
            description={i18n(
              "SampleMetadataImportSelectFile.alert.valid.description"
            )}
          />
        )}
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
