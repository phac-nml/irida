import React from "react";
import { useHistory, useParams } from "react-router-dom";
import { Alert, Button, Table, Tag, Typography } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import {
  useGetProjectSampleMetadataQuery,
  useSaveProjectSampleMetadataMutation,
} from "../../../../apis/metadata/metadata-import";
import {
  IconArrowLeft,
  IconArrowRight,
} from "../../../../components/icons/Icons";
import { red1 } from "../../../../styles/colors";

const { Paragraph, Text } = Typography;

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
  const [valid, setValid] = React.useState(false);
  const { data = {}, isError, isFetching, isSuccess } = useGetProjectSampleMetadataQuery(projectId);
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

  const regex = new RegExp("^[A-Za-z0-9\-\_]{3,}$");

  React.useEffect(() => {
    if (isSuccess) {
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
        render(text, item){
          return({
            props: {style: {background: regex.test(item.entry[sample]) ? null : red1}},
            children: item.entry[sample]
          })
        },
      };

      const otherColumns = headers.map((header) => ({
        title: header,
        dataIndex: header,
        render: (text, item) => item.entry[header],
      }));

      const updatedColumns = [sampleColumn, tagColumn, ...otherColumns];

      setColumns(updatedColumns);
      setSelected(
        data.rows.map((row) => {
          return row.rowKey;
        })
      );
    }
  }, [data, isSuccess]);

  const save = () => {
    const sampleNames = data.rows.filter(row => selected.includes(row.rowKey)).map(row => row.entry[data.sampleNameColumn]);
    saveMetadata({ projectId, sampleNames })
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
      {!valid && <Alert
        message="Validation Error"
        description={
          <Paragraph>Please correct the following errors within the file and re-upload. The sample name must meet the following criteria:
            <ul>
              <li>cannot be empty</li>
              <li>minimum 3 characters long</li>
              <li>contain only alphanumeric characters and '-', '_'</li>
            </ul>
           </Paragraph>
         }
        type="error"
        showIcon
      />}
      <Table
        className="t-metadata-uploader-review-table"
        rowKey={(row) => row.rowKey}
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
          disabled={!valid}
        >
          {i18n("SampleMetadataImportReview.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}
