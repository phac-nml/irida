import React from "react";
import { fetchMetadataEntries } from "../apis/metadata/entry";

let MetadataEntriesContext;
const { Provider, Consumer } = (MetadataEntriesContext = React.createContext());

class MetadataEntriesProvider extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedRowKeys: [],
      entries: undefined,
      loading: true,
      onSelectedRowChange: this.onSelectedRowChange,
      onSelectAll: this.onSelectAll,
      onSelectNone: this.onSelectNone
    };
  }

  componentDidMount() {
    fetchMetadataEntries(4).then(({ data }) =>
      this.setState({ entries: data, loading: false })
    );
  }

  onSelectAll = () =>
    this.setState({
      selectedRowKeys: this.state.entries.map(e => e["irida-static-sample-id"])
    });

  onSelectNone = () => this.setState({ selectedRowKeys: [] });

  onSelectedRowChange = selectedRowKeys => this.setState({ selectedRowKeys });

  render() {
    return <Provider value={this.state}>{this.props.children}</Provider>;
  }
}

export {
  MetadataEntriesContext,
  MetadataEntriesProvider,
  Consumer as MetadataEntriesConsumer
};
