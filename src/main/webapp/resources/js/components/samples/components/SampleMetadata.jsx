import React from "react";
import { Button, Empty, List, notification, Popconfirm, Space } from "antd";
import { AddNewMetadata } from "./AddNewMetadata";
import {
  useGetSampleMetadataQuery,
  useRemoveSampleMetadataMutation,
} from "../../../apis/samples/samples";
import { ContentLoading } from "../../loader";
import { IconEdit, IconPlusCircle, IconRemove } from "../../icons/Icons";
import { MetadataRolesProvider } from "../../../contexts/metadata-roles-context";
import { EditMetadata } from "./EditMetadata";
import AutoSizer from "react-virtualized-auto-sizer";
import { FixedSizeList as VList } from "react-window";
import { useDispatch, useSelector } from "react-redux";
import {
  removeSampleMetadataField,
  setEditSampleMetadata,
  setSampleMetadata,
} from "../sampleSlice";

const DEFAULT_HEIGHT = 600;

/**
 * React component to display metadata associated with a sample
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleMetadata() {
  const { sample, modifiable: isModifiable, projectId } = useSelector(
    (state) => state.sampleReducer
  );

  const {
    data = {},
    isLoading,
    refetch: refetchSampleMetadata,
  } = useGetSampleMetadataQuery({
    sampleId: sample.identifier,
    projectId: projectId,
  });
  const [removeSampleMetadata] = useRemoveSampleMetadataMutation();
  const dispatch = useDispatch();

  React.useEffect(() => {
    if (!isLoading) {
      dispatch(setSampleMetadata(data.metadata));
    }
  }, [data]);

  const removeMetadata = (field, entryId) => {
    removeSampleMetadata({
      field,
      entryId,
    })
      .then(({ data }) => {
        notification.success({ message: data.message });
        dispatch(removeSampleMetadataField({ field, entryId }));
        refetchSampleMetadata();
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  const renderMetadataFieldListItem = ({ index, style }) => {
    const item = data.metadata[index];
    return (
      <List.Item
        className="t-sample-details-metadata-item"
        style={{ ...style, paddingRight: "15px" }}
      >
        <List.Item.Meta
          title={
            <span className="t-sample-details-metadata__field">
              {item.metadataTemplateField}
            </span>
          }
          description={
            <span className="t-sample-details-metadata__entry">
              {item.metadataEntry}
            </span>
          }
        />
        {isModifiable && (
          <Space size="small" direction="horizontal">
            <Button
              shape="circle"
              icon={
                <IconEdit
                  onClick={() => {
                    dispatch(
                      setEditSampleMetadata({
                        editModalVisible: true,
                        field: item.metadataTemplateField,
                        fieldId: item.fieldId,
                        entryId: item.entryId,
                        entry: item.metadataEntry,
                        restriction: item.metadataRestriction,
                      })
                    );
                  }}
                />
              }
            />
            <Popconfirm
              placement={"topRight"}
              title={i18n(
                "SampleMetadata.remove.confirm",
                item.metadataTemplateField
              )}
              onConfirm={() =>
                removeMetadata(item.metadataTemplateField, item.entryId)
              }
              okText="Confirm"
            >
              <Button shape="circle" icon={<IconRemove />} />
            </Popconfirm>
          </Space>
        )}
      </List.Item>
    );
  };

  return (
    <>
      {isModifiable && (
        <MetadataRolesProvider>
          <AddNewMetadata refetch={refetchSampleMetadata}>
            <Button icon={<IconPlusCircle />}>
              {i18n("SampleMetadata.addNewMetadata")}
            </Button>
          </AddNewMetadata>
        </MetadataRolesProvider>
      )}
      <div
        style={{
          height: DEFAULT_HEIGHT,
          width: "100%",
        }}
      >
        {!isLoading ? (
          data.metadata.length ? (
            <>
              <AutoSizer>
                {({ height = DEFAULT_HEIGHT, width = "100%" }) => (
                  <VList
                    itemCount={data.metadata.length}
                    itemSize={75}
                    height={height}
                    width={width}
                  >
                    {renderMetadataFieldListItem}
                  </VList>
                )}
              </AutoSizer>
              <MetadataRolesProvider>
                <EditMetadata
                  sampleId={sample.identifier}
                  projectId={projectId}
                  refetch={refetchSampleMetadata}
                ></EditMetadata>
              </MetadataRolesProvider>
            </>
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
