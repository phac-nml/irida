import React from "react";
import { navigate } from "@reach/router"
import {
  Button,
  Radio,
  Typography,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { useSelector } from "react-redux";
import { BlockRadioInput } from "../../../../components/ant.design/forms/BlockRadioInput";

const { Text } = Typography

function Back() {
  navigate(-1);
}

/**
 * React component that displays Step #2 of the Sample Metadata Uploader.
 * This page is where the user selects the sample name column.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportMapHeaders({ projectId }) {

  const { headers, sampleNameColumn } = useSelector((state) => state.reducer);

  return (
    <SampleMetadataImportWizard currentStep={1}>
      <Text>
        {i18n("SampleMetadataImportMapHeaders.description")}
      </Text>
      <Radio.Group style={{ width: `100%` }} defaultValue={sampleNameColumn}>
        {headers.map((header, index) => (
          <BlockRadioInput key={`radio-item-header-${index}`}>
            <Radio key={`radio-header-${index}`} value={header}>
              {header}
            </Radio>
          </BlockRadioInput>
        ))}
      </Radio.Group>
      <Button onClick={() => Back()}> {i18n("SampleMetadataImportMapHeaders.back")}</Button>
    </SampleMetadataImportWizard>
  );
}