import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Radio, Table, Typography } from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import {
  IconArrowLeft,
  IconArrowRight,
} from "../../../../components/icons/Icons";
import {
  MetadataHeaderItem,
  setSampleNameColumn,
  updateHeaders,
} from "../services/importReducer";
import { NavigateFunction } from "react-router/dist/lib/hooks";
import {
  ImportDispatch,
  ImportState,
  useImportDispatch,
  useImportSelector,
} from "../store";
import { getMetadataRestrictions } from "../../../../apis/metadata/field";

const { Text } = Typography;

/**
 * React component that displays Step #2 of the Sample Metadata Uploader.
 * This page is where the user selects the sample name column.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportMapHeaders(): JSX.Element {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate: NavigateFunction = useNavigate();
  const [loading, setLoading] = React.useState<boolean>(false);
  const [restrictions, setRestrictions] = React.useState([]);
  const [selectedRowKeys, setSelectedRowKeys] = React.useState<React.Key[]>([]);
  const { headers, sampleNameColumn } = useImportSelector(
    (state: ImportState) => state.importReducer
  );
  const [updatedSampleNameColumn, setUpdatedSampleNameColumn] =
    React.useState<string>();
  const updatedHeaders: MetadataHeaderItem[] = [...headers];
  const dispatch: ImportDispatch = useImportDispatch();

  React.useEffect(() => {
    getMetadataRestrictions().then((data) => {
      setRestrictions(data);
      console.log("restrictions");
      console.log(data);
    });
  }, []);

  React.useEffect(() => {
    if (!updatedSampleNameColumn && headers.length > 0) {
      if (sampleNameColumn) {
        const column = headers.filter(
          (header) => header.name === sampleNameColumn
        );
        setSelectedRowKeys([column[0].rowKey]);
        setUpdatedSampleNameColumn(column[0].name);
      } else {
        setSelectedRowKeys([headers[0]?.rowKey]);
        setUpdatedSampleNameColumn(headers[0].name);
      }
    }
  }, [updatedSampleNameColumn, headers, sampleNameColumn, selectedRowKeys]);

  const onSubmit = async () => {
    if (projectId && updatedSampleNameColumn) {
      setLoading(true);
      await dispatch(
        setSampleNameColumn({ projectId, updatedSampleNameColumn })
      );
      await dispatch(updateHeaders(updatedHeaders));
      navigate(`/${projectId}/sample-metadata/upload/review`);
    }
  };

  const onChange = (item: MetadataHeaderItem, value: string) => {
    const index = updatedHeaders.findIndex(
      (header) => header.rowKey === item.rowKey
    );
    if (index !== -1) {
      const updatedHeadersItem = { ...updatedHeaders[index] };
      updatedHeadersItem.restriction = value;
      updatedHeaders[index] = updatedHeadersItem;
    }
  };

  const columns = [
    {
      title: i18n("SampleMetadataImportMapHeaders.table.header"),
      dataIndex: "name",
    },
    {
      title: i18n("SampleMetadataImportMapHeaders.table.restriction"),
      dataIndex: "restriction",
      render(id: number, item: MetadataHeaderItem) {
        return (
          <Radio.Group
            options={restrictions}
            defaultValue={item.restriction}
            disabled={item.name === updatedSampleNameColumn}
            onChange={({ target: { value } }) => onChange({ ...item }, value)}
            optionType="button"
          />
        );
      },
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: (
      selectedRowKeys: React.Key[],
      selectedRows: MetadataHeaderItem[]
    ) => {
      setUpdatedSampleNameColumn(selectedRows[0].name);
      setSelectedRowKeys([selectedRows[0].rowKey]);
    },
  };

  return (
    <SampleMetadataImportWizard current={1}>
      <Text>{i18n("SampleMetadataImportMapHeaders.description")}</Text>
      <Table
        className="t-metadata-uploader-header-table"
        rowKey={(row) => row.rowKey}
        rowSelection={{
          type: "radio",
          ...rowSelection,
        }}
        columns={columns}
        dataSource={headers}
        pagination={false}
      />
      <div style={{ display: "flex" }}>
        <Button
          className="t-metadata-uploader-file-button"
          icon={<IconArrowLeft />}
          onClick={() => navigate(-1)}
        >
          {i18n("SampleMetadataImportMapHeaders.button.back")}
        </Button>
        <Button
          className="t-metadata-uploader-preview-button"
          onClick={onSubmit}
          style={{ marginLeft: "auto" }}
          loading={loading}
        >
          {i18n("SampleMetadataImportMapHeaders.button.next")}
          <IconArrowRight />
        </Button>
      </div>
    </SampleMetadataImportWizard>
  );
}
