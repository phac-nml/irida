import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Radio, Typography } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { BlockRadioInput } from "../../../../components/ant.design/forms/BlockRadioInput";
import {
  IconArrowLeft,
  IconArrowRight,
} from "../../../../components/icons/Icons";
import { setSampleNameColumn } from "../services/importReducer";

const { Text } = Typography;

/**
 * React component that displays Step #2 of the Sample Metadata Uploader.
 * This page is where the user selects the sample name column.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportMapHeaders() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [column, setColumn] = React.useState();
  const { headers, sampleNameColumn } = useSelector((state) => state.reducer);
  const dispatch = useDispatch();

  React.useEffect(() => {
    if (!column) {
      setColumn(sampleNameColumn ? sampleNameColumn : headers[0]);
    }
  }, [sampleNameColumn, headers]);

  const onSubmit = () => {
    dispatch(setSampleNameColumn(column));
    navigate(`/${projectId}/sample-metadata/upload/review`);
  };

  return (
    <SampleMetadataImportWizard currentStep={1}>
      <Text>{i18n("SampleMetadataImportMapHeaders.description")}</Text>
      <Radio.Group
        style={{ width: `100%` }}
        value={column}
        onChange={(e) => setColumn(e.target.value)}
      >
        {headers.map((header, index) => (
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
        >
          {i18n("SampleMetadataImportMapHeaders.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}
