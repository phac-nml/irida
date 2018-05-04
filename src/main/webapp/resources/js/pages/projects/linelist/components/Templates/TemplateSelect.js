import React from "react";
import ImmutablePropTypes from "react-immutable-proptypes";
import PropTypes from "prop-types";
import { Select } from "antd";
import {
  MODIFIED_SELECT_INDEX,
  NO_TEMPLATE_INDEX
} from "../../reducers/template";

const { Option } = Select;

export class TemplateSelect extends React.Component {
  constructor(props) {
    super(props);
  }

  templateSelected = id => {
    if (this.props.current !== id) {
      this.props.fetchTemplate(id);
    }
  };

  render() {
    const templates = this.props.templates.toJS();

    return templates.length > 0 ? (
      <Select
        value={this.props.current}
        style={{ width: 250 }}
        onSelect={this.templateSelected}
      >
        {this.props.modified ? (
          <Option value={MODIFIED_SELECT_INDEX}>_Modified_</Option>
        ) : null}
        <Option value={NO_TEMPLATE_INDEX} title="_NONE_">
          _None_
        </Option>
        {templates.map(t => (
          <Option key={t.id} value={t.id} title={t.label}>
            {t.label}
          </Option>
        ))}
      </Select>
    ) : null;
  }
}

TemplateSelect.propTypes = {
  modified: PropTypes.bool.isRequired,
  templates: ImmutablePropTypes.list.isRequired,
  fetchTemplate: PropTypes.func.isRequired
};
