import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Radio, RadioChangeEvent, Typography } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { BlockRadioInput } from "../../../../components/ant.design/forms/BlockRadioInput";
import {
  IconArrowLeft,
  IconArrowRight,
} from "../../../../components/icons/Icons";
import { setSampleNameColumn } from "../services/importReducer";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import { ImportDispatch, ImportState } from "../store";

const { Text } = Typography;

/**
 * React component that displays Step #2 of the Sample Metadata Uploader.
 * This page is where the user selects the sample name column.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportMapHeaders(): JSX.Element {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate: NavigateFunction = useNavigate();
  const [column, setColumn] = React.useState<string>();
  const [loading, setLoading] = React.useState<boolean>(false);
  const { headers, sampleNameColumn } = useSelector(
    (state: ImportState) => state.importReducer
  );
  const dispatch: ImportDispatch = useDispatch();

  React.useEffect(() => {
    if (!column) {
      setColumn(sampleNameColumn ? sampleNameColumn : headers[0]);
    }
  }, [sampleNameColumn, headers]);

  const onSubmit = async () => {
    if (projectId && column) {
      setLoading(true);
      await dispatch(setSampleNameColumn({ projectId, column }));
      navigate(`/${projectId}/sample-metadata/upload/review`);
    }
  };

  return (
    <SampleMetadataImportWizard current={1}>
      <Text>{i18n("SampleMetadataImportMapHeaders.description")}</Text>
      <Radio.Group
        style={{ width: `100%` }}
        value={column}
        onChange={(e: RadioChangeEvent) => setColumn(e.target.value)}
      >
        {headers.map((header: String, index: number) => (
          <BlockRadioInput key={`metadata-uploader-radio-header-${index}`}>
            <Radio
              key={`metadata-uploader-radio-header-${index}`}
              value={header}
            >
              {header}
            </Radio>
          </BlockRadioInput>
        ))}
      </Radio.Group>
      <div style={{ display: "flex" }}>
        <Button
          className="t-metadata-uploader-file-button"
          icon={<IconArrowLeft />}
          onClick={() => navigate(-1)}
        >
          {i18n("SampleMetadataImportMapHeaders.button.back")}
        </Button>
        <Button
          className="t-metadata-uploader-preview-button"
          onClick={onSubmit}
          style={{ marginLeft: "auto" }}
          loading={loading}
        >
          {i18n("SampleMetadataImportMapHeaders.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}
