import React from "react";
import { Button, Empty, List, Typography } from "antd";
import { AddNewMetadata } from "./AddNewMetadata";
import { MetadataRolesProvider } from "../../../contexts/metadata-roles-context";
import {
  useGetSampleMetadataQuery,
  useUpdateSampleMetadataMutation,
  useRemoveSampleMetadataMutation,
} from "../../../apis/samples/samples";
import { ContentLoading } from "../../loader";
import { IconRemove } from "../../icons/Icons";
const { Paragraph } = Typography;
import styled from "styled-components";

const StyledListMetadata = styled(List)`
  .ant-list-item {
    padding: 15px;
    div.ant-typography,
    .ant-typography p {
      margin-bottom: 0;
    }
    .ant-typography.ant-typography-edit-content {
      margin: 0;
    }
  }
`;
/**
 * React component to display metadata associated with a sample
 *
 * @param sampleId The sample identifier
 * @param isModifiable If the current user can modify the sample or not
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleMetadata({ sampleId, isModifiable }) {
  const { data = {}, isLoading, refetch } = useGetSampleMetadataQuery(sampleId);
  const [removeSampleMetadata] = useRemoveSampleMetadataMutation();
  const [updateSampleMetadata] = useUpdateSampleMetadataMutation();

  console.log(data);

  const updateMetadata = (fieldId, value) => {
    console.log(fieldId);
    console.log(value);
  };

  const removeMetadata = (fieldId) => {
    console.log(fieldId);
  };

  return (
    <>
      {isModifiable && (
        <MetadataRolesProvider>
          <AddNewMetadata sampleId={sampleId} refetch={refetch}>
            <Button>Add New Metadata</Button>
          </AddNewMetadata>
        </MetadataRolesProvider>
      )}
      <div>
        {!isLoading ? (
          Object.keys(data).length ? (
            <StyledListMetadata
              itemLayout="horizontal"
              dataSource={data}
              renderItem={(item) => (
                <List.Item className="t-sample-details-metadata-item">
                  <List.Item.Meta
                    title={
                      <span className="t-sample-details-metadata__field">
                        {item.metadataTemplateField}
                      </span>
                    }
                    description={
                      <Paragraph
                        editable={{
                          onChange: (e) =>
                            updateMetadata(item.fieldId, item.metadataEntry),
                        }}
                        className="t-sample-details-metadata__entry"
                      >
                        {item.metadataEntry}
                      </Paragraph>
                    }
                  />
                  <div>
                    <Button
                      shape="circle"
                      icon={<IconRemove />}
                      onClick={() => removeMetadata(item.fieldId)}
                      value={item.fieldId}
                    ></Button>
                  </div>
                </List.Item>
              )}
            />
          ) : (
            <Empty description={i18n("SampleDetails.no-metadata")} />
          )
        ) : (
          <ContentLoading />
        )}
      </div>
    </>
  );
}
