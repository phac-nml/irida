import React, { createContext, ReactNode, useContext, useReducer } from "react";
import {
  TableFilters,
  TableOptions,
  TableSortOrder,
} from "../../../types/ant-design";
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
  { filters, pagination, sorter }: UpdateTablePayload,
  state: State
): State {
  console.log(filters);
  const { associated, ...searchOptions } = filters;
  const tableFilters =
    associated === undefined
      ? undefined
      : { associated: associated as FilterValue };
  const search = formatSearch(searchOptions);

  if (
    !(
      isEqual(search, state.options.search) &&
      isEqual(tableFilters, state.options.filters)
    )
  ) {
    // Not just a pagination change
    return { ...state, selection: { selected: {}, count: 0 } };
  }
  return {
    ...state,
    options: {
      pagination,
      filters: tableFilters,
      search,
      order: formatSort(sorter),
    },
  };
}

function reducer(state: State, action: Action) {
  switch (action.type) {
    case "tableUpdate":
      return formatTableOptions(action.payload, state);
    default: {
      throw new Error(`Unhandled action type: ${action.type}`);
    }
  }
  return state;
}

function ProjectSamplesProvider({
  children,
}: ProjectSamplesContextProps): JSX.Element {
  const [state, dispatch] = useReducer(reducer, {
    options: INITIAL_TABLE_OPTIONS,
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
