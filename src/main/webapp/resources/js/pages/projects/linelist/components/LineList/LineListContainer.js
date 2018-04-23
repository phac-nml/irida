import { connect } from "react-redux";
import { LineList } from "./LineList";

/*
Default react-redux boiler plate to connect the current state of the
application to the component. When the state of the application gets
updated (in this case so far it is the loading state), the this connect
method is what triggers the updates.
 */
const mapStateToProps = state => ({
  loading: state.fields.fetching,
  error: state.fields.error
});

const mapDispatchToProps = dispatch => ({});

export const LineListContainer = connect(mapStateToProps, mapDispatchToProps)(
  LineList
);
