import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Button, Radio, Select, Table, Typography } from "antd";
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
  const { headers, sampleNameColumn } = useImportSelector(
    (state: ImportState) => state.importReducer
  );
  const [updatedSampleNameColumn, setUpdatedSampleNameColumn] =
    React.useState<string>(sampleNameColumn);
  const updatedHeaders = React.useRef<MetadataHeaderItem[]>(headers);
  const dispatch: ImportDispatch = useImportDispatch();

  React.useEffect(() => {
    getMetadataRestrictions().then((data) => {
      setRestrictions(data);
    });
  }, []);

  const onSubmit = async () => {
    if (projectId && updatedSampleNameColumn) {
      setLoading(true);
      await dispatch(
        setSampleNameColumn({ projectId, updatedSampleNameColumn })
      );
      await dispatch(updateHeaders(updatedHeaders.current));
      navigate(`/${projectId}/sample-metadata/upload/review`);
    }
  };

  const onSampleNameColumnChange = (value: string) => {
    setUpdatedSampleNameColumn(value);
  };

  const onRestrictionChange = (item: MetadataHeaderItem, value: string) => {
    const index = updatedHeaders.current.findIndex(
      (header) => header.rowKey === item.rowKey
    );
    if (index !== -1) {
      const updatedHeadersItem = { ...updatedHeaders.current[index] };
      updatedHeadersItem.restriction = value;
      updatedHeaders.current[index] = updatedHeadersItem;
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
            onChange={({ target: { value } }) =>
              onRestrictionChange({ ...item }, value)
            }
            optionType="button"
          />
        );
      },
    },
  ];

  return (
    <SampleMetadataImportWizard current={1}>
      <Text>{i18n("SampleMetadataImportMapHeaders.description")}</Text>
      <Select
        style={{ width: 300 }}
        value={updatedSampleNameColumn}
        onChange={onSampleNameColumnChange}
      >
        {headers.map((header) => (
          <Select.Option key={header.name} value={header.name}>
            {header.name}
          </Select.Option>
        ))}
      </Select>
      {updatedSampleNameColumn && (
        <Table
          className="t-metadata-uploader-header-table"
          rowKey={(row) => row.rowKey}
          columns={columns}
          dataSource={headers.filter(
            (header) => header.name !== updatedSampleNameColumn
          )}
          pagination={false}
          scroll={{ y: 600 }}
        />
      )}
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
