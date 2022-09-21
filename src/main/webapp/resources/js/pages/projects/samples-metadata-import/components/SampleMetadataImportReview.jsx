import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Alert, Button, Table, Tag, Tooltip, Typography } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import {
  IconArrowLeft,
  IconArrowRight,
  IconExclamationCircle,
} from "../../../../components/icons/Icons";
import { red1, red2, red5 } from "../../../../styles/colors";
import styled from "styled-components";
import { useDispatch, useSelector } from "react-redux";
import { saveMetadata } from "../services/importReducer";
import { getPaginationOptions } from "../../../../utilities/antdesign-table-utilities";

const { Paragraph, Text } = Typography;

const MetadataTable = styled(Table)`
  tr.row-error > td {
    background-color: ${red1};
  }
  tr.row-error:hover > td {
    background-color: ${red2};
  }
  tr.row-error > td.ant-table-cell-fix-left {
    background-color: ${red1};
  }
  tr.row-error:hover > td.ant-table-cell-fix-left {
    background-color: ${red2};
  }
  tr.row-error > td.ant-table-cell-fix-right {
    background-color: ${red1};
  }
  tr.row-error:hover > td.ant-table-cell-fix-right {
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
  const navigate = useNavigate();
  const [columns, setColumns] = React.useState([]);
  const [selected, setSelected] = React.useState([]);
  const [valid, setValid] = React.useState(true);
  const { headers, sampleNameColumn, metadata } = useSelector(
    (state) => state.importReducer
  );
  const dispatch = useDispatch();

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
      disabled: !(
        record.isSampleNameValid &&
        (record.saved === null || record.saved === true)
      ),
    }),
  };

  React.useEffect(() => {
    setValid(!metadata.some((row) => row.isSampleNameValid === false));

    const sampleColumn = {
      title: sampleNameColumn,
      dataIndex: sampleNameColumn,
      fixed: "left",
      width: 100,
      onCell: (item) => {
        return {
          style: {
            background: item.isSampleNameValid === true ? null : red1,
          },
        };
      },
    };

    const savedColumn = {
      dataIndex: "saved",
      fixed: "left",
      width: 10,
      render: (text, item) => {
        if (item.saved === false)
          return (
            <Tooltip title={item.error} color={red5}>
              <IconExclamationCircle style={{ color: red5 }} />
            </Tooltip>
          );
      },
    };

    const otherColumns = headers
      .filter((header) => header !== sampleNameColumn)
      .map((header) => ({
        title: header,
        dataIndex: header,
      }));

    const updatedColumns = [
      savedColumn,
      sampleColumn,
      tagColumn,
      ...otherColumns,
    ];

    setColumns(updatedColumns);
    setSelected(
      metadata.map((row) => {
        if (row.isSampleNameValid && (row.saved === null || row.saved === true))
          return row.rowKey;
      })
    );
  }, []);

  const save = async () => {
    const selectedMetadataKeys = metadata
      .filter((metadataItem) => selected.includes(metadataItem.rowKey))
      .map((metadataItem) => metadataItem.rowKey);

    const response = await dispatch(
      saveMetadata({ projectId, selectedMetadataKeys })
    );

    if (
      response.payload.metadata.every((metadataItem) => !metadataItem.error)
    ) {
      navigate(`/${projectId}/sample-metadata/upload/complete`);
    }
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
      <MetadataTable
        className="t-metadata-uploader-review-table"
        rowKey={(row) => row.rowKey}
        rowClassName={(record) => (record.saved === false ? "row-error" : null)}
        rowSelection={rowSelection}
        columns={columns}
        dataSource={metadata}
        scroll={{ x: "max-content", y: 600 }}
        pagination={getPaginationOptions(metadata.length)}
      />

      <div style={{ display: "flex" }}>
        <Button
          className="t-metadata-uploader-column-button"
          icon={<IconArrowLeft />}
          onClick={() => navigate(-1)}
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
