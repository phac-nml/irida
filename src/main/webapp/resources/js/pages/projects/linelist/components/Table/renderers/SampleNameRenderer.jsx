/**
 * @file React component for use with ag-grid to render the sample name as
 * a button of type link which launches the sample details viewer.
 */
import React from "react";
import { Button } from "antd";
import { FIELDS } from "../../../constants";
import { setBaseUrl } from "../../../../../../utilities/url-utilities";
import { SampleDetailViewer } from "../../../../../../components/samples/SampleDetailViewer";

export class SampleNameRenderer extends React.Component {
  constructor(props) {
    super(props);
    this.href = setBaseUrl(
      `projects/${Number(props.data[FIELDS.projectId])}/samples/${Number(
        props.data[FIELDS.sampleId]
      )}`
    );
    this.name = props.data[FIELDS.sampleName];
    this.projectId = Number(props.data[FIELDS.projectId]);
    this.sampleId = Number(props.data[FIELDS.sampleId]);
  }

  render() {
    return (
      <SampleDetailViewer sampleId={this.sampleId} projectId={this.projectId}>
        <Button type="link" className="t-sample-name" style={{ padding: 0 }}>
          {this.name}
        </Button>
      </SampleDetailViewer>
    );
  }
}
