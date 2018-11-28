import React from "react";
import PropTypes from "prop-types";
import { List, Input, Tag } from "antd";

export default class Cart extends React.Component {
  static propTypes = {
    total: PropTypes.number.isRequired
  };

  state = {
    samples: [
      {
        id: 1,
        label: "FOOBAR",
        projects: [{ label: "SUPER PROJECT", id: 133 }]
      },
      {
        id: 2,
        label: "SNAFU",
        projects: [
          { label: "SUPER PROJECT", id: 133 },
          { label: "CRAPPY PROJECT", id: 134 }
        ]
      }
    ]
  };

  componentDidMount() {
    console.log(this.props);
  }

  renderSample = sample => {
    return (
      <List.Item actions={[<a href="#">Remove</a>]}>
        <List.Item.Meta
          title={sample.label}
          description={this.renderProjects(sample.projects)}
        />
      </List.Item>
    );
  };

  renderProjects = projects => {
    return (
      <div>
        {projects.map(p => (
          <Tag color="green" closable key={p.id}>
            {p.label}
          </Tag>
        ))}
      </div>
    );
  };

  render() {
    return (
      <div style={{height: "100%"}}>
        <div style={{padding: 4}}><Input.Search
          placeholder="sample search"
          onSearch={value => console.log(value)}
          enterButton
        /></div>
        <List
        dataSource={this.state.samples}
        renderItem={this.renderSample}
      /></div>
    );
  }
}
