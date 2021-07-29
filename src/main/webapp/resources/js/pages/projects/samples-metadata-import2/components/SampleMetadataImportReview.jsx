import React from "react";
import { navigate } from "@reach/router"
import {
  Button,
  Typography,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";

const { Text } = Typography

function Back() {
  navigate(-1);
}

/**
 * React component that displays Step #3 of the Sample Metadata Uploader.
 * This page is where the user reviews the metadata to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportReview({ projectId }) {

  return (
    <SampleMetadataImportWizard currentStep={2}>
      <Text>
        {i18n("SampleMetadataImportReview.description")}
      </Text>
      <Text>
        Hello World!
      </Text>
      <Button onClick={() => Back()}> {i18n("SampleMetadataImportReview.back")}</Button>
    </SampleMetadataImportWizard>
  );
}