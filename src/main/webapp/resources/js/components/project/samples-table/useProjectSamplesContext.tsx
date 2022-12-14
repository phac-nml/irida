import React, { createContext, ReactNode, useContext, useReducer } from "react";
import { TableOptions } from "../../../types/ant-design";
import {
  FilterValue,
  SorterResult,
  TablePaginationConfig,
} from "antd/es/table/interface";
import { ProjectSample } from "../../../redux/endpoints/project-samples";
import { formatSearch, formatSort } from "../../../utilities/table-utilities";
import isEqual from "lodash/isEqual";

const INITIAL_TABLE_OPTIONS: TableOptions = {
  filters: {},
  pagination: {
    current: 1,
    pageSize: 10,
  },
  order: [{ property: "sample.modifiedDate", direction: "desc" }],
  search: [],
};

type UpdateTablePayload = {
  pagination: TablePaginationConfig;
  filters: Record<string, FilterValue | null>;
  sorter: SorterResult<ProjectSample> | SorterResult<ProjectSample>[];
};

type Action = {
  type: `tableUpdate`;
  payload: UpdateTablePayload;
};
type Dispatch = (action: Action) => void;
type State = {
  options: TableOptions;
  selection: {
    selected: Record<string, ProjectSample>;
    count: number;
  };
};
type ProjectSamplesContextProps = { children: ReactNode };

const ProjectSamplesContext = createContext<
  | {
      state: State;
      dispatch: Dispatch;
    }
  | undefined
>(undefined);

function formatTableOptions(
  { filters: tableFilters, pagination, sorter }: UpdateTablePayload,
  state: State
): State {
  const { associated, ...searchOptions } = tableFilters;
  const filters =
    associated === undefined
      ? undefined
      : { associated: associated as FilterValue };
  const search = formatSearch(searchOptions);

  const options: TableOptions = {
    filters,
    pagination,
    order: formatSort(sorter),
    search,
  };

  if (
    isEqual(search, state.options.search) &&
    isEqual(tableFilters, state.options.filters)
  ) {
    console.info("NOT JUST PAGINATION");
    // Just a pagination change
    return {
      ...state,
      options,
    };
  }
  return { ...state, options, selection: { selected: {}, count: 0 } };
}

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "tableUpdate":
      return formatTableOptions(action.payload, state);
    default: {
      throw new Error(`Unhandled action type: ${action.type}`);
    }
  }
}

function ProjectSamplesProvider({
  children,
}: ProjectSamplesContextProps): JSX.Element {
  const [state, dispatch] = useReducer(reducer, {
    options: INITIAL_TABLE_OPTIONS,
    selection: {
      count: 0,
      selected: {},
    },
  });

  return (
    <ProjectSamplesContext.Provider value={{ state, dispatch }}>
      {children}
    </ProjectSamplesContext.Provider>
  );
}

function useProjectSamples() {
  const context = useContext(ProjectSamplesContext);
  if (context === undefined) {
    throw new Error(
      "useProjectSamples must be used within a ProjectSamplesProvider"
    );
  }
  return context;
}

export { ProjectSamplesProvider, useProjectSamples };
