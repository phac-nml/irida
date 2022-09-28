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
  const [progress, setProgress] = React.useState(0);
  const [loading, setLoading] = React.useState(false);
  const {
    headers,
    sampleNameColumn,
    metadata,
    metadataValidateDetails,
    metadataSaveDetails,
  } = useSelector((state) => state.importReducer);
  const dispatch = useDispatch();

  const tagColumn = {
    title: "",
    dataIndex: "tags",
    className: "t-metadata-uploader-new-column",
    fixed: "left",
    width: 70,
    render: (text, item) => {
      if (!metadataValidateDetails[item.rowKey].foundSampleId)
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
      value === "new"
        ? !metadataValidateDetails[record.rowKey].foundSampleId
        : metadataValidateDetails[record.rowKey].foundSampleId,
  };

  const rowSelection = {
    fixed: true,
    selectedRowKeys: selected,
    onChange: (selectedRowKeys) => {
      setSelected(selectedRowKeys);
    },
    getCheckboxProps: (record) => ({
      disabled: !(
        metadataValidateDetails[record.rowKey].isSampleNameValid ||
        metadataSaveDetails[record.rowKey]?.saved === true
      ),
    }),
  };

  React.useEffect(() => {
    const savedCount = Object.entries(metadataSaveDetails).filter(
      ([key, metadataSaveDetailsItem]) => metadataSaveDetailsItem.saved
    ).length;
    setProgress((savedCount / selected.length) * 100);
  }, [metadataSaveDetails]);

  React.useEffect(() => {
    setValid(
      !metadata.some(
        (row) => metadataValidateDetails[row.rowKey].isSampleNameValid === false
      )
    );

    const sampleColumn = {
      title: sampleNameColumn,
      dataIndex: sampleNameColumn,
      fixed: "left",
      width: 100,
      onCell: (item) => {
        return {
          style: {
            background:
              metadataValidateDetails[item.rowKey].isSampleNameValid === true
                ? null
                : red1,
          },
        };
      },
    };

    const savedColumn = {
      dataIndex: "saved",
      fixed: "left",
      width: 10,
      render: (text, item) => {
        if (metadataSaveDetails[item.rowKey]?.saved === false)
          return (
            <Tooltip
              title={metadataSaveDetails[item.rowKey]?.error}
              color={red5}
            >
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
        if (
          metadataValidateDetails[row.rowKey].isSampleNameValid ||
          metadataSaveDetails[row.rowKey]?.saved === true
        )
          return row.rowKey;
      })
    );
  }, [metadataSaveDetails]);

  const save = async () => {
    setLoading(true);
    const selectedMetadataKeys = metadata
      .filter((metadataItem) => selected.includes(metadataItem.rowKey))
      .map((metadataItem) => metadataItem.rowKey);

    const response = await dispatch(
      saveMetadata({ projectId, selectedMetadataKeys })
    );

    if (
      Object.entries(response.payload.metadataSaveDetails).filter(
        ([, metadataSaveDetailsItem]) => metadataSaveDetailsItem.error
      ).length === 0
    ) {
      navigate(`/${projectId}/sample-metadata/upload/complete`);
    } else {
      setLoading(false);
    }
  };

  return (
    <SampleMetadataImportWizard currentStep={2} currentPercent={progress}>
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
        rowClassName={(record) =>
          metadataSaveDetails[record.rowKey]?.saved === false
            ? "row-error"
            : null
        }
        rowSelection={rowSelection}
        columns={columns}
        dataSource={metadata}
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
          loading={loading}
        >
          {i18n("SampleMetadataImportReview.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}
