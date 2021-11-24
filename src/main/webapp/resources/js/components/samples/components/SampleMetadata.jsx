import React from "react";
import { Button, Empty, List, notification, Typography } from "antd";
import { AddNewMetadata } from "./AddNewMetadata";
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

  const updateMetadata = (fieldId, value) => {
    console.log(fieldId);
    console.log(value);
  };

  const removeMetadata = (field, entryId) => {
    removeSampleMetadata({
      field,
      entryId,
    })
      .then(({ data }) => {
        notification.success({ message: data.message });
        refetch();
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  return (
    <>
      {isModifiable && (
        <AddNewMetadata sampleId={sampleId} refetch={refetch}>
          <Button style={{ marginLeft: "15px" }}>Add New Metadata</Button>
        </AddNewMetadata>
      )}
      <div>
        {!isLoading ? (
          Object.keys(data.metadata).length ? (
            <StyledListMetadata
              itemLayout="horizontal"
              dataSource={data.metadata
                .slice()
                .sort((a, b) =>
                  a.metadataTemplateField.localeCompare(b.metadataTemplateField)
                )}
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
                      onClick={() =>
                        removeMetadata(item.metadataTemplateField, item.entryId)
                      }
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
