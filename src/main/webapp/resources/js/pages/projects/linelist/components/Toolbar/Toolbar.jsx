import { Button, Form, Input, Popover, Space } from "antd";

import React, { Component, Suspense } from "react";
import { connect } from "react-redux";
import {
  IconCloudUpload,
  IconQuestion,
} from "../../../../../components/icons/Icons";
import { setBaseUrl } from "../../../../../utilities/url-utilities";
import { actions as entryActions } from "../../reducers/entries";
import { AddSamplesToCartButton } from "../AddToCartButton/AddSamplesToCart";
import { ExportDropDown } from "../Export/ExportDropdown";
import { ShareSampleButton } from "../ShareSampleButton";

const LineListTour = React.lazy(() => import("../Tour/LineListTour"));

const { Search } = Input;

const { project } = window.PAGE;

export class ToolbarComponent extends Component {
  state = { tourOpen: false, showTourPopover: false };

  componentDidMount() {
    if (typeof window.localStorage === "object") {
      if (!window.localStorage.getItem("linelist-tour")) {
        window.localStorage.setItem("linelist-tour", "complete");
        this.setState({ showTourPopover: true });
        this.timer = window.setTimeout(() => {
          this.setState({ showTourPopover: false });
        }, 10000);
      }
    }
  }

  openTour = () => {
    this.props.scrollTableToTop();
    this.setState({ tourOpen: true, showTourPopover: false });
  };

  closePopover = () => {
    window.clearTimeout(this.timer);
    this.setState({ showTourPopover: false });
  };

  closeTour = () => this.setState({ tourOpen: false });

  componentWillUnmount() {
    /*
    Clear the timer if it is there to prevent any memory leakages.
     */
    window.clearTimeout(this.timer);
  }

  render() {
    return (
      <div className="toolbar">
        <Space>
          <ExportDropDown
            csv={this.props.exportCSV}
            excel={this.props.exportXLSX}
          />
          {window.project.canManage && <ShareSampleButton />}
          <AddSamplesToCartButton
            selectedCount={this.props.selectedCount}
            addSamplesToCart={this.props.addSamplesToCart}
          />
        </Space>
        <div className="toolbar-group">
          <Form layout="inline">
            {window.project.canManage ? (
              <Form.Item>
                <Button
                  className="t-import-metadata-btn"
                  href={setBaseUrl(
                    `projects/${project.identifier}/sample-metadata/upload/file`
                  )}
                  tour="tour-import"
                  icon={<IconCloudUpload />}
                >
                  {i18n("linelist.importBtn.text")}
                </Button>
              </Form.Item>
            ) : null}
            <Form.Item>
              <Search
                tour="tour-search"
                onKeyUp={(e) => this.props.updateFilter(e.target.value)}
                id="js-table-filter"
                className="table-filter t-table-filter"
                style={{
                  width: 200,
                }}
              />
            </Form.Item>
            <Form.Item>
              <Suspense fallback={<span />}>
                {this.state.tourOpen ? (
                  <LineListTour
                    isOpen={this.state.tourOpen}
                    closeTour={this.closeTour}
                  />
                ) : null}
              </Suspense>
              <Popover
                content={
                  <strong
                    style={{
                      borderBottom: "2px solid orange",
                      cursor: "pointer",
                    }}
                    onClick={this.closePopover}
                  >
                    {i18n("linelist.tour.popover")}
                  </strong>
                }
                visible={this.state.showTourPopover}
                placement="topLeft"
                arrowPointAtCenter
              >
                <Button
                  title={i18n("linelist.tour.title")}
                  className="js-tour-button t-tour-button tour-button"
                  shape="circle"
                  onClick={this.openTour}
                >
                  <IconQuestion />
                </Button>
              </Popover>
            </Form.Item>
          </Form>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state) => ({});

const mapDispatchToProps = (dispatch) => ({
  updateFilter: (value) => dispatch(entryActions.setGlobalFilter(value)),
});

export const Toolbar = connect(
  mapStateToProps,
  mapDispatchToProps
)(ToolbarComponent);
