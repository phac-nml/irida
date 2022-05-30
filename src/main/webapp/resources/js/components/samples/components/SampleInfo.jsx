import React from "react";
import {
  Col,
  DatePicker,
  Empty,
  List,
  notification,
  Row,
  Typography,
} from "antd";
import { useUpdateSampleDetailsMutation } from "../../../apis/samples/samples";
import { formatDate } from "../../../utilities/date-utilities";
const { Paragraph } = Typography;
import moment from "moment";
import { OntologyInput } from "../../ontology";
import { TAXONOMY } from "../../../apis/ontology/taxonomy";
import { useDispatch, useSelector } from "react-redux";
import { MetadataRolesProvider } from "../../../contexts/metadata-roles-context";
import { EditMetadata } from "./EditMetadata";
import AutoSizer from "react-virtualized-auto-sizer";
import { FixedSizeList as VList } from "react-window";
import { updateDetails } from "../sampleSlice";

const DEFAULT_HEIGHT = 600;

/**
 * React component to display basic sample information
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleInfo() {
  const { sample, modifiable: isModifiable } = useSelector(
    (state) => state.sampleReducer
  );
  const dispatch = useDispatch();
  const [updateSampleDetails] = useUpdateSampleDetailsMutation();
  const dateFormat = i18n("SampleInfo.date.format");

  /*
  Updates the field with the provided value. If nothing has
  changed then no updates are done.
   */
  const updateField = (field, value) => {
    /*
    Make sure the value actually changed, if it hasn't then don't update it.
     */
    if (sample[field] === value) return;
    if (sample[field] === null && value === "") return;

    updateSampleDetails({
      sampleId: sample.identifier,
      field,
      value: value || "",
    })
      .then((response) => {
        if (response.error) {
          notification.error({ message: response.error.data.error });
        } else {
          dispatch(updateDetails({ field, value: value || "" }));
          notification.success({ message: response.data.message });
        }
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  const detailsData = [
    {
      title: i18n("SampleInfo.sampleName"),
      value: isModifiable ? (
        <Paragraph
          editable={{ onChange: (value) => updateField("sampleName", value) }}
          className="t-sample-name"
        >
          {sample.sampleName}
        </Paragraph>
      ) : (
        <span className="t-sample-name">{sample.sampleName}</span>
      ),
    },
    {
      title: i18n("SampleInfo.description"),
      value: isModifiable ? (
        <Paragraph
          editable={{ onChange: (value) => updateField("description", value) }}
          className="t-sample-description"
        >
          {sample.description}
        </Paragraph>
      ) : (
        <span className="t-sample-description">{sample.description}</span>
      ),
    },
    {
      title: i18n("SampleInfo.id"),
      value: <span className="t-sample-identifier">{sample.identifier}</span>,
    },
    {
      title: i18n("SampleInfo.createdDate"),
      value: (
        <span className="t-sample-created-date">
          {formatDate({ date: sample.createdDate })}
        </span>
      ),
    },
    {
      title: i18n("SampleInfo.modifiedDate"),
      value: (
        <span className="t-sample-modified-date">
          {formatDate({ date: sample.modifiedDate })}
        </span>
      ),
    },
    {
      title: i18n("SampleInfo.organism"),
      value: isModifiable ? (
        <OntologyInput
          term={sample.organism}
          ontology={TAXONOMY}
          onTermSelected={(value) => updateField("organism", value)}
          className="t-sample-organism"
          autofocus={false}
        />
      ) : (
        <span className="t-sample-organism">{sample.organism}</span>
      ),
    },
    {
      title: i18n("SampleInfo.isolate"),
      value: isModifiable ? (
        <Paragraph
          editable={{ onChange: (value) => updateField("isolate", value) }}
          className="t-sample-isolate"
        >
          {sample.isolate}
        </Paragraph>
      ) : (
        <span className="t-sample-isolate">{sample.isolate}</span>
      ),
    },
    {
      title: i18n("SampleInfo.strain"),
      value: isModifiable ? (
        <Paragraph
          editable={{ onChange: (value) => updateField("strain", value) }}
          className="t-sample-strain"
        >
          {sample.strain}
        </Paragraph>
      ) : (
        <span className="t-sample-strain">{sample.strain}</span>
      ),
    },
    {
      title: i18n("SampleInfo.collectedBy"),
      value: isModifiable ? (
        <Paragraph
          editable={{ onChange: (value) => updateField("collectedBy", value) }}
          className="t-sample-collected-by"
        >
          {sample.collectedBy}
        </Paragraph>
      ) : (
        <span className="t-sample-collected-by">{sample.collectedBy}</span>
      ),
    },
    {
      title: i18n("SampleInfo.dateCollected"),
      value: isModifiable ? (
        <DatePicker
          onChange={(value) => updateField("collectionDate", value)}
          defaultValue={
            sample.collectionDate !== null
              ? moment(sample.collectionDate, dateFormat)
              : ""
          }
          format={dateFormat}
          allowClear={false}
          className="t-sample-collected-date"
        />
      ) : (
        <span className="t-sample-collected-date">{sample.collectionDate}</span>
      ),
    },
    {
      title: i18n("SampleInfo.isolationSource"),
      value: isModifiable ? (
        <Paragraph
          editable={{
            onChange: (value) => updateField("isolationSource", value),
          }}
          className="t-sample-isolation-source"
        >
          {sample.isolationSource}
        </Paragraph>
      ) : (
        <span className="t-sample-isolation-source">
          {sample.isolationSource}
        </span>
      ),
    },
    {
      title: i18n("SampleInfo.geographicLocation"),
      value: isModifiable ? (
        <Paragraph
          editable={{
            onChange: (value) => updateField("geographicLocationName", value),
          }}
          className="t-sample-geographic-location-name"
        >
          {sample.geographicLocationName}
        </Paragraph>
      ) : (
        <span className="t-sample-geographic-location-name">
          {sample.geographicLocationName}
        </span>
      ),
    },
    {
      title: i18n("SampleInfo.latitude"),
      value: isModifiable ? (
        <Paragraph
          editable={{ onChange: (value) => updateField("latitude", value) }}
          className="t-sample-latitude"
        >
          {sample.latitude}
        </Paragraph>
      ) : (
        <span className="t-sample-latitude">{sample.latitude}</span>
      ),
    },
    {
      title: i18n("SampleInfo.longitude"),
      value: isModifiable ? (
        <Paragraph
          editable={{ onChange: (value) => updateField("longitude", value) }}
          className="t-sample-longitude"
        >
          {sample.longitude}
        </Paragraph>
      ) : (
        <span className="t-sample-longitude">{sample.longitude}</span>
      ),
    },
  ];

  const renderDetailsListItem = ({ index, style }) => {
    const item = detailsData[index];

    return (
      <List.Item style={{ ...style, padding: 15 }}>
        <List.Item.Meta title={item.title} description={item.value} />
      </List.Item>
    );
  };

  return (
    <Row gutter={16}>
      <Col
        span={24}
        style={{
          height: DEFAULT_HEIGHT,
        }}
      >
        {detailsData.length ? (
          <>
            <AutoSizer>
              {({ height = DEFAULT_HEIGHT, width = "100%" }) => (
                <VList
                  itemCount={detailsData.length}
                  itemSize={70}
                  height={height}
                  width={width}
                >
                  {renderDetailsListItem}
                </VList>
              )}
            </AutoSizer>
            <MetadataRolesProvider>
              <EditMetadata />
            </MetadataRolesProvider>
          </>
        ) : (
          <Empty description={i18n("SampleInfo.noDetailsAvailable")} />
        )}
      </Col>
    </Row>
  );
}
