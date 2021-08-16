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
  const [dataSource, setDataSource] = React.useState([]);
  const [columns, setColumns] = React.useState([]);
  const [selected, setSelected] = React.useState([]);
  const { data } = useGetProjectSampleMetadataQuery(projectId);

  const tagColumn = {
    title: '',
    dataIndex: 'tags',
    className: 't-metadata-uploader-new-column',
    fixed: 'left',
    width: 70,
    render: (text, item) => {
      if (!item.foundSampleId)
        return (<Tag color="green">New</Tag>)
    },
    filters: [{ text: i18n("SampleMetadataImportReview.table.filter.new"), value: 'new' }, { text: i18n("SampleMetadataImportReview.table.filter.existing"), value: 'existing' }],
    onFilter: (value, record) => (value === 'new') ? !record.foundSampleId : record.foundSampleId,
  };

  const rowSelection = {
    fixed: true,
    selectedRowKeys: selected,
    onChange: (selectedRowKeys, selectedRows) => {
      setSelected(selectedRowKeys);
    },
  };

  React.useEffect(() => {
    const dataSource = data?.rows?.map((item, index) => {
      let newItem = { ...item, key: `metadata-uploader-row-${index}` };
      return newItem;
    });

    const sampleColumn = data?.headers?.filter(item => item === data?.sampleNameColumn).map(header => ({
      title: header,
      dataIndex: header,
      fixed: 'left',
      width: 100,
      render: (text, item) => <>{item.entry[header]}</>
    }));

    const otherColumns = data?.headers?.filter(item => item !== data?.sampleNameColumn).map(header => ({
      title: header,
      dataIndex: header,
      render: (text, item) => <>{item.entry[header]}</>
    }));

    const columns = sampleColumn?.concat(tagColumn).concat(otherColumns);

    setDataSource(dataSource);
    setColumns(columns);
    setSelected(dataSource?.map(item => { return item.key; }));
  }, [data]);

  return (
    <SampleMetadataImportWizard currentStep={2}>
      <Text>
        {i18n("SampleMetadataImportReview.description")}
      </Text>
      <Table className="t-metadata-uploader-review-table" rowSelection={rowSelection} columns={columns} dataSource={dataSource} scroll={{ x: 'max-content', y: 600 }} pagination={false} />
      <div style={{ display: 'flex' }}>
        <Button className="t-metadata-uploader-column-button" icon={<IconArrowLeft />} onClick={() => navigate(-1)}>{i18n("SampleMetadataImportReview.button.back")}</Button>
        <Button className="t-metadata-uploader-upload-button" style={{ marginLeft: 'auto' }}>
          {i18n("SampleMetadataImportReview.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}