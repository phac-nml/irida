import React from "react";
import {
  Button,
  Col,
  Empty,
  List,
  notification,
  Popconfirm,
  Row,
  Space,
} from "antd";
import { AddNewMetadata } from "./AddNewMetadata";
import { useRemoveSampleMetadataMutation } from "../../../apis/samples/samples";
import { ContentLoading } from "../../loader";
import { IconPlusCircle } from "../../icons/Icons";
import { MetadataRolesProvider } from "../../../contexts/metadata-roles-context";
import { EditMetadata } from "./EditMetadata";
import AutoSizer from "react-virtualized-auto-sizer";
import { FixedSizeList as VList } from "react-window";
import { RootStateOrAny, useDispatch, useSelector } from "react-redux";
import {
  fetchSampleMetadata,
  removeSampleMetadataField,
  setEditSampleMetadata,
} from "../sampleSlice";

const DEFAULT_HEIGHT = 600;

/**
 * React component to display metadata associated with a sample
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleMetadata() {
  const {
    sample,
    modifiable: isModifiable,
    projectId,
    metadata,
    loading,
  } = useSelector((state: RootStateOrAny) => state.sampleReducer);

  const [removeSampleMetadata] = useRemoveSampleMetadataMutation();
  const dispatch = useDispatch();

  React.useEffect(() => {
    dispatch(
      fetchSampleMetadata({
        sampleId: sample.identifier,
        projectId,
      })
    );
  }, []);

  const removeMetadata = (field: string, entryId: number) => {
    removeSampleMetadata({
      projectId,
      field,
      entryId,
    })
      .then(({ data }) => {
        notification.success({ message: data.message });
        dispatch(removeSampleMetadataField({ field, entryId }));
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  const renderMetadataFieldListItem = ({ index, style }: ListStyles) => {
    const concatenatedStyle = { ...style, paddingright: 15 };
    const item = metadata[index];
    return (
      <List.Item
        className="t-sample-details-metadata-item"
        {...concatenatedStyle}
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
              type="link"
              style={{ padding: 0 }}
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
            >
              {i18n("SampleMetadata.button.edit")}
            </Button>
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
              <Button type="link" style={{ padding: 0 }}>
                {i18n("SampleMetadata.button.remove")}
              </Button>
            </Popconfirm>
          </Space>
        )}
      </List.Item>
    );
  };

  return (
    <Row gutter={[16, 16]}>
      {isModifiable && (
        <Col>
          <MetadataRolesProvider>
            <AddNewMetadata>
              <Button
                icon={<IconPlusCircle />}
                className="t-add-new-metadata-btn"
              >
                {i18n("SampleMetadata.addNewMetadata")}
              </Button>
            </AddNewMetadata>
          </MetadataRolesProvider>
        </Col>
      )}
      <Col
        span={24}
        style={{
          height: DEFAULT_HEIGHT,
        }}
      >
        {!loading ? (
          metadata.length ? (
            <>
              <AutoSizer>
                {({ height = DEFAULT_HEIGHT, width = "100%" }) => (
                  <VList
                    itemCount={metadata.length}
                    itemSize={75}
                    height={height}
                    width={width}
                  >
                    {renderMetadataFieldListItem}
                  </VList>
                )}
              </AutoSizer>
              <MetadataRolesProvider>
                <EditMetadata />
              </MetadataRolesProvider>
            </>
          ) : (
            <Empty description={i18n("SampleDetails.no-metadata")} />
          )
        ) : (
          <ContentLoading />
        )}
      </Col>
    </Row>
  );
}
