import React from "react";
import { useHistory, useParams } from "react-router-dom";
import { Alert, Button, Space, Table, Tag, Tooltip, Typography } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import {
  useGetProjectSampleMetadataQuery,
  useSaveProjectSampleMetadataMutation,
} from "../../../../apis/metadata/metadata-import";
import {
  IconArrowLeft,
  IconArrowRight,
  IconExclamationCircle
} from "../../../../components/icons/Icons";
import { red1, red2, red5 } from "../../../../styles/colors";
import styled from "styled-components";

const { Paragraph, Text } = Typography;

const ErrorTable = styled(Table)`
  tr.red > td {
    background-color: ${red1};
  }
  tr.red:hover > td {
    background-color: ${red2};
  }
  tr.red > td.ant-table-cell-fix-left {
    background-color: ${red1};
  }
  tr.red:hover > td.ant-table-cell-fix-left {
    background-color: ${red2};
  }
  tr.red > td.ant-table-cell-fix-right {
    background-color: ${red1};
  }
  tr.red:hover > td.ant-table-cell-fix-right {
    background-color: ${red2};
  }
`;

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
  const [valid, setValid] = React.useState(true);
  const {
    data = {},
    isError,
    isFetching,
    isSuccess,
    refetch
  } = useGetProjectSampleMetadataQuery(projectId);
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
    getCheckboxProps: (record) => ({
      disabled: !(record.isSampleNameValid && (record.saved === null || record.saved === true)),
    }),
  };

  React.useEffect(() => {
    if (isSuccess) {
      setValid(!data.rows.some((row) => row.isSampleNameValid === false));

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
        render(text, item) {
          return {
            props: {
              style: { background: item.isSampleNameValid ? null : red1 },
            },
            children:
              item.entry[sample]
          };
        },
      };

      const savedColumn = {
          dataIndex: "saved",
          fixed: "left",
          width: 10,
          render: (text, item) => {
            if(item.saved === false)
              return (
                <Tooltip title={item.error} color={red5}>
                  <IconExclamationCircle style={{ color: red5 }} />
                </Tooltip>
              );
          },
        };

      const otherColumns = headers.map((header) => ({
        title: header,
        dataIndex: header,
        render: (text, item) => item.entry[header],
      }));

      const updatedColumns = [savedColumn, sampleColumn, tagColumn, ...otherColumns];

      setColumns(updatedColumns);
      setSelected(
        data.rows.map((row) => {
          if (row.isSampleNameValid && (row.saved === null || row.saved === true)) return row.rowKey;
        })
      );
    }
  }, [data, isSuccess]);

  const save = () => {
    const sampleNames = data.rows
      .filter((row) => selected.includes(row.rowKey))
      .map((row) => row.entry[data.sampleNameColumn]);
    saveMetadata({ projectId, sampleNames })
      .unwrap()
      .then((payload) => {
        refetch();
        if(data.rows.filter(row => selected.includes(row.rowKey)).every(row => row.saved === true)){
          history.push({
            pathname: "complete",
            state: { statusMessage: payload.message },
          });
        }
      });
  };

  return (
    <SampleMetadataImportWizard currentStep={2}>
      <Text>{i18n("SampleMetadataImportReview.description")}</Text>
      {!valid && (
        <Alert
          message={i18n("SampleMetadataImportReview.alert.title")}
          description={
            <Paragraph>
              {i18n("SampleMetadataImportReview.alert.description")}
              <ul>
                <li>{i18n("SampleMetadataImportReview.alert.rule1")}</li>
                <li>{i18n("SampleMetadataImportReview.alert.rule2")}</li>
                <li>{i18n("SampleMetadataImportReview.alert.rule3")}</li>
              </ul>
            </Paragraph>
          }
          type="error"
          showIcon
        />
      )}
      <ErrorTable
        className="t-metadata-uploader-review-table"
        rowKey={(row) => row.rowKey}
        loading={isFetching}
        rowClassName={(record, index) => (record.saved === false ? "red" : null)}
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
