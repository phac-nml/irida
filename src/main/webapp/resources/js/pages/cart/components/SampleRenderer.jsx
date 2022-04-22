import { Button, Space, Tag, Tooltip } from "antd";

import React from "react";
import { IconLocked, IconRemove } from "../../../components/icons/Icons";
import {
  SampleDetailViewer
} from "../../../components/samples/SampleDetailViewer";
import { grey1, grey4 } from "../../../styles/colors";
import { SPACE_SM, SPACE_XS } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";

/**
 * Component to display sample information on the Cart page.  This is used
 * by ag-grid to render the a cell in the table.
 */
export class SampleRenderer extends React.Component {
  displaySample = () => this.props.displaySample(this.props.data);

  removeProject = () => this.props.removeProject(this.props.data.project.id);

  render() {
    const sample = this.props.data;
    return (
      <div
        style={{
          ...this.props.style,
          padding: SPACE_SM,
          backgroundColor: grey1,
          borderBottom: `1px solid ${grey4}`,
        }}
      >
        <div
          className="t-cart-sample"
          key={sample.id}
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: SPACE_XS,
          }}
        >
          <Space>
            <SampleDetailViewer
              sampleId={sample.id}
              removeSample={this.props.removeSample}
            >
              <Button
                className="t-sample-details-btn"
                size="small"
                onClick={this.displaySample}
              >
                {sample.label}
              </Button>
            </SampleDetailViewer>
            {this.props.data.locked && (
              <Tooltip placement="right" title={i18n("SampleRenderer.locked")}>
                <IconLocked />
              </Tooltip>
            )}
          </Space>
          <Space>
            <Tag
              color="blue"
              closable
              onClose={this.removeProject}
              closeIcon={
                <Tooltip
                  placement="topRight"
                  title={i18n("SampleRenderer.remove.project")}
                >
                  <IconRemove className="t-remove-project" />
                </Tooltip>
              }
            >
              <a href={setBaseUrl(`projects/${sample.project.id}`)}>
                {sample.project.label}
              </a>
            </Tag>
            <Tooltip
              placement="left"
              title={i18n("SampleRenderer.remove.sample")}
            >
              <Button
                type="text"
                className="t-remove-sample"
                icon={<IconRemove />}
                size="small"
                onClick={() =>
                  this.props.removeSample(sample.project.id, sample.id)
                }
              />
            </Tooltip>
          </Space>
        </div>
      </div>
    );
  }
}
