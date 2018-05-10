import React from "react";
import PropTypes from "prop-types";
import ImmutablePropTypes from "react-immutable-proptypes";
import { Tag, Select } from "antd";
import {
  MODIFIED_SELECT_INDEX,
  NO_TEMPLATE_INDEX
} from "../../reducers/templates";

const { Option } = Select;
const { i18n } = window.PAGE;

export class TemplateSelect extends React.Component {
  constructor(props) {
    super(props);
  }

  templateSelected = index => {
    if (this.props.current !== index) {
      this.props.useTemplate(index);
    }
  };

  render() {
    const templates = this.props.templates.toJS();

    return templates.length > 0 ? (
      <React.Fragment>
        <Select
          disabled={templates.length === 0}
          value={this.props.current}
          style={{ width: 250 }}
          onSelect={this.templateSelected}
        >
          {this.props.modified !== null ? (
            <Option value={MODIFIED_SELECT_INDEX}>
              {i18n.linelist.templates.Select.modified}
            </Option>
          ) : null}
          <Option value={NO_TEMPLATE_INDEX} title="_NONE_">
            {i18n.linelist.templates.Select.none}
          </Option>
          {templates.map((t, index) => (
            <Option key={t.id} value={index}>
              <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span
                style={{
                  maxWidth: 170,
                  overflow: "hidden",
                  textOverflow: "ellipsis"
                }}
              >
                {t.name}
              </span>{" "}
                <Tag>{t.fields.length}</Tag>
              </div>
            </Option>
          ))}
        </Select>
      </React.Fragment>
    ) : null;
  }
}

TemplateSelect.propTypes = {
  current: PropTypes.number.isRequired,
  modified: PropTypes.object,
  templates: ImmutablePropTypes.list.isRequired,
  useTemplate: PropTypes.func.isRequired
};
