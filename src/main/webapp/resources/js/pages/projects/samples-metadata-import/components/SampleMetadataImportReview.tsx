import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  Alert,
  Button,
  Table,
  TableProps,
  Tag,
  Tooltip,
  Typography,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import {
  IconArrowLeft,
  IconArrowRight,
  IconExclamationCircle,
} from "../../../../components/icons/Icons";
import styled from "styled-components";
import { saveMetadata } from "../services/importReducer";
import { getPaginationOptions } from "../../../../utilities/antdesign-table-utilities";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import {
  ImportDispatch,
  ImportState,
  useImportDispatch,
  useImportSelector,
} from "../store";
import { MetadataItem } from "../../../../apis/projects/samples";
import { ColumnsType, ColumnType } from "antd/es/table";
import { TableRowSelection } from "antd/lib/table/interface";

const { Paragraph, Text } = Typography;

const MetadataTable = styled(Table)`
  tr.row-error > td {
    background-color: var(--red-1);
  }
  tr.row-error:hover > td {
    background-color: var(--red-2);
  }
  tr.row-error > td.ant-table-cell-fix-left {
    background-color: var(--red-1);
  }
  tr.row-error:hover > td.ant-table-cell-fix-left {
    background-color: var(--red-2);
  }
  tr.row-error > td.ant-table-cell-fix-right {
    background-color: var(--red-1);
  }
  tr.row-error:hover > td.ant-table-cell-fix-right {
    background-color: var(--red-2);
  }
`;

/**
 * React component that displays Step #3 of the Sample Metadata Uploader.
 * This page is where the user reviews the metadata to be uploaded.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportReview(): JSX.Element {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate: NavigateFunction = useNavigate();
  const [columns, setColumns] = React.useState<ColumnsType<MetadataItem>>([]);
  const [selected, setSelected] = React.useState<React.Key[]>([]);
  const [valid, setValid] = React.useState<boolean>(true);
  const [progress, setProgress] = React.useState<number>(0);
  const [loading, setLoading] = React.useState<boolean>(false);
  const {
    headers,
    sampleNameColumn,
    metadata,
    metadataValidateDetails,
    metadataSaveDetails,
  } = useImportSelector((state: ImportState) => state.importReducer);
  const dispatch: ImportDispatch = useImportDispatch();

  const rowSelection: TableRowSelection<MetadataItem> = {
    fixed: true,
    selectedRowKeys: selected,
    onChange: (selectedRowKeys) => {
      setSelected(selectedRowKeys);
    },
    getCheckboxProps: (record: MetadataItem) => ({
      disabled: !(
        metadataValidateDetails[record[sampleNameColumn]].isSampleNameValid ||
        metadataSaveDetails[record[sampleNameColumn]]?.saved === true
      ),
    }),
  };

  React.useEffect(() => {
    const savedCount = Object.entries(metadataSaveDetails).filter(
      ([, metadataSaveDetailsItem]) => metadataSaveDetailsItem.saved
    ).length;
    setProgress((savedCount / selected.length) * 100);
  }, [metadataSaveDetails, selected.length]);

  React.useEffect(() => {
    setValid(
      !metadata.some(
        (row) =>
          !metadataValidateDetails[row[sampleNameColumn]].isSampleNameValid
      )
    );

    const sampleColumn: ColumnType<MetadataItem> = {
      title: sampleNameColumn,
      dataIndex: sampleNameColumn,
      fixed: "left",
      width: 100,
      onCell: (item) => {
        return {
          style: {
            background: metadataValidateDetails[item[sampleNameColumn]]
              .isSampleNameValid
              ? undefined
              : `var(--red-1)`,
          },
        };
      },
    };

    const savedColumn: ColumnType<MetadataItem> = {
      dataIndex: "saved",
      fixed: "left",
      width: 10,
      render: (text, item) => {
        if (metadataSaveDetails[item[sampleNameColumn]]?.saved === false)
          return (
            <Tooltip
              title={metadataSaveDetails[item[sampleNameColumn]]?.error}
              color={`var(--red-5)`}
            >
              <IconExclamationCircle style={{ color: `var(--red-5)` }} />
            </Tooltip>
          );
        return text;
      },
    };

    const tagColumn: ColumnType<MetadataItem> = {
      title: "",
      dataIndex: "tags",
      className: "t-metadata-uploader-new-column",
      fixed: "left",
      width: 70,
      render: (text, item) => {
        if (!metadataValidateDetails[item[sampleNameColumn]].foundSampleId)
          return (
            <Tag color="green">
              {i18n("SampleMetadataImportReview.table.filter.new")}
            </Tag>
          );
        return text;
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
          ? metadataValidateDetails[record[sampleNameColumn]].foundSampleId !==
            undefined
          : metadataValidateDetails[record[sampleNameColumn]].foundSampleId ===
            undefined,
    };

    const otherColumns: ColumnsType<MetadataItem> = headers
      .filter((header) => header !== sampleNameColumn)
      .map((header) => ({
        title: header,
        dataIndex: header,
      }));

    const updatedColumns: ColumnsType<MetadataItem> = [
      savedColumn,
      sampleColumn,
      tagColumn,
      ...otherColumns,
    ];

    setColumns(updatedColumns);
    setSelected(
      metadata
        .filter(
          (row) =>
            metadataValidateDetails[row[sampleNameColumn]].isSampleNameValid ||
            metadataSaveDetails[row[sampleNameColumn]]?.saved === true
        )
        .map((row): string => row.rowKey)
    );
  }, [
    headers,
    metadata,
    metadataSaveDetails,
    metadataValidateDetails,
    sampleNameColumn,
  ]);

  const save = async () => {
    setLoading(true);
    const selectedMetadataKeys = metadata
      .filter((metadataItem) => selected.includes(metadataItem.rowKey))
      .map((metadataItem) => metadataItem.rowKey);

    if (projectId) {
      const response = await dispatch(
        saveMetadata({ projectId, selectedMetadataKeys })
      ).unwrap();

      if (
        Object.entries(response.metadataSaveDetails).filter(
          ([, metadataSaveDetailsItem]) => metadataSaveDetailsItem.error
        ).length === 0
      ) {
        navigate(`/${projectId}/sample-metadata/upload/complete`);
      } else {
        setLoading(false);
      }
    }
  };

  return (
    <SampleMetadataImportWizard current={2} percent={progress}>
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
      <MetadataTable<(props: TableProps<MetadataItem>) => JSX.Element>
        className="t-metadata-uploader-review-table"
        rowKey={(row) => row.rowKey}
        rowClassName={(record) =>
          metadataSaveDetails[record[sampleNameColumn]]?.saved === false
            ? "row-error"
            : ""
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
