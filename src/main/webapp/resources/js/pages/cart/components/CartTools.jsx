import React from "react";
import { Row } from "antd";
import { spacing } from "../../../styles";

export default class CartTools extends React.Component {
  static propTypes = {};

  render() {
    return (
      <Row type="flex" style={{height: "100%", padding: spacing.DEFAULT, backgroundColor: "white"}}>
        <div>LET'S BUILD SOME TOOLS</div>
      </Row>
    );
  }
}
