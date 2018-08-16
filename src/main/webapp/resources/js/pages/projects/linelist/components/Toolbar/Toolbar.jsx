import React, { Component } from "react";
import PropTypes from "prop-types";
import { ExportDropDown } from "../Export/ExportDropdown";
import { AddSamplesToCartButton } from "../AddToCartButton/AddSamplesToCart";
import { Button, Form, Input, Popover } from "antd";
import LineListTour from "../Tour/LineListTour";

const { Search } = Input;

const { i18n, urls } = window.PAGE;

export class Toolbar extends Component {
  state = { tourOpen: false, showTourPopover: false };
  openTour = () => this.setState({ tourOpen: true, showTourPopover: false });

  componentDidMount() {
    if (typeof window.localStorage === "object") {
      if (!window.localStorage.getItem("linelist-tour")) {
        window.localStorage.setItem("linelist-tour", "complete");
        this.setState({ showTourPopover: true });
      }
    }
  }

  closeTour = () => this.setState({ tourOpen: false });

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
              <LineListTour
                isOpen={this.state.tourOpen}
                closeTour={this.closeTour}
              />
              <Popover
                content={
                  <strong style={{ borderBottom: "2px solid orange" }}>
                    {i18n.linelist.tour.popover}
                  </strong>
                }
                visible={this.state.showTourPopover}
                placement="topLeft"
                arrowPointAtCenter
              >
                <Button
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
  quickSearch: PropTypes.func.isRequired
};
