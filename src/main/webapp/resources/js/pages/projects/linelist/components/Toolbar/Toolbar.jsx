import React, { Component, Suspense } from "react";
import PropTypes from "prop-types";
import { ExportDropDown } from "../Export/ExportDropdown";
import { AddSamplesToCartButton } from "../AddToCartButton/AddSamplesToCart";
import { Button, Form, Input, Popover } from "antd";

const LineListTour = React.lazy(() => import("../Tour/LineListTour"));

const { Search } = Input;

const { i18n, urls } = window.PAGE;

export class Toolbar extends Component {
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
        <div className="toolbar-group">
          <Form layout="inline">
            <Form.Item>
              <ExportDropDown
                csv={this.props.exportCSV}
                excel={this.props.exportXLSX}
              />
            </Form.Item>
            <Form.Item>
              <AddSamplesToCartButton
                selectedCount={this.props.selectedCount}
                addSamplesToCart={this.props.addSamplesToCart}
              />
            </Form.Item>
          </Form>
        </div>
        <div className="toolbar-group">
          <Form layout="inline">
            <Form.Item>
              <Button href={urls.import} tour="tour-import">
                <i
                  className="fas fa-cloud-upload-alt spaced-right__sm"
                  aria-hidden="true"
                />
                {i18n.linelist.importBtn.text}
              </Button>
            </Form.Item>
            <Form.Item>
              <Search
                tour="tour-search"
                onKeyUp={e => this.props.quickSearch(e.target.value)}
                id="js-table-filter"
                className="table-filter t-table-filter"
                style={{
                  width: 200
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
                      cursor: "pointer"
                    }}
                    onClick={this.closePopover}
                  >
                    {i18n.linelist.tour.popover}
                  </strong>
                }
                visible={this.state.showTourPopover}
                placement="topLeft"
                arrowPointAtCenter
              >
                <Button
                  title={i18n.linelist.tour.title}
                  className="js-tour-button t-tour-button tour-button"
                  shape="circle"
                  icon="question"
                  onClick={this.openTour}
                />
              </Popover>
            </Form.Item>
          </Form>
        </div>
      </div>
    );
  }
}

Toolbar.propTypes = {
  selectedCount: PropTypes.number.isRequired,
  exportCSV: PropTypes.func.isRequired,
  exportXLSX: PropTypes.func.isRequired,
  addSamplesToCart: PropTypes.func.isRequired,
  quickSearch: PropTypes.func.isRequired,
  scrollTableToTop: PropTypes.func.isRequired
};
