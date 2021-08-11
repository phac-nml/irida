import React from "react";
import { navigate } from "@reach/router"
import {
  Button,
  Space,
  Table,
  Tag,
  Typography,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { useGetProjectSampleMetadataQuery } from "../../../../apis/metadata/metadata-import";
import { IconArrowLeft, IconArrowRight } from "../../../../components/icons/Icons";

const { Text } = Typography

/**
 * React component that displays Step #3 of the Sample Metadata Uploader.
 * This page is where the user reviews the metadata to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportReview({ projectId }) {
  const { data } = useGetProjectSampleMetadataQuery(projectId);

  const fileColumns = data?.headers?.map((header) => {
    let item = { title: header, dataIndex: header, render: (text, item) => (<>{item.entry[header]}</>) };
    return item;
  });

  const tagColumn = { title: '', dataIndex: 'tags', fixed: 'right', width: 70, render: (text, item) => { if (!item.foundSampleId) return (<Tag color="green">New</Tag>) } };

  const columns = fileColumns?.concat(tagColumn);

  const dataSource = data?.rows?.map((item, index) => {
    let newItem = { ...item, key: index };
    return newItem;
  });

  const rowSelection = {
    fixed: true,
    onChange: (selectedRowKeys, selectedRows) => {
      console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    },
  };

  return (
    <SampleMetadataImportWizard currentStep={2}>
      <Text>
        {i18n("SampleMetadataImportReview.description")}
      </Text>

      <Table rowSelection={rowSelection} columns={columns} dataSource={dataSource} scroll={{ x: 1500 }} />

      <div style={{ display: 'flex' }}>
        <Button icon={<IconArrowLeft />} onClick={() => navigate(-1)}>{i18n("SampleMetadataImportReview.button.back")}</Button>
        <Button style={{ marginLeft: 'auto' }}>
          {i18n("SampleMetadataImportReview.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}