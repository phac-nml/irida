import React from "react";
import { fetchTemplates } from "../apis/metadata/templates";
import { FIELDS, TYPES } from "../pages/projects/linelist/constants";
import { Icon } from "antd";
import { formatDate, isDate } from "../utilities/date-utilities";
import { getTextSearchProps } from "../pages/projects/linelist/components/LineListTable/filters/TextFilter";
import { getDateSearchProps } from "../pages/projects/linelist/components/LineListTable/filters/dateFilter";

let MetadataTemplatesContext;
const {
  Provider,
  Consumer
} = (MetadataTemplatesContext = React.createContext());

class MetadataTemplatesProvider extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      width: 0,
      loading: true,
      current: 0,
      templates: undefined,
      getCurrentTemplate: this.getCurrentTemplate
    };
  }

  componentDidMount() {
    fetchTemplates(4).then(({ data }) => {
      this.setState({ templates: data, loading: false });
    });
  }

  getCurrentTemplate = () => {
    function dateSorter(d1, d2) {
      if (typeof d1 === "undefined" || !isDate(d1)) {
        return 1;
      } else if (typeof d2 === "undefined" || !isDate(d2)) {
        return -1;
      } else {
        return new Date(d2) - new Date(d1);
      }
    }

    const getColumnDefinition = ({ field, headerName, type }) => {
      const f = {
        key: field,
        title: <div style={{ whiteSpace: "nowrap" }}>{headerName || ""}</div>,
        dataIndex: field,
        sorter: (a, b) => {
          const af = a[field];
          const bf = b[field];
          if (!af && !bf) return 0;
          if (!af) return -1;
          if (!bf) return 1;
          return af.toLowerCase() < bf.toLowerCase();
        },
        width: 150
      };

      switch (type) {
        case TYPES.date:
          Object.assign(f, {
            render: text => formatDate({ date: text }),
            sorter: (a, b) => dateSorter(a[field], b[field]),
            width: 200,
            ...getDateSearchProps(field)
          });
          break;
        case TYPES.text:
          Object.assign(f, {
            ...getTextSearchProps(field)
          });
      }

      switch (field) {
        case FIELDS.sampleName:
          Object.assign(f, {
            fixed: "left",
            render: (text, data) => (
              <a
                target="_blank"
                rel="noopener noreferrer"
                className="t-sample-name"
                href={`${window.TL.BASE_URL}projects/${Number(
                  data[FIELDS.projectId]
                )}/samples/${Number(data[FIELDS.sampleId])}`}
              >
                {text}
              </a>
            )
          });
          break;
        case FIELDS.icons:
          Object.assign(f, {
            render: (text, data) => (
              <>
                {!JSON.parse(data.owner) ? (
                  <Icon type="lock" theme="twoTone" />
                ) : null}
              </>
            ),
            sorter: false,
            fixed: "left",
            width: 50
          });
          break;
        case FIELDS.modifiedDate:
          Object.assign(f, { defaultSortOrder: "ascend" });
          break;
      }

      return f;
    };

    if (this.state.templates) {
      const { fields } = this.state.templates[this.state.current];
      return fields.map(getColumnDefinition);
    }

    return undefined;
  };

  render() {
    return <Provider value={this.state}>{this.props.children}</Provider>;
  }
}

export {
  MetadataTemplatesContext,
  MetadataTemplatesProvider,
  Consumer as MetadataTemplatesConsumer
};
