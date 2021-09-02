import React from "react";
import { List, Space, Typography } from "antd";
import { CalendarDate } from "../../CalendarDate";

const { Paragraph } = Typography;

/**
 * React component to display basic sample information
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleInfo({ sample }) {
  const data = [
    {
      title: i18n("SampleInfo.sampleName"),
      value: (
        <Paragraph
          editable={{
            onChange: (value) => console.log(value),
          }}
        >
          {sample.sampleName}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.description"),
      value: (
        <Paragraph
          editable={{
            onChange: (value) => console.log(value),
          }}
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
          editable={{
            onChange: (value) => console.log(value),
          }}
        >
          {sample.organism}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.isolate"),
      value: (
        <Paragraph
          editable={{
            onChange: (value) => console.log(value),
          }}
        >
          {sample.isolate}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.strain"),
      value: (
        <Paragraph
          editable={{
            onChange: (value) => console.log(value),
          }}
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
          editable={{
            onChange: (value) => console.log(value),
          }}
        >
          {sample.collectedBy}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.dateCollected"),
      value: (
        <Paragraph
          editable={{
            onChange: (value) => console.log(value),
          }}
        >
          {sample.collectionDate}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.isolationSource"),
      value: (
        <Paragraph
          editable={{
            onChange: (value) => console.log(value),
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
            onChange: (value) => console.log(value),
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
          editable={{
            onChange: (value) => console.log(value),
          }}
        >
          {sample.latitude}
        </Paragraph>
      ),
    },
    {
      title: i18n("SampleInfo.longitude"),
      value: (
        <Paragraph
          editable={{
            onChange: (value) => console.log(value),
          }}
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
