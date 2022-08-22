import { CheckOutlined, WarningFilled } from "@ant-design/icons";
import { Button, Collapse, FormInstance, Space, Tag } from "antd";
import { NamePath } from "antd/lib/form/interface";
import React from "react";
import { SampleRecord } from "./CreateNcbiExport";
import CreateNcbiSampleDetails from "./CreateNcbiSampleDetails";

/**
 * React component to display if a sample is valid for export to the NCBI.
 * @param status
 * @constructor
 */
function SampleValidIcon({ status }: { status: boolean }) {
  return status ? (
    <Tag color="green" icon={<CheckOutlined />}>
      {i18n("SampleValidIcon.valid").toUpperCase()}
    </Tag>
  ) : (
    <Tag color="orange" icon={<WarningFilled />}>
      {i18n("SampleValidIcon.invalid").toUpperCase()}
    </Tag>
  );
}

/**
 * React component to render a sample that will be exported to the NCBI SRA
 * @param form - export form
 * @param samples - object of samples to export
 * @param removeSample - method to remove a sample from the form
 * @constructor
 */
function CreateNcbiExportSamples({
  form,
  samples,
  removeSample,
}: {
  form: FormInstance;
  samples: Record<string, SampleRecord>;
  removeSample: (
    e: React.MouseEvent<HTMLElement>,
    sample: SampleRecord
  ) => void;
}): JSX.Element {
  const [validationStatus, setValidationStatus] = React.useState<
    Record<string, boolean>
  >({});

  const checkStatus = (sample: SampleRecord): void => {
    const fields: Array<NamePath> = [
      ["samples", sample.name, "bioSample"],
      ["samples", sample.name, "libraryName"],
      ["samples", sample.name, "libraryStrategy"],
      ["samples", sample.name, "librarySource"],
      ["samples", sample.name, "libraryConstructionProtocol"],
      ["samples", sample.name, "instrumentModel"],
      ["samples", sample.name, "librarySelection"],
      ["samples", sample.name, "singles"],
      ["samples", sample.name, "pairs"],
    ];

    const values = form.getFieldsValue(fields).samples[sample.name];

    console.log(values);

    const isValid =
      values.bioSample.length > 0 &&
      values.libraryName.length > 0 &&
      values.libraryStrategy.length > 0 &&
      values.librarySource.length > 0 &&
      values.libraryConstructionProtocol.length > 0 &&
      values.instrumentModel.length > 0 &&
      values.librarySelection.length > 0 &&
      (values.singles.length > 0 || values.pairs.length > 0);

    console.log({ isValid });

    const status = { ...validationStatus, [sample.name]: isValid };
    setValidationStatus(status);
  };

  return (
    <Collapse accordion>
      {Object.values(samples).map((sample) => (
        <Collapse.Panel
          className="t-sample-panel"
          key={String(sample.key)}
          header={
            <Space>
              <SampleValidIcon status={validationStatus[sample.name]} />
              <span className="t-sample-name">{sample.name}</span>
            </Space>
          }
          extra={
            <Button
              size="small"
              onClick={(event) => removeSample(event, sample)}
            >
              {i18n("CreateNcbiExport.remove")}
            </Button>
          }
        >
          <CreateNcbiSampleDetails
            sample={sample}
            onChange={() => checkStatus(sample)}
          />
        </Collapse.Panel>
      ))}
    </Collapse>
  );
}

export default CreateNcbiExportSamples;
