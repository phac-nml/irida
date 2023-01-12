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
import { ErrorAlert } from "../../../../components/alerts/ErrorAlert";
import { SPACE_XS } from "../../../../styles/spacing";
import { MetadataItem } from "../../../../apis/projects/samples";

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
  const [showEmptyHeaderError, setShowEmptyHeaderError] =
    React.useState<boolean>(false);
  const [showDuplicateHeaderError, setShowDuplicateHeaderError] =
    React.useState<boolean>(false);

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
        setLoading(true);
        setShowEmptyHeaderError(false);
        setShowDuplicateHeaderError(false);

        const data = await readFileContents(file);
        const workbook: XLSX.WorkBook = XLSX.read(data, {
          type: "binary",
          raw: true,
        });
        const { SheetNames } = workbook;
        const [firstSheet] = SheetNames;
        const rows = XLSX.utils.sheet_to_json(workbook.Sheets[firstSheet], {
          rawNumbers: false,
          header: 1,
        });
        const cleanRows: string[][] = JSON.parse(
          JSON.stringify(rows).replace(/"\s+|\s+"/g, '"')
        );
        const headers = cleanRows.shift();
        const duplicateHeaders = headers?.filter(
          (header, index, headers) => headers.indexOf(header) !== index
        );
        const emptyHeaders = headers?.filter((header) => header === null);

        if (
          headers === undefined ||
          headers.length === 0 ||
          (emptyHeaders && emptyHeaders.length > 0) ||
          (duplicateHeaders && duplicateHeaders.length > 0)
        ) {
          if (
            headers === undefined ||
            headers.length === 0 ||
            (emptyHeaders && emptyHeaders.length > 0)
          ) {
            setShowEmptyHeaderError(true);
          }
          if (duplicateHeaders && duplicateHeaders.length > 0) {
            setShowDuplicateHeaderError(true);
          }
          setLoading(false);
          return false;
        } else {
          const output: MetadataItem[] = cleanRows.map((row, rowIndex) => {
            const metadataItem: MetadataItem = {
              rowKey: `metadata-uploader-row-${rowIndex}`,
            };
            row.forEach((item, itemIndex) => {
              if (item) metadataItem[headers[itemIndex]] = item;
            });
            return metadataItem;
          });
          await dispatch(setHeaders({ headers: headers }));
          await dispatch(setMetadata(output));
          notification.success({
            message: i18n("SampleMetadataImportSelectFile.success", file.name),
          });
          return true;
        }
      } catch (error) {
        setLoading(false);
        setStatus("error");
        notification.error({
          message: i18n("SampleMetadataImportSelectFile.error", file.name),
        });
        return false;
      }
    },
  };

  return (
    <SampleMetadataImportWizard current={0} status={status}>
      <Spin spinning={loading}>
        {(showEmptyHeaderError || showDuplicateHeaderError) && (
          <ErrorAlert
            style={{ marginBottom: SPACE_XS }}
            message={i18n("SampleMetadataImportSelectFile.alert.valid.title")}
            description={
              <>
                {i18n(
                  "SampleMetadataImportSelectFile.alert.valid.description.preface"
                )}
                <ul style={{ marginBottom: 0 }}>
                  {showDuplicateHeaderError && (
                    <li>
                      {i18n(
                        "SampleMetadataImportSelectFile.alert.valid.description.duplicate"
                      )}
                    </li>
                  )}
                  {showEmptyHeaderError && (
                    <li>
                      {i18n(
                        "SampleMetadataImportSelectFile.alert.valid.description.empty"
                      )}
                    </li>
                  )}
                </ul>
                {i18n(
                  "SampleMetadataImportSelectFile.alert.valid.description.postface"
                )}
              </>
            }
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
