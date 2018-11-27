import React from "react";

export default class Cart extends React.Component {
  static propTypes = {};

  componentDidMount() {}

  render() {
    return (
      <p>
        Cart Has STuff: <strong>{this.props.total} in fact</strong>
      </p>
    );
  }
}
