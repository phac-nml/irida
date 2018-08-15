import React, { Component } from "react";
import PropTypes from "prop-types";
import { ExportDropDown } from "../Export/ExportDropdown";
import { AddSamplesToCartButton } from "../AddToCartButton/AddSamplesToCart";
import { Button, Form, Input } from "antd";
import LineListTour from "../Tour/LineListTour";

const { Search } = Input;

const { i18n, urls } = window.PAGE;

export class Toolbar extends Component {
  state = { tourOpen: false };

  openTour = () => this.setState({ tourOpen: true });

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
              <LineListTour isOpen={this.state.tourOpen}
                            closeTour={this.closeTour}/>
              <Button shape="circle" icon="question" onClick={this.openTour}/>
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
