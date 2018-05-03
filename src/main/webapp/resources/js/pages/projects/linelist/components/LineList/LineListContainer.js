import React from "react";
import { connect } from "react-redux";
import { LineList } from "./LineList";

class LineListContainer extends React.Component {
  constructor(props) {
    super(props);
  }

  componentDidCatch(error, info) {
    // TODO: This will fire if an error is thrown in the children
  }

  render() {
    return <LineList {...this.props} />;
  }
}

/*
Default react-redux boiler plate to connect the current state of the
application to the component. When the state of the application gets
updated (in this case so far it is the loading state), the this connect
method is what triggers the updates.
 */
const mapStateToProps = state => ({
  initializing: state.fields.get("initializing"),
  error: state.fields.get("error")
});

const mapDispatchToProps = dispatch => ({});

export default connect(mapStateToProps, mapDispatchToProps)(LineListContainer);
