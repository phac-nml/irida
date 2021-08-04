import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { navigate } from "@reach/router"
import {
  Button,
  Form,
  Radio,
  Typography,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { BlockRadioInput } from "../../../../components/ant.design/forms/BlockRadioInput";
import { useSetColumnProjectSampleMetadataMutation } from "../../../../apis/metadata/metadata-import";

const { Text } = Typography

/**
 * React component that displays Step #2 of the Sample Metadata Uploader.
 * This page is where the user selects the sample name column.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportMapHeaders({ projectId }) {
  const dispatch = useDispatch();
  const { headers, sampleNameColumn } = useSelector((state) => state.reducer);
  const [form] = Form.useForm();
  const [updateColumn] = useSetColumnProjectSampleMetadataMutation(projectId, sampleNameColumn);

  React.useEffect(() => {
    form.setFieldsValue({
      sampleNameColumnRadio: sampleNameColumn ? sampleNameColumn : headers[0],
    });
  }, []);

  const onFinish = (values) => {
    updateColumn({ projectId: projectId, sampleNameColumn: values.sampleNameColumnRadio });
    navigate('review');
  };

  return (
    <SampleMetadataImportWizard currentStep={1}>
      <Text>
        {i18n("SampleMetadataImportMapHeaders.description")}
      </Text>
      <Form
        form={form}
        onFinish={onFinish}
      >
        <Form.Item name="sampleNameColumnRadio">
          <Radio.Group style={{ width: `100%` }}>
            {headers.map((header, index) => (
              <BlockRadioInput key={`radio-item-header-${index}`}>
                <Radio key={`radio-header-${index}`} value={header}>
                  {header}
                </Radio>
              </BlockRadioInput>
            ))}
          </Radio.Group>
        </Form.Item>
        <Form.Item>
          <Button onClick={() => navigate(-1)}> {i18n("SampleMetadataImportMapHeaders.back")}</Button>
          <Button htmlType="submit">{i18n("SampleMetadataImportMapHeaders.next")}</Button>
        </Form.Item>
      </Form>
    </SampleMetadataImportWizard>
  );
}