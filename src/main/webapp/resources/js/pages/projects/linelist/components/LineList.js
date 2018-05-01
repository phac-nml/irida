import React from "react";
import PropTypes from "prop-types";
import { connect } from "react-redux";
import { Loader } from "./Loader";
import { Table } from "./Table";

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
    if (this.props.initializing) {
      return <Loader />;
    } else if (this.props.error) {
      // ERROR STATE
      // TODO: (Josh | 2018-04-11) Create error component
      return <h3>A major error has occurred! Better find a 💣 shelter!</h3>;
    } else {
      // CREATE TABLE
      return <Table />;
    }
  }
}

LineList.propTypes = {
  initializing: PropTypes.bool.isRequired,
  error: PropTypes.bool
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
