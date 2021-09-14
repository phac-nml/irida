import React from "react";
import { useHistory, useParams } from "react-router-dom";
import { Button, Table, Tag, Typography } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import {
  useGetProjectSampleMetadataQuery,
  useSaveProjectSampleMetadataMutation,
} from "../../../../apis/metadata/metadata-import";
import {
  IconArrowLeft,
  IconArrowRight,
} from "../../../../components/icons/Icons";

const { Text } = Typography;

/**
 * React component that displays Step #3 of the Sample Metadata Uploader.
 * This page is where the user reviews the metadata to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportReview() {
  const { projectId } = useParams();
  const history = useHistory();
  const [columns, setColumns] = React.useState([]);
  const [selected, setSelected] = React.useState([]);
  const { data = {}, isFetching, isSuccess } = useGetProjectSampleMetadataQuery(projectId);
  const [saveMetadata] = useSaveProjectSampleMetadataMutation();
  const tagColumn = {
    title: "",
    dataIndex: "tags",
    className: "t-metadata-uploader-new-column",
    fixed: "left",
    width: 70,
    render: (text, item) => {
      if (!item.foundSampleId)
        return (
          <Tag color="green">
            {i18n("SampleMetadataImportReview.table.filter.new")}
          </Tag>
        );
    },
    filters: [
      {
        text: i18n("SampleMetadataImportReview.table.filter.new"),
        value: "new",
      },
      {
        text: i18n("SampleMetadataImportReview.table.filter.existing"),
        value: "existing",
      },
    ],
    onFilter: (value, record) =>
      value === "new" ? !record.foundSampleId : record.foundSampleId,
  };

  const rowSelection = {
    fixed: true,
    selectedRowKeys: selected,
    onChange: (selectedRowKeys) => {
      setSelected(selectedRowKeys);
    },
  };

  React.useEffect(() => {
    if (isSuccess) {
      console.log(data);
      const index = data.headers.findIndex(
        (item) => item === data.sampleNameColumn
      );

      const headers = [...data.headers];

      const sample = headers.splice(index, 1)[0];

      const sampleColumn = {
        title: sample,
        dataIndex: sample,
        fixed: "left",
        width: 100,
        render: (text, item) => <>{item.entry[sample]}</>,
      };

      const otherColumns = headers.map((header) => ({
        title: header,
        dataIndex: header,
        render: (text, item) => <>{item.entry[header]}</>,
      }));

      const updatedColumns = [sampleColumn, tagColumn, ...otherColumns];

      setColumns(updatedColumns);
      setSelected(
        data.rows.map((row) => {
          return row.entry[data.sampleNameColumn];
        })
      );
    }
  }, [data, isSuccess]);

  const save = () => {
    saveMetadata({ projectId, sampleNames: selected })
      .unwrap()
      .then((payload) => {
        history.push({
          pathname: "complete",
          state: { statusMessage: payload.message },
        });
      });
  };

  return (
    <SampleMetadataImportWizard currentStep={2}>
      <Text>{i18n("SampleMetadataImportReview.description")}</Text>
      <Table
        className="t-metadata-uploader-review-table"
        rowKey={(row) => row.entry[data.sampleNameColumn]}
        loading={isFetching}
        rowSelection={rowSelection}
        columns={columns}
        dataSource={data.rows}
        scroll={{ x: "max-content", y: 600 }}
        pagination={false}
      />

      <div style={{ display: "flex" }}>
        <Button
          className="t-metadata-uploader-column-button"
          icon={<IconArrowLeft />}
          onClick={() => history.goBack()}
        >
          {i18n("SampleMetadataImportReview.button.back")}
        </Button>
        <Button
          className="t-metadata-uploader-upload-button"
          style={{ marginLeft: "auto" }}
          onClick={save}
        >
          {i18n("SampleMetadataImportReview.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}
