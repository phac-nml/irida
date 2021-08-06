import { Button, Space, Tag, Tooltip } from "antd";

import PropTypes from "prop-types";
import React from "react";
import { IconLocked, IconRemove } from "../../../components/icons/Icons";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { grey1, grey4 } from "../../../styles/colors";
import { SPACE_SM, SPACE_XS } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";

/**
 * Component to display sample information on the Cart page.  This is used
 * by ag-grid to render the a cell in the table.
 */
export class SampleRenderer extends React.Component {
  static propTypes = {
    api: PropTypes.shape({
      /** Function to open panel with details of a sample */
      displaySample: PropTypes.func.isRequired,
      /** Function to remove a sample from the cart */
      removeSample: PropTypes.func.isRequired,
      /** Function to remove an entire project from the cart */
      removeProject: PropTypes.func.isRequired,
    }),
    /** Index in the ag-grid table of the current row */
    rowIndex: PropTypes.number.isRequired,
    /** All the information about the current sample */
    data: PropTypes.shape({
      label: PropTypes.string.isRequired,
      project: PropTypes.shape({
        label: PropTypes.string.isRequired,
        id: PropTypes.number.isRequired,
      }).isRequired,
    }),
  };

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
            <Button
              type="text"
              className="t-remove-sample"
              icon={<IconRemove />}
              size="small"
              onClick={() =>
                this.props.removeSample(sample.project.id, sample.id)
              }
            />
          </Space>
        </div>
      </div>
    );
  }
}
