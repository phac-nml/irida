import React from "react";
import { Button, Empty, List } from "antd";
import { AddNewMetadata } from "./AddNewMetadata";
import { MetadataRolesProvider } from "../../../contexts/metadata-roles-context";
import { useGetSampleMetadataQuery } from "../../../apis/samples/samples";
import { ContentLoading } from "../../loader";

/**
 * React component to display metadata associated with a sample
 *
 * @param sampleId The sample identifier
 * @param isModifiable If the current user can modify the sample or not
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleMetadata({ sampleId, isModifiable }) {
  const { data = {}, isLoading } = useGetSampleMetadataQuery(sampleId);

  return (
    <>
      {isModifiable && (
        <MetadataRolesProvider>
          <AddNewMetadata sampleId={sampleId}>
            <Button>Add New Metadata</Button>
          </AddNewMetadata>
        </MetadataRolesProvider>
      )}
      <div>
        {!isLoading ? (
          Object.keys(data.metadata).length ? (
            <List
              itemLayout="horizontal"
              dataSource={Object.keys(data.metadata).sort((a, b) =>
                a.localeCompare(b)
              )}
              renderItem={(item) => (
                <List.Item className="t-sample-details-metadata-item">
                  <List.Item.Meta
                    title={
                      <span className="t-sample-details-metadata__field">
                        {item}
                      </span>
                    }
                    description={
                      <span className="t-sample-details-metadata__entry">
                        {data.metadata[item].value}
                      </span>
                    }
                  />
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
