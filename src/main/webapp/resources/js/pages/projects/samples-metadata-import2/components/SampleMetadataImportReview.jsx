import React, { useEffect, useState } from "react";
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
  const [selected, setSelected] = useState([]);
  const { data } = useGetProjectSampleMetadataQuery(projectId);

  const dataSource = data?.rows?.map((item, index) => {
    let newItem = { ...item, key: index };
    return newItem;
  });

  const tagColumn = { title: '', dataIndex: 'tags', fixed: 'left', width: 70, render: (text, item) => { if (!item.foundSampleId) return (<Tag color="green">New</Tag>) } };

  const sampleColumn = data?.headers?.filter(item => item === data?.sampleNameColumn).map((header) => {
    let item = { title: header, dataIndex: header, fixed: 'left', width: 100, render: (text, item) => (<>{item.entry[header]}</>) };
    return item;
  });

  const otherColumns = data?.headers?.filter(item => item !== data?.sampleNameColumn).map((header) => {
    let item = { title: header, dataIndex: header, render: (text, item) => (<>{item.entry[header]}</>) };
    return item;
  });

  const columns = sampleColumn?.concat(tagColumn).concat(otherColumns);

  const rowSelection = {
    fixed: true,
    selectedRowKeys: selected,
    onChange: (selectedRowKeys, selectedRows) => {
      setSelected(selectedRowKeys);
    },
  };

  useEffect(() => {
    setSelected(dataSource?.map(item => {return item.key;}));
  }, [data]);

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