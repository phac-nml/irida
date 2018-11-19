import React, { Component } from "react";
import { render } from "react-dom";
import { getCart } from "../../apis/cart/cart";
import { List, Skeleton } from "antd";

class CartProject extends React.Component {
  state = { loading: true };

  render() {
    const { loading } = this.state;
    const { id, label } = this.props.project;

    return (
      <List.Item key={id} actions={[<a>Remove</a>]}>
        <List.Item.Meta
          title={label}
          description={<Skeleton active={true} title={false} />}
        />
      </List.Item>
    );
  }
}

class Cart extends Component {
  state = { projects: [] };
  componentDidMount() {
    getCart().then(data => {
      this.setState({ projects: data.projects });
    });
  }

  render() {
    const { projects } = this.state;
    return (
      <List>
        {projects.map(project => (
          <CartProject key={project.id} project={project} />
        ))}
      </List>
    );
  }
}

render(<Cart />, document.querySelector("#root"));
