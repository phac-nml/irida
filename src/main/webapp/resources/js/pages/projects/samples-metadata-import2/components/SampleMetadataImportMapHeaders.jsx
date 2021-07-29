import React from "react";
import { navigate } from "@reach/router"
import {
  Button,
  Form,
  Radio,
  Typography,
} from "antd";
import { SampleMetadataImportWizard } from "./SampleMetadataImportWizard";
import { useSelector } from "react-redux";
import { BlockRadioInput } from "../../../../components/ant.design/forms/BlockRadioInput";
import { useSetColumnProjectSampleMetadataQuery } from "../../../../apis/metadata/metadata-import";

const { Text } = Typography

function Back() {
  navigate(-1);
}

function setSampleColumn(projectId, {sampleNameColumnRadio}) {
  console.log(projectId, sampleNameColumnRadio);
//   useSetColumnProjectSampleMetadataQuery({ projectId, sampleNameColumnRadio }).then(() =>
  navigate('review');
}

/**
 * React component that displays Step #2 of the Sample Metadata Uploader.
 * This page is where the user selects the sample name column.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportMapHeaders({ projectId }) {

  const { headers, sampleNameColumn } = useSelector((state) => state.reducer);
  const [form] = Form.useForm();

  React.useEffect(() => {
    form.setFieldsValue({
      sampleNameColumnRadio: sampleNameColumn ? sampleNameColumn : headers[0],
    });
  }, []);

  const onFinish = (values) => {
    console.log(values);
    setSampleColumn(projectId, values);
  };

  return (
    <SampleMetadataImportWizard currentStep={1}>
      <Text>
        {i18n("SampleMetadataImportMapHeaders.description")}
      </Text>
      <Form
        form={form}
        onFinish={onFinish}
        name="setSampleNameColumnForm"
      >
        <Form.Item
          name="sampleNameColumnRadio"
          rules={[{ required: true }]}
        >
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
          <Button onClick={() => Back()}> {i18n("SampleMetadataImportMapHeaders.back")}</Button>
          <Button htmlType="submit">{i18n("SampleMetadataImportMapHeaders.next")}</Button>
        </Form.Item>
      </Form>
    </SampleMetadataImportWizard>
  );
}