import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import { Loader } from "./Loader";

/**
 * Container class for the higher level states of the page:
 * 1. Loading
 * 2. Table
 * 3. Loading error.
 */
export class LineList extends React.Component {
  constructor(props) {
    super(props);
  }
  render() {
    return this.props.initializing ? (
      <Loader />
    ) : (
      <h3>
        Are You Ready to Make Some Tables?{" "}
        <small>Well, you need to wait until the next merge request 😎</small>
      </h3>
    );
  }
}

LineList.propTypes = {
  initializing: PropTypes.bool.isRequired
};

/*
Default react-redux boiler plate to connect the current state of the
application to the component. When the state of the application gets
updated (in this case so far it is the loading state), the this connect
method is what triggers the updates.
 */
const mapStateToProps = state => ({
  initializing: state.fields.initializing,
  error: state.fields.error
});

const mapDispatchToProps = dispatch => ({});

export default connect(mapStateToProps, mapDispatchToProps)(LineList);
