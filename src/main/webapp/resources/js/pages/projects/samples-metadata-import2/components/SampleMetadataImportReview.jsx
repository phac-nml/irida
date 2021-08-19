import React from 'react'
import { navigate } from '@reach/router'
import { Button, Table, Tag, Typography } from 'antd'
import { SampleMetadataImportWizard } from './SampleMetadataImportWizard'
import { useGetProjectSampleMetadataQuery } from '../../../../apis/metadata/metadata-import'
import {
  IconArrowLeft,
  IconArrowRight,
} from '../../../../components/icons/Icons'

const { Text } = Typography

/**
 * React component that displays Step #3 of the Sample Metadata Uploader.
 * This page is where the user reviews the metadata to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportReview({ projectId }) {
  const [columns, setColumns] = React.useState([])
  const [selected, setSelected] = React.useState([])
  const { data = {}, isLoading } = useGetProjectSampleMetadataQuery(projectId)
  const tagColumn = {
    title: '',
    dataIndex: 'tags',
    className: 't-metadata-uploader-new-column',
    fixed: 'left',
    width: 70,
    render: (text, item) => {
      if (!item.foundSampleId)
        return (
          <Tag color="green">
            {i18n('SampleMetadataImportReview.table.filter.new')}
          </Tag>
        )
    },
    filters: [
      {
        text: i18n('SampleMetadataImportReview.table.filter.new'),
        value: 'new',
      },
      {
        text: i18n('SampleMetadataImportReview.table.filter.existing'),
        value: 'existing',
      },
    ],
    onFilter: (value, record) =>
      value === 'new' ? !record.foundSampleId : record.foundSampleId,
  }

  const rowSelection = {
    fixed: true,
    selectedRowKeys: selected,
    onChange: (selectedRowKeys, selectedRows) => {
      setSelected(selectedRowKeys)
    },
  }

  React.useEffect(() => {
    if (!isLoading) {
      const index = data.headers.findIndex(
        (item) => item === data.sampleNameColumn,
      )

      const headers = [...data.headers]

      const sample = headers.splice(index, 1)[0]

      const sampleColumn = {
        title: sample,
        dataIndex: sample,
        fixed: 'left',
        width: 100,
        render: (text, item) => <>{item.entry[sample]}</>,
      }

      const otherColumns = headers.map((header) => ({
        title: header,
        dataIndex: header,
        render: (text, item) => <>{item.entry[header]}</>,
      }))

      const updatedColumns = [sampleColumn, tagColumn, ...otherColumns]

      setColumns(updatedColumns)
      setSelected(
        data.rows.map((item) => {
          return item.key
        }),
      )
    }
  }, [data, isLoading])

  return (
    <SampleMetadataImportWizard currentStep={2}>
      <Text>{i18n('SampleMetadataImportReview.description')}</Text>
      <Table
        className="t-metadata-uploader-review-table"
        rowKey={(row) => row.entry[data.sampleNameColumn]}
        loading={isLoading}
        rowSelection={rowSelection}
        columns={columns}
        dataSource={data.rows}
        scroll={{ x: 'max-content', y: 600 }}
        pagination={false}
      />

      <div style={{ display: 'flex' }}>
        <Button
          className="t-metadata-uploader-column-button"
          icon={<IconArrowLeft />}
          onClick={() => navigate(-1)}
        >
          {i18n('SampleMetadataImportReview.button.back')}
        </Button>
        <Button
          className="t-metadata-uploader-upload-button"
          style={{ marginLeft: 'auto' }}
          onClick={() => navigate('complete')}
        >
          {i18n('SampleMetadataImportReview.button.next')}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  )
}
