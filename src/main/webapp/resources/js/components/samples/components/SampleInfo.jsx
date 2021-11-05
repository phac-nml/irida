import React from "react";
import { DatePicker, List, notification, Space, Typography } from "antd";
import { CalendarDate } from "../../CalendarDate";
import { useUpdateSampleDetailsMutation } from "../../../apis/samples/samples";

const { Paragraph } = Typography;

/**
 * React component to display basic sample information
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleInfo({ sample }) {
  const [updateSampleDetails] = useUpdateSampleDetailsMutation();

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
          notification.success({ message: response.data.message });
        }
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  const data = [
    {
      title: i18n("SampleInfo.sampleName"),
      value: (
        <Paragraph
          editable={{ onChange: (value) => updateField("sampleName", value) }}
        >
          {sample.sampleName}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.description"),
      value: (
        <Paragraph
          editable={{ onChange: (value) => updateField("description", value) }}
        >
          {sample.description}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.id"),
      value: sample.identifier,
    },
    {
      title: i18n("SampleInfo.createdDate"),
      value: <CalendarDate date={sample.createdDate} />,
    },
    {
      title: i18n("SampleInfo.modifiedDate"),
      value: <CalendarDate date={sample.modifiedDate} />,
    },
  ];

  const organismData = [
    {
      title: i18n("SampleInfo.organism"),
      value: (
        <Paragraph
          editable={{ onChange: (value) => updateField("organism", value) }}
        >
          {sample.organism}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.isolate"),
      value: (
        <Paragraph
          editable={{ onChange: (value) => updateField("isolate", value) }}
        >
          {sample.isolate}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.strain"),
      value: (
        <Paragraph
          editable={{ onChange: (value) => updateField("strain", value) }}
        >
          {sample.strain}
        </Paragraph>
      ),
    },
  ];

  const collectionData = [
    {
      title: i18n("SampleInfo.collectedBy"),
      value: (
        <Paragraph
          editable={{ onChange: (value) => updateField("collectedBy", value) }}
        >
          {sample.collectedBy}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.dateCollected"),
      value: (
        <DatePicker
          onChange={(value) => updateField("collectionDate", value)}
          placeholder={sample.collectionDate}
        />
      ),
    },
    {
      title: i18n("SampleInfo.isolationSource"),
      value: (
        <Paragraph
          editable={{
            onChange: (value) => updateField("isolationSource", value),
          }}
        >
          {sample.isolationSource}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.geographicLocation"),
      value: (
        <Paragraph
          editable={{
            onChange: (value) => updateField("geographicLocationName", value),
          }}
        >
          {sample.geographicLocationName}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.latitude"),
      value: (
        <Paragraph
          editable={{ onChange: (value) => updateField("latitude", value) }}
        >
          {sample.latitude}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.longitude"),
      value: (
        <Paragraph
          editable={{ onChange: (value) => updateField("longitude", value) }}
        >
          {sample.longitude}
        </Paragraph>
      ),
    },
  ];

  return (
    <Space size={`large`} direction={`vertical`} style={{ width: `100%` }}>
      <List
        itemLayout="horizontal"
        dataSource={data}
        renderItem={(item) => (
          <List.Item>
            <List.Item.Meta
              title={item.title}
              description={
                <span style={{ marginLeft: "10px" }}>{item.value}</span>
              }
            />
          </List.Item>
        )}
      />

      <List
        itemLayout="horizontal"
        dataSource={organismData}
        renderItem={(item) => (
          <List.Item>
            <List.Item.Meta title={item.title} description={item.value} />
          </List.Item>
        )}
      />

      <List
        itemLayout="horizontal"
        dataSource={collectionData}
        renderItem={(item) => (
          <List.Item>
            <List.Item.Meta title={item.title} description={item.value} />
          </List.Item>
        )}
      />
    </Space>
  );
}
